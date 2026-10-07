package com.airbnb.project.services;

import com.airbnb.project.dtos.HotelPriceDTO;
import com.airbnb.project.dtos.HotelSearchRequest;
import com.airbnb.project.dtos.InventoryDTO;
import com.airbnb.project.dtos.UpdateInventoryRequestDTO;
import com.airbnb.project.entities.Room;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InventoryServices {

    void initializeRoomForYear(Room room);

    void deleteAllInventory(Room room);

    Page<HotelPriceDTO> search(HotelSearchRequest hotelSearchRequest);

    List<InventoryDTO> getAllInventoryByRoom(Long roomId);

    void updateInventory(Long roomId, UpdateInventoryRequestDTO updateInventoryRequestDTO);
}
