package com.airbnb.project.services;

import com.airbnb.project.dtos.RoomDTO;
import com.airbnb.project.entities.Hotel;
import com.airbnb.project.entities.Room;
import com.airbnb.project.exceptions.ResourceNotFound;
import com.airbnb.project.repositories.HotelRepository;
import com.airbnb.project.repositories.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImp implements RoomService {

    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;
    private final HotelRepository hotelRepository;
    private final InventoryServices inventoryServices;

    @Override
    public RoomDTO createRoom(Long hotelId,RoomDTO roomDTO) {
        log.info("Check Hotel Exist or not...");
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(()-> new ResourceNotFound("Hotel with hotelId " + hotelId));

        log.info("Creating Room for Hotel Id:{}",hotelId);
        Room room = modelMapper.map(roomDTO, Room.class);
        room.setHotel(hotel);

        room=roomRepository.save(room);
        log.info("Room with roomDTO {} has been created", roomDTO.toString());

        if(hotel.getActive())
            inventoryServices.initializeRoomForYear(room);

        return modelMapper.map(room, RoomDTO.class);
    }

    @Override
    public List<RoomDTO> findAllRoomsByHotelId(Long hotelId) {
        log.info("Check Hotel Exist or not...");
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(()-> new ResourceNotFound("Hotel with hotelId " + hotelId));
        log.info("Finding all Rooms for Hotel Id:{}",hotelId);
        List<RoomDTO> roomsList = hotel.getRooms()
                .stream()
                .map(room -> modelMapper.map(room,RoomDTO.class))
                .toList();
        return  roomsList;
    }

    @Override
    public RoomDTO findRoomById(Long roomId) {
        log.info("Finding Room with roomId:{}",roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(()-> new ResourceNotFound("Room with roomId " + roomId));
        log.info("Room with roomId:{} has been found",roomId);
        return modelMapper.map(room,RoomDTO.class);
    }

    @Override
    @Transactional
    public void deleteRoom(Long roomId) {
        log.info("Deleting Room with roomId:{}",roomId);
       Room room = roomRepository
               .findById(roomId)
               .orElseThrow(()-> new ResourceNotFound("Room with roomId " + roomId));

       log.info("Room with roomId:{} has been deleted",roomId);
        inventoryServices.deleteAllInventory(room);
       roomRepository.deleteById(roomId);


    }
}
