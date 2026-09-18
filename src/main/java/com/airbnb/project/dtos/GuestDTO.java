package com.airbnb.project.dtos;

import com.airbnb.project.entities.Booking;
import com.airbnb.project.entities.User;
import com.airbnb.project.entities.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestDTO {

    private Long id;
    private User user;
    private String name;
    private Gender gender;
    private Integer age;
    private Set<Booking> bookings;

}
