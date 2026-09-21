package com.airbnb.project.dtos;

import com.airbnb.project.entities.Hotel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDTO {

    private Long id;
    private String type;
    private BigDecimal basePrice;

    private Integer totalCount;
    private Integer capacity;
    private String[] photos;
    private String[] amenities;
}
