package com.airbnb.project.controller;

import com.airbnb.project.dtos.HotelDTO;
import com.airbnb.project.services.HotelServices;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/admin/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {
    private final HotelServices hotelServices;

    @PostMapping
    public ResponseEntity<HotelDTO> createHotel(@RequestBody HotelDTO hotelDTO) {
        log.info("Attempting to creating hotel {}", hotelDTO);
        HotelDTO createHotelDTO = hotelServices.createHotel(hotelDTO);
        return new  ResponseEntity<>(createHotelDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDTO> getHotelById(@PathVariable Long hotelId) {
        log.info("Attempting to get hotel {}", hotelId);
        HotelDTO hotelDTO = hotelServices.getHotelById(hotelId);
        return new  ResponseEntity<>(hotelDTO, HttpStatus.OK);
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelDTO> updateHotel(@PathVariable Long hotelId,@RequestBody HotelDTO hotelDTO) {
        log.info("Attempting to update hotel {}", hotelDTO);
        HotelDTO hotelDTO1 = hotelServices.updateHotel(hotelId, hotelDTO);
        return new  ResponseEntity<>(hotelDTO1, HttpStatus.OK);
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Boolean> deleteHotel(@PathVariable Long hotelId) {
        log.info("Attempting to delete hotel {}", hotelId);
        Boolean deleted = hotelServices.deleteHotelById(hotelId);
        log.info("Deleted hotel {}", hotelId);
        return ResponseEntity.ok().body(deleted);
    }

    @PatchMapping("/{hotelId}")
    public ResponseEntity<Void> activateHotel(@PathVariable Long hotelId) {
        log.info("Attempting to activate hotel {}", hotelId);
        hotelServices.ActivateHotel(hotelId);
        return ResponseEntity.ok().build();
    }
}
