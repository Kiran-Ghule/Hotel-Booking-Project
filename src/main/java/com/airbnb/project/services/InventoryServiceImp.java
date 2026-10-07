package com.airbnb.project.services;

import com.airbnb.project.dtos.*;
import com.airbnb.project.entities.Inventory;
import com.airbnb.project.entities.Room;
import com.airbnb.project.entities.User;
import com.airbnb.project.exceptions.ResourceNotFound;
import com.airbnb.project.exceptions.UnAuthorisedException;
import com.airbnb.project.repositories.HotelMinPriceRepository;
import com.airbnb.project.repositories.InventoryRepository;
import com.airbnb.project.repositories.RoomRepository;
import jakarta.transaction.Transactional;
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
import java.util.List;
import java.util.stream.Collectors;

import static com.airbnb.project.utils.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImp implements InventoryServices {

    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final RoomRepository roomRepository;

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

    @Override
    public List<InventoryDTO> getAllInventoryByRoom(Long roomId) {

        log.info("getting all inventory for room {}", roomId);
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new ResourceNotFound("Room with id " + roomId + " not found")
        );

        User user = getCurrentUser();

        if(!user.equals(room.getHotel().getOwner()))
            throw new UnAuthorisedException("Current User is not Owner of this Room's hotel ");

        return inventoryRepository.findByRoomOrderByDate(room).stream()
                .map(inv -> modelMapper.map(inv, InventoryDTO.class))
                .toList();

    }

    @Override
    @Transactional
    public void updateInventory(Long roomId, UpdateInventoryRequestDTO updateInventoryRequestDTO) {
        log.info("updating inventory for room {}", roomId);
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new ResourceNotFound("Room with id " + roomId + " not found")
        );

        User user = getCurrentUser();

        if(!user.equals(room.getHotel().getOwner()))
            throw new UnAuthorisedException("Current User is not Owner of this Room's hotel ");

        inventoryRepository.getInventoryAndLockBeforeUpdate(roomId,updateInventoryRequestDTO.getStartTime(),updateInventoryRequestDTO.getEndTime());

       inventoryRepository.updateInventory(roomId,updateInventoryRequestDTO.getStartTime(),
                updateInventoryRequestDTO.getEndTime(),updateInventoryRequestDTO.getClosed(),
                updateInventoryRequestDTO.getSurgeFactor());

    }


}
