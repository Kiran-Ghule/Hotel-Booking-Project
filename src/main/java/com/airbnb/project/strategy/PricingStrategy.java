package com.airbnb.project.strategy;

import com.airbnb.project.entities.Hotel;
import com.airbnb.project.entities.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal calculatePrice(Inventory inventory);
}
