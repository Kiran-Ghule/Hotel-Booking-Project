package com.airbnb.project.controller;

import com.airbnb.project.dtos.BookingDTO;
import com.airbnb.project.dtos.ProfileUpdateDTO;
import com.airbnb.project.dtos.UserDTO;
import com.airbnb.project.services.BookingService;
import com.airbnb.project.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;
    private final ModelMapper modelMapper;

    @PutMapping("/profile")
    public ResponseEntity<Void> updateProfile(@RequestBody ProfileUpdateDTO profileUpdateDTO) {
        userService.updateProfile(profileUpdateDTO);
        return ResponseEntity.ok().build();

    }

    @GetMapping("/myBooking")
    public ResponseEntity<List<BookingDTO>> getMyBookings() {
        return ResponseEntity.ok(bookingService.getMyBookings());
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getMyProfile() {
        return ResponseEntity.ok(modelMapper.map(userService.getMyProfile(), UserDTO.class));
    }

}
