package com.airbnb.project.entities;

import com.airbnb.project.entities.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "Hotel_Id", nullable = false)
        private Hotel hotel;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "Room_Id",nullable = false)
        private Room room;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "User_Id",nullable = false)
        private User user;

        @Column(nullable = false)
        private Integer roomsCount;

        @Column(nullable = false)
        private LocalDate checkInDate;

        @Column(nullable = false)
        private LocalDate checkOutDate;

        @CreationTimestamp
        @Column(updatable = false)
        private LocalDateTime created;

        @UpdateTimestamp
        private LocalDateTime updated;

        @Column(nullable = false,precision = 10,scale = 2)
        private BigDecimal amount;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private BookingStatus bookingStatus;

        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(
            name = "Booking_Guest",
                joinColumns = @JoinColumn(name = "Booking_Id"),
                inverseJoinColumns = @JoinColumn(name="Guest_Id")
            )
        private Set<Guest> guests;

}
