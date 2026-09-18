package com.airbnb.project.services;

import com.airbnb.project.dtos.RoomDTO;
import com.airbnb.project.repositories.HotelRepository;
import com.airbnb.project.repositories.RoomRepository;

import java.util.List;


public interface RoomService {
    RoomDTO createRoom(Long HotelId,RoomDTO roomDTO);
    List<RoomDTO> findAllRoomsByHotelId(Long hotelId);
    RoomDTO findRoomById(Long roomId);
    void deleteRoom(Long roomId);
}
