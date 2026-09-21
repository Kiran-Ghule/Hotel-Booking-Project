package com.airbnb.project.strategy;

import com.airbnb.project.entities.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@RequiredArgsConstructor
public class holidayPricingStrategy implements PricingStrategy{
    private final PricingStrategy pricingStrategy;


    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = pricingStrategy.calculatePrice(inventory);
        boolean isHoliday = true;

        if(isHoliday)
        price = price.multiply(BigDecimal.valueOf(1.25));
        return price;
    }
}
