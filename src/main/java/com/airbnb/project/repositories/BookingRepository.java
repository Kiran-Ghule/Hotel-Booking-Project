package com.airbnb.project.repositories;

import com.airbnb.project.dtos.BookingDTO;
import com.airbnb.project.entities.Booking;
import com.airbnb.project.entities.Hotel;
import com.airbnb.project.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByPaymentSessionId(String sessionId);

    List<Booking> findByHotel(Hotel hotel);

    List<Booking> findByHotelIdAndCreatedBetween(Long hotelId, LocalDateTime startDate, LocalDateTime endDate);

    List<Booking> findByUser(User user);
}
