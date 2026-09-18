package com.airbnb.project.controller;

import com.airbnb.project.dtos.HotelDTO;
import com.airbnb.project.dtos.HotelInfoDTO;
import com.airbnb.project.dtos.HotelSearchRequest;
import com.airbnb.project.entities.Hotel;
import com.airbnb.project.services.HotelServices;
import com.airbnb.project.services.InventoryServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hotels")
public class HotelBrowseController {

    private final InventoryServices inventoryServices;
    private final HotelServices hotelServices;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelDTO>> searchHotels(@RequestBody HotelSearchRequest hotelSearchRequest){
        Page<HotelDTO> page = inventoryServices.search(hotelSearchRequest);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelInfoDTO> getHotelInfo(@PathVariable("hotelId") Long hotelId){
        return  ResponseEntity.ok(hotelServices.getHotelInfoById(hotelId));
    }
}

