package com.airbnb.project.controller;

import com.airbnb.project.dtos.LoginDTO;
import com.airbnb.project.dtos.LoginResponseDTO;
import com.airbnb.project.dtos.SignUpRequestDTO;
import com.airbnb.project.dtos.UserDTO;
import com.airbnb.project.security.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        log.info("Signup request received");
        return new ResponseEntity(authService.signUp(signUpRequestDTO), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request, HttpServletResponse  response) {
        String tokens[] = authService.login(loginDTO);

        Cookie cookie = new Cookie("RefreshToken", tokens[1]);
        cookie.setHttpOnly(true);

        response.addCookie(cookie);

        return ResponseEntity.ok(new LoginResponseDTO(tokens[1]));
    }


    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(HttpServletRequest request) {
        String refreshToken = Arrays.stream(request.getCookies())
                .filter( cookie ->  "RefreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(()->new AssertionError("RefreshToken not found in cookies"));

        return ResponseEntity.ok(new LoginResponseDTO(authService.refreshToken(refreshToken)));
    }

}
