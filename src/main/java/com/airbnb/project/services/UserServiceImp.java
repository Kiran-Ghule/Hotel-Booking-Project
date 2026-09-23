package com.airbnb.project.services;

import com.airbnb.project.entities.User;
import com.airbnb.project.exceptions.ResourceNotFound;
import com.airbnb.project.repositories.UserRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements  UserService, UserDetailsService {

    private  final UserRepository userRepository;
    @Override
    public User findUserById(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(()->new ResourceNotFound("User Not found with Id: "+id));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElse(null);
    }
}
