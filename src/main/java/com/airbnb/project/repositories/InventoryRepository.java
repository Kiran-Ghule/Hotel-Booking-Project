package com.airbnb.project.repositories;

import com.airbnb.project.entities.Hotel;
import com.airbnb.project.entities.Inventory;
import com.airbnb.project.entities.Room;
import jakarta.persistence.LockModeType;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    void deleteByRoom( Room room);
 @Query("""
    SELECT DISTINCT i.hotel
    FROM  Inventory i
    WHERE 
        i.city= :city 
        AND i.date BETWEEN :startDate AND :endDate
        AND i.closed=FALSE
        AND (i.totalCount - i.bookedCount- i.reservedCount) >= :roomCount
    GROUP BY i.hotel, i.room
    HAVING COUNT(i.date) = :dateCount   
""")
    Page<Hotel> findHotelsWithAvailableInventory(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomCount") Integer roomCount,
            @Param("dateCount")  Long dateCount,
            Pageable pageable
    );

    @Query("""
        Select i
        from Inventory i
        where i.room.id = :roomId
        and (i.date between :startDate and :endDate)
        and i.closed = false
        AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomCount      
""")
        @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockAvailableInventory(
            @Param("roomId") Long roomId,
            @Param("startDate")LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomCount") Integer roomCount
            );
}
