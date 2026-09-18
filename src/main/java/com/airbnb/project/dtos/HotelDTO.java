package com.airbnb.project.dtos;

import com.airbnb.project.entities.HotelContactInfo;
import com.airbnb.project.entities.Room;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelDTO {

    private Long id;
    private String name;

    private String city;
    private String []photos;
    private String []amenities;

    private HotelContactInfo contactInfo;
    private Boolean active;
}
