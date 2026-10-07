package com.airbnb.project.dtos;

import com.airbnb.project.entities.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateDTO {

    private String name;
    private LocalDate birthOfDate;
    private Gender gender;

}
