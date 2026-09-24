package com.airbnb.project.security;

import com.airbnb.project.dtos.LoginDTO;
import com.airbnb.project.dtos.SignUpRequestDTO;
import com.airbnb.project.dtos.UserDTO;
import com.airbnb.project.entities.User;
import com.airbnb.project.entities.enums.Roles;
import com.airbnb.project.exceptions.ResourceNotFound;
import com.airbnb.project.repositories.UserRepository;
import com.airbnb.project.services.UserService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    public UserDTO signUp(SignUpRequestDTO signUpRequestDTO) {

        User user = userRepository.findByEmail(signUpRequestDTO.getEmail()).orElse(null);

        if(user != null) {
            throw new RuntimeException("User already exists with email " + signUpRequestDTO.getEmail());
        }

        User newUser = modelMapper.map(signUpRequestDTO, User.class);
        newUser.setRole(Set.of(Roles.GUEST));
        newUser.setPassword(passwordEncoder.encode(signUpRequestDTO.getPassword()));
        newUser = userRepository.save(newUser);
        return modelMapper.map(newUser, UserDTO.class);

    }

    public String[] login(LoginDTO loginDTO) {
        Authentication authentication= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(), loginDTO.getPassword()
        ));
        log.info("At Authentication Checkup");
        User user = (User) authentication.getPrincipal();

        String tokens[] = new String[2];

        tokens[0] = jwtService.generateRefreshToken(user);
        tokens[1] = jwtService.generateAccessToken(user);

        return tokens;

    }

    public String refreshToken(String refreshToken) {
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId).orElseThrow(() -> new AuthenticationServiceException("User not found with id " + userId));

        return jwtService.generateAccessToken(user);


    }
}

