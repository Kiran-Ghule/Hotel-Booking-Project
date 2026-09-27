package com.airbnb.project.services;

import com.airbnb.project.entities.Booking;

public interface CheckOutService {
    String getCheckoutSession(Booking bookingId, String successUrl, String failureUrl);


}
