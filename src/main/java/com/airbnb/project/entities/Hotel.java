package com.airbnb.project.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data

public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String city;

    @Column(columnDefinition = "TEXT[]")
    private String []photos;

    @Column(columnDefinition = "TEXT[]")
    private String []amenities;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime created;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updated;

    @Embedded
    private  HotelContactInfo contactInfo;

    @Column(nullable = false)
    private Boolean active;

    @OneToMany(mappedBy = "hotel",fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Room> rooms;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    private User owner;




}
