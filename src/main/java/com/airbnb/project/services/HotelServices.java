package com.airbnb.project.services;

import com.airbnb.project.dtos.HotelDTO;
import com.airbnb.project.dtos.HotelInfoDTO;
import com.airbnb.project.entities.Hotel;

public interface HotelServices {
    HotelDTO createHotel(HotelDTO hotel);
    HotelDTO getHotelById(Long hotelId);

    HotelDTO updateHotel(Long id,HotelDTO hotelDTO);

    Boolean deleteHotelById(Long hotelId);

    void ActivateHotel(Long hotelId);

    HotelInfoDTO getHotelInfoById(Long hotelId);
}
