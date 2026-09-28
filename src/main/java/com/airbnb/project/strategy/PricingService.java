package com.airbnb.project.strategy;

import com.airbnb.project.entities.Inventory;
import com.airbnb.project.entities.Room;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PricingService {

    public BigDecimal calculateDynamicPrice(Inventory inventory) {
        PricingStrategy pricingStrategy = new BasePricingStrategy();

        pricingStrategy = new SurgePricingStrategy(pricingStrategy);
        pricingStrategy = new UrgencyPricingStrategy(pricingStrategy);
        pricingStrategy = new OccupancyPricingStrategy(pricingStrategy);
        pricingStrategy = new holidayPricingStrategy(pricingStrategy);

        return pricingStrategy.calculatePrice(inventory);

    }

    public BigDecimal calculateTotalPrice(List<Inventory> inventoryList) {

          return inventoryList.stream()
                    .map(this::calculateDynamicPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

    }
}
