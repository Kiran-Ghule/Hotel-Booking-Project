package com.airbnb.project.dtos;

import com.airbnb.project.entities.Guest;
import com.airbnb.project.entities.Hotel;
import com.airbnb.project.entities.Room;
import com.airbnb.project.entities.User;
import com.airbnb.project.entities.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {

    private Long id;
    private Integer roomsCount;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime created;
    private LocalDateTime updated;
    private BigDecimal amount;
    private BookingStatus bookingStatus;
    private Set<Guest> guests;
}
