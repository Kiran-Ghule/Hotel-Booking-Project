package com.airbnb.project.services;

import com.airbnb.project.dtos.ProfileUpdateDTO;
import com.airbnb.project.dtos.UserDTO;
import com.airbnb.project.entities.User;

public interface UserService {

    User findUserById(Long id);

    void updateProfile(ProfileUpdateDTO profileUpdateDTO);

    User getMyProfile();
}
