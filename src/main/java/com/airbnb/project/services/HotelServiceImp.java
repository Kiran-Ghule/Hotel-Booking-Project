package com.airbnb.project.services;

import com.airbnb.project.dtos.HotelDTO;
import com.airbnb.project.dtos.HotelInfoDTO;
import com.airbnb.project.dtos.RoomDTO;
import com.airbnb.project.entities.Hotel;
import com.airbnb.project.entities.Room;
import com.airbnb.project.entities.User;
import com.airbnb.project.exceptions.ResourceNotFound;
import com.airbnb.project.exceptions.UnAuthorisedException;
import com.airbnb.project.repositories.HotelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class HotelServiceImp implements HotelServices {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryServices inventoryServices;
    private final RoomService roomService;

    @Override
    public HotelDTO createHotel(HotelDTO hotelDTO) {
        log.info("Creating Hotel with hotelDTO {}", hotelDTO.toString());
        Hotel hotelEntity = modelMapper.map(hotelDTO, Hotel.class);
        hotelEntity.setActive(false);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotelEntity.getOwner())){
            throw new UnAuthorisedException("Current User Does not Own this hotel");
        }
        hotelEntity.setOwner(user);

        hotelRepository.save(hotelEntity);
        log.info("Hotel with hotelDTO {} has been created", hotelDTO.toString());
        return modelMapper.map(hotelEntity, HotelDTO.class);

    }

    @Override
    public HotelDTO getHotelById(Long hotelId) {
        log.info("Getting Hotel with hotelId {}", hotelId);
        Hotel hotelEntity = hotelRepository
                .findById(hotelId)
                .orElseThrow(()-> new ResourceNotFound("Hotel with hotelId " + hotelId));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotelEntity.getOwner())){
            throw new UnAuthorisedException("Current User Does not Own this hotel");
        }

        return modelMapper.map(hotelEntity, HotelDTO.class);
    }

    @Override
    public HotelDTO updateHotel(Long id, HotelDTO hotelDTO) {
        log.info("Finding Hotel with hotelId {}", hotelDTO.toString());
        Hotel  hotelEntity = hotelRepository
                .findById(id)
                .orElseThrow(()-> new ResourceNotFound("Hotel with hotelId " + id));

        modelMapper.map(hotelDTO, hotelEntity);
        hotelEntity.setId(id);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotelEntity.getOwner())){
            throw new UnAuthorisedException("Current User Does not Own this hotel");
        }

        log.info("Updating Hotel with hotelId {}", hotelDTO.toString());
        hotelEntity=hotelRepository.save(hotelEntity);

        log.info("Hotel with hotelDTO {} has been updated", hotelDTO.toString());
        return modelMapper.map(hotelEntity, HotelDTO.class);
    }

    @Override
    @Transactional
    public Boolean deleteHotelById(Long hotelId) {
        log.info("Deleting Hotel with hotelId {}", hotelId);
        Hotel hotel=hotelRepository
                .findById(hotelId)
                .orElseThrow(()-> new ResourceNotFound("Hotel with hotelId " + hotelId));

        /*
            Boolean exist = hotelRepository.existById(id);
            if(!exist)
                throw new ResourceNotFound("Hotel Not Found");
         */
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("Current User Does not Own this hotel");
        }


        for(Room room : hotel.getRooms()) {
            inventoryServices.deleteAllInventory(room);
            roomService.deleteRoom(room.getId());

        }


        hotelRepository.deleteById(hotelId);
        return true;
    }

    @Override
    @Transactional
    public void ActivateHotel(Long hotelId) {
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(()-> new ResourceNotFound("Hotel with hotelId " + hotelId));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("Current User Does not Own this hotel");
        }

        hotel.setActive(true);

        hotelRepository.save(hotel);
        log.info("Hotel with hotel for has been activated", hotel);

        for(Room room : hotel.getRooms()) {
            log.info("Initializing room with room price {}", room.getBasePrice());
            inventoryServices.initializeRoomForYear(room);



        }
    }

    @Override
    public HotelInfoDTO getHotelInfoById(Long hotelId) {
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(()-> new ResourceNotFound("Hotel with hotelId " + hotelId));

        List<RoomDTO> rooms = hotel.getRooms()
                                .stream()
                                .map(element -> modelMapper.map(element,RoomDTO.class))
                                .toList();

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("Current User Does not Own this hotel");
        }

        return new HotelInfoDTO(modelMapper.map(hotel, HotelDTO.class),rooms);
    }
}
