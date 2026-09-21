package com.airbnb.project.repositories;

import com.airbnb.project.dtos.HotelPriceDTO;
import com.airbnb.project.entities.Hotel;
import com.airbnb.project.entities.HotelMinPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HotelMinPriceRepository extends JpaRepository<HotelMinPrice, Long> {
    @Query(value = """
                SELECT  new com.airbnb.project.dtos.HotelPriceDTO(i.hotel,Avg(i.price))
                FROM  HotelMinPrice i
                WHERE 
                    i.hotel.city= :city 
                    AND i.date BETWEEN :startDate AND :endDate
                    AND i.hotel.active= true
                GROUP BY i.hotel  
            """)
    Page<HotelPriceDTO> findHotelsWithAvailableInventory(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomCount") Integer roomCount,
            @Param("dateCount")  Long dateCount,
            Pageable pageable
    );

    Optional<HotelMinPrice> findByHotelAndDate(Hotel hotel, LocalDate date);
}
