package com.airbnb.project.services;

import com.airbnb.project.dtos.HotelDTO;
import com.airbnb.project.dtos.HotelPriceDTO;
import com.airbnb.project.dtos.HotelSearchRequest;
import com.airbnb.project.entities.Room;
import org.springframework.data.domain.Page;

public interface InventoryServices {

    void initializeRoomForYear(Room room);

    void deleteAllInventory(Room room);

    Page<HotelPriceDTO> search(HotelSearchRequest hotelSearchRequest);
}
