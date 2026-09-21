package com.airbnb.project.strategy;

import com.airbnb.project.entities.Hotel;
import com.airbnb.project.entities.HotelMinPrice;
import com.airbnb.project.entities.Inventory;
import com.airbnb.project.repositories.HotelMinPriceRepository;
import com.airbnb.project.repositories.HotelRepository;
import com.airbnb.project.repositories.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PricingUpdateService {
    //Schedular to update the inventory and HotelMinPrice tables every hour

    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository  hotelMinPriceRepository;
    private final PricingService pricingService;

    @Scheduled(cron = "0 0 * * * * ")// for every day : fro every five min 0 */5 * * * *
    public  void updates()
    {
        int page =0;
        int batchsize=100;

        while(true)
        {
            Page<Hotel> hotelPage = hotelRepository.findAll(PageRequest.of(page,batchsize));
            log.info(hotelPage.getContent().size()+" Size of Hotels-------------------------------");
            if(hotelPage.isEmpty())
                break;

            hotelPage.getContent().forEach(this::updateHotelPrice);

            page++;
        }
    }

    public void updateHotelPrice(Hotel hotel)
    {
        log.info("Updating hotel price for hotel {}", hotel.getId());
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);

        List<Inventory> inventoryList = inventoryRepository.findByHotelAndDateBetween(hotel,startDate,endDate);
        log.info("Updating hotel price Inventory of Size {} ", inventoryList.size());
        updateInventoryPrices(inventoryList);

        updateHotelMinPrice(hotel,inventoryList,startDate,endDate);

    }

    private void updateHotelMinPrice(Hotel hotel, List<Inventory> inventoryList,LocalDate startDate,LocalDate endDate)
    {
        Map<LocalDate,BigDecimal> dailyPrices = inventoryList.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getDate,
                        Collectors.mapping(Inventory::getPrice,Collectors.minBy(Comparator.naturalOrder()))
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,e -> e.getValue().orElse(BigDecimal.ZERO)));

        List<HotelMinPrice> hotelMinPrices = new ArrayList<>();
        dailyPrices.forEach((date,price)->{
            HotelMinPrice hotelMinPrice = hotelMinPriceRepository.findByHotelAndDate(hotel,date)
                    .orElse(new HotelMinPrice(hotel,date));
            hotelMinPrice.setPrice(price);
            hotelMinPrices.add(hotelMinPrice);
        });
        hotelMinPriceRepository.saveAll(hotelMinPrices);

    }

    private void updateInventoryPrices(List<Inventory> inventory)
    {
        inventory.forEach(inventoryItem -> {
            BigDecimal dynamicPrice = pricingService.calculateDynamicPrice(inventoryItem);
            inventoryItem.setPrice(dynamicPrice);
            log.info("Updating inventory price as {} with date {}", inventoryItem.getPrice(),inventoryItem.getDate());
        });

        inventoryRepository.saveAll(inventory);
    }
}
