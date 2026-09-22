package com.airbnb.project.services;

import com.airbnb.project.entities.User;
import com.airbnb.project.exceptions.ResourceNotFound;
import com.airbnb.project.repositories.UserRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements  UserService {

    private  final UserRepository userRepository;
    @Override
    public User findUserById(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(()->new ResourceNotFound("User Not found with Id: "+id));
    }
}
