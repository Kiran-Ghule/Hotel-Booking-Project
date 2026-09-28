package com.airbnb.project.services;

import com.airbnb.project.dtos.BookingDTO;
import com.airbnb.project.dtos.BookingRequest;
import com.airbnb.project.dtos.GuestDTO;
import com.airbnb.project.entities.*;
import com.airbnb.project.entities.enums.BookingStatus;
import com.airbnb.project.exceptions.ResourceNotFound;
import com.airbnb.project.exceptions.UnAuthorisedException;
import com.airbnb.project.repositories.*;
import com.airbnb.project.strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final GuestRepository guestRepository;
    private final CheckOutService  checkOutService;
    private final PricingService pricingService;


    @Value("${frontend.url}")
    private  String FrontEndUrl ;

    @Override
    @Transactional
    public BookingDTO initialiseBooking(BookingRequest bookingRequest) {
        log.info("BookingServiceImp initialiseBooking for hotel : {}, room: {}, data {}-{}",bookingRequest.getHotelId(),
                bookingRequest.getRoomId(),bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate());
        Hotel hotel = hotelRepository
                .findById( bookingRequest.getHotelId() )
                .orElseThrow(()->new ResourceNotFound("Hotel not found with {}"+bookingRequest.getHotelId()));

        Room room = roomRepository
                .findById(bookingRequest.getRoomId())
                .orElseThrow(()->new ResourceNotFound("Room not found with {}"+bookingRequest.getRoomId()));


        List<Inventory> list  =    inventoryRepository.findAndLockAvailableInventory(room.getId(),
                bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate(), bookingRequest.getRoomCount());

        log.info("list : with size {}",list.size());
        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate());

        if( list.size() < daysCount){
            throw new IllegalStateException("No inventory Available with "+bookingRequest.getRoomId());
        }

        // TODO : Reserve the Room and Update Book Count

        inventoryRepository.initBooking(room.getId(),bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate(),bookingRequest.getRoomCount());
        log.info("Inventory saved ");


        BigDecimal priceForOneRoom = pricingService.calculateTotalPrice(list);
        BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(bookingRequest.getRoomCount()));
        log.info("Booking for booking request {}",bookingRequest);


        Booking  booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequest.getRoomCount())
                .amount(totalPrice)
                .build();

        log.info("Booking check-in: {}", booking.getCheckInDate());
        log.info("Booking check-out: {}", booking.getCheckOutDate());

        bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDTO.class);
    }

    @Override
    @Transactional
    public BookingDTO addGuests(Long bookingId, List<GuestDTO> guestDTOList) {
        log.info("Adding Guest for Booking Id: {}",bookingId);
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(()->new ResourceNotFound("Booking not found with {}"+bookingId));
        log.info("Checking Booking expired or not");

        User user = getCurrentUser();

        if(!user.equals(booking.getUser())){
            throw new UnAuthorisedException("Booking Doest not belong to User as Id : "+user.getId());
        }

        if(hasBookingExpired(booking))
        {
            throw  new IllegalStateException("Booking has expired");
        }

        if(booking.getBookingStatus() != BookingStatus.RESERVED)
            throw new IllegalStateException("Booking Status is Reserved");

        for(GuestDTO guestDTO:guestDTOList){
            Guest  guest = modelMapper.map(guestDTO,Guest.class);
            guest.setUser(user);
            guestRepository.save(guest);
            booking.getGuests().add(guest);
        }

        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDTO.class);


    }

    @Override
    @Transactional
    public String initiatePayment(Long bookingId) {
        log.info("initiatePayment() Called");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()->( new ResourceNotFound("Booking not found with "+bookingId)));

        if(hasBookingExpired(booking))
            throw new RuntimeException("Booking has expired");

        log.info(FrontEndUrl);

        String sessionUrl= checkOutService.getCheckoutSession(booking,FrontEndUrl+"/Success",FrontEndUrl+"/Failure");
        booking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);
        return sessionUrl;

    }

    @Override
    @Transactional
    public void capturePayment(Event event) {
        log.info("CapturePayment has been Hit.....");
        if("checkout.session.completed".equals(event.getType())){
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            log.info("Got session as {}",session.getId());
            if(session==null) return;
                String sessionId = session.getId();
                Booking booking =
                        bookingRepository.findByPaymentSessionId(sessionId).orElseThrow(()->
                                new ResourceNotFound("Booking not Found for sessid "+sessionId));
                log.info("Got booking as {}",booking.getId());
                booking.setBookingStatus(BookingStatus.CONFIRMED);
                bookingRepository.save(booking);
                inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(),booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());
                inventoryRepository.confirmBooking(booking.getRoom().getId(),booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());

                log.info("Booking confirmed successfully for Booking Id: {}",booking.getId());

        }
        else
        {
            log.warn("Unhandled event type:{}",event.getType());

        }

    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(()->new ResourceNotFound("Booking not found with {}"+bookingId));

        User user = getCurrentUser();
        if(!user.equals(booking.getUser()))
        {
            throw new UnAuthorisedException("User is not the owner of the booking");
        }

        if(booking.getBookingStatus() != BookingStatus.CONFIRMED)
        {
            throw new IllegalStateException("Booking Status is not CONFIRMED");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(),booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());
        inventoryRepository.cancelBooking(booking.getRoom().getId(),booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());

        //handling Refund
        try
        {
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundCreateParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();
            Refund.create(refundCreateParams);

        } catch (StripeException e) {
            throw new RuntimeException(e);
        }


    }

    public Boolean hasBookingExpired(Booking booking){
        return booking.getCreated().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    private User getCurrentUser()
    {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
