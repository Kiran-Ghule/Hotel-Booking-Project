package com.airbnb.project.services;

import com.airbnb.project.entities.User;

public interface UserService {

    User findUserById(Long id);
}
