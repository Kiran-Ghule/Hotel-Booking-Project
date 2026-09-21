package com.airbnb.project.strategy;

import com.airbnb.project.entities.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
public class UrgencyPricingStrategy implements PricingStrategy{

    private  final  PricingStrategy pricingStrategy;


    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = pricingStrategy.calculatePrice(inventory);
        LocalDate now =  LocalDate.now();

        if(!inventory.getDate().isBefore(now) && inventory.getDate().isBefore(now.plusDays(7))){
            price = price.multiply(BigDecimal.valueOf(1.5));
        }

        return price;
    }
}
