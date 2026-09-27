package com.airbnb.project.services;

import com.airbnb.project.dtos.BookingDTO;
import com.airbnb.project.dtos.BookingRequest;
import com.airbnb.project.dtos.GuestDTO;

import java.util.List;
import java.util.Map;

public interface BookingService {

    BookingDTO initialiseBooking(BookingRequest bookingRequest);

    BookingDTO addGuests(Long bookingId, List<GuestDTO> guestDTOList);

    String initiatePayment(Long bookingId);
}
