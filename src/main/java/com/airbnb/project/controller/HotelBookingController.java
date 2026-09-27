package com.airbnb.project.controller;

import com.airbnb.project.dtos.BookingDTO;
import com.airbnb.project.dtos.BookingRequest;
import com.airbnb.project.dtos.GuestDTO;
import com.airbnb.project.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class HotelBookingController {

        private final BookingService bookingService;

        @PostMapping
        public ResponseEntity<BookingDTO> getGuests(@RequestBody BookingRequest bookingRequest) {
            return ResponseEntity.ok(bookingService.initialiseBooking(bookingRequest));
        }

        @PostMapping("/{bookingId}/addGuests")
        public ResponseEntity<BookingDTO> addGuests(@PathVariable Long bookingId, @RequestBody List<GuestDTO> guestDTOList) {
            return ResponseEntity.accepted().body(bookingService.addGuests(bookingId,guestDTOList));
        }

        @PostMapping("/{bookingId}/Payment")
    public ResponseEntity<Map<String,String>> initiatePayment(@PathVariable Long bookingId) {
            String sessitionUrl = bookingService.initiatePayment(bookingId);
            return ResponseEntity.ok(Map.of("sessitionUrl",sessitionUrl));
        }
}
