package com.airbnb.project.entities;

import jakarta.persistence.*;
import lombok.Data;


@Embeddable
@Data
public class HotelContactInfo {

    private String address;
    private String phoneNumber;
    private String email;
    private String location;
}
