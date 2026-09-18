package com.airbnb.project.services;

import com.airbnb.project.dtos.BookingDTO;
import com.airbnb.project.dtos.BookingRequest;
import com.airbnb.project.dtos.GuestDTO;
import com.airbnb.project.entities.*;
import com.airbnb.project.entities.enums.BookingStatus;
import com.airbnb.project.exceptions.ResourceNotFound;
import com.airbnb.project.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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

        for(Inventory inventory:list){
            inventory.setReservedCount(inventory.getReservedCount()+ bookingRequest.getRoomCount());
            log.info("Room Count: "+inventory.getBookedCount());
        }

        inventoryRepository.saveAll(list);
        log.info("Inventory saved ");
        // Create the Booking


        log.info("Booking for booking request {}",bookingRequest);
        Booking  booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequest.getRoomCount())
                .amount(BigDecimal.TEN)
                .build();

        log.info("Booking check-in: {}", booking.getCheckInDate());
        log.info("Booking check-out: {}", booking.getCheckOutDate());

        bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDTO.class);
    }

    @Override
    public BookingDTO addGuests(Long bookingId, List<GuestDTO> guestDTOList) {
        log.info("Adding Guest for Booking Id: {}",bookingId);
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(()->new ResourceNotFound("Booking not found with {}"+bookingId));
        log.info("Checking Booking expired or not");
        if(hasBookingExpired(booking))
        {
            throw  new IllegalStateException("Booking has expired");
        }

        if(booking.getBookingStatus() != BookingStatus.RESERVED)
            throw new IllegalStateException("Booking Status is Reserved");

        for(GuestDTO guestDTO:guestDTOList){
            Guest  guest = modelMapper.map(guestDTO,Guest.class);
            guest.setUser(getCurrentUser());
            guestRepository.save(guest);
            booking.getGuests().add(guest);
        }

        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDTO.class);


    }

    public Boolean hasBookingExpired(Booking booking){
        return booking.getCreated().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    private User getCurrentUser()
    {
        return new User(1L,"abc@gmail.com","pass","amar", Set.of());
    }
}
