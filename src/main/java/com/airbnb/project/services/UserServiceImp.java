package com.airbnb.project.services;

import com.airbnb.project.dtos.ProfileUpdateDTO;
import com.airbnb.project.dtos.UserDTO;
import com.airbnb.project.entities.User;
import com.airbnb.project.exceptions.ResourceNotFound;
import com.airbnb.project.repositories.UserRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.airbnb.project.utils.AppUtils.getCurrentUser;

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
    public void updateProfile(ProfileUpdateDTO profileUpdateDTO) {
        User user = getCurrentUser();

        if(user.getDateOfBirth()!=null) user.setDateOfBirth(profileUpdateDTO.getBirthOfDate());
        if(user.getGender()!=null) user.setGender(profileUpdateDTO.getGender());
        if(profileUpdateDTO.getName()!=null) user.setName(profileUpdateDTO.getName());

        userRepository.save(user);

    }

    @Override
    public User getMyProfile() {
        return getCurrentUser();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElse(null);
    }
}
