package com.airbnb.project.controller;

import com.airbnb.project.dtos.InventoryDTO;
import com.airbnb.project.dtos.RoomDTO;
import com.airbnb.project.dtos.UpdateInventoryRequestDTO;
import com.airbnb.project.repositories.InventoryRepository;
import com.airbnb.project.services.InventoryServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private  final InventoryServices inventoryServices;


    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<List<InventoryDTO>> getAllInventoryByRoom(@PathVariable Long roomId){
        return ResponseEntity.ok(inventoryServices.getAllInventoryByRoom(roomId));
    }

    @PutMapping("/rooms/{roomId}")
    public ResponseEntity<Void> updateInvenotry(@PathVariable Long roomId, @RequestBody UpdateInventoryRequestDTO updateInventoryRequestDTO){
        inventoryServices.updateInventory(roomId, updateInventoryRequestDTO);
        return ResponseEntity.ok().build();
    }




}

