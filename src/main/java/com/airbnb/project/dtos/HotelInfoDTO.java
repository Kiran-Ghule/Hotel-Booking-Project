package com.airbnb.project.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelInfoDTO {
    private HotelDTO hotelDTO;
    private List<RoomDTO> rooms;
}
