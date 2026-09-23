package com.airbnb.project.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpREquestDTO {
    private String email;
    private String password;
    private Long id;
}
