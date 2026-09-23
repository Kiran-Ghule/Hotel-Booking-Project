package com.airbnb.project.controller;

import com.airbnb.project.dtos.RoomDTO;
import com.airbnb.project.entities.Room;
import com.airbnb.project.services.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.IdGeneratorType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/hotels/{hotelId}/rooms")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomDTO> createNewRoom(@PathVariable Long hotelId, @RequestBody RoomDTO room) {
        RoomDTO  roomDTO = roomService.createRoom(hotelId, room);
        return new ResponseEntity<>(roomDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllRooms(@PathVariable Long hotelId) {
        return new ResponseEntity<>(roomService.findAllRoomsByHotelId(hotelId), HttpStatus.OK);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDTO> getRoom(@PathVariable Long roomId) {
        return new ResponseEntity<>(roomService.findRoomById(roomId), HttpStatus.FOUND);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<RoomDTO> deleteRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
