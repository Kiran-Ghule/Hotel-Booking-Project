package com.airbnb.project.repositories;

import com.airbnb.project.entities.Hotel;
import com.airbnb.project.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    Collection<Object> findByOwner(User user);
}
