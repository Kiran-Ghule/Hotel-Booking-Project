package com.airbnb.project.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateInventoryRequestDTO {

    private LocalDate startTime;
    private LocalDate endTime;
    private BigDecimal surgeFactor;
    private Boolean closed;


}
