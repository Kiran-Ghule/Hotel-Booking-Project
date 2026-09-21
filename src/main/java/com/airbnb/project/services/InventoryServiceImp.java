package com.airbnb.project.services;

import com.airbnb.project.dtos.HotelDTO;
import com.airbnb.project.dtos.HotelPriceDTO;
import com.airbnb.project.dtos.HotelSearchRequest;
import com.airbnb.project.entities.Inventory;
import com.airbnb.project.entities.Room;
import com.airbnb.project.repositories.HotelMinPriceRepository;
import com.airbnb.project.repositories.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImp implements InventoryServices {

    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final HotelMinPriceRepository hotelMinPriceRepository;

    @Override
    public void initializeRoomForYear(Room room) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);
        for(;!today.isAfter(endDate); today = today.plusDays(1))
        {
            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .city(room.getHotel().getCity())
                    .date(today)
                    .reservedCount(0)
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();
            inventoryRepository.save(inventory);

        }

    }

    @Override
    public void deleteAllInventory(Room room) {
        log.info("Delete all inventory");
        LocalDate today = LocalDate.now();
        inventoryRepository.deleteByRoom( room);


    }

    @Override
    public Page<HotelPriceDTO> search(HotelSearchRequest hotelSearchRequest) {
        log.info("searching for hotels with {}", hotelSearchRequest);
        long dateCount = ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(),hotelSearchRequest.getEndDate())+1;


        //For 90 Dayas
        Pageable pageable = PageRequest.of(hotelSearchRequest.getPage(), hotelSearchRequest.getPageSize());
        return hotelMinPriceRepository.findHotelsWithAvailableInventory(
                hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate(),
                hotelSearchRequest.getRoomCount(),
                dateCount,
                pageable
        );
    }
}
