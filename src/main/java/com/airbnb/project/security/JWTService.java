package com.airbnb.project.security;

import com.airbnb.project.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Date;

@Service
public class JWTService {

    @Value("${jwt.secret.key}")
    private String secretKey;

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private String generateAccessToken(User user){
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email",user.getEmail())
                .claim("roles",user.getRole().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+1000L*60*10))
                .signWith(getSecretKey())
                .compact();

    }

    private String generateRefreshToken(User user){
        return Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+1000L*60*60*24*30*6))
                .signWith(getSecretKey())
                .compact();

    }

    private Long getUserIdFromToken(String token){
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.parseLong(claims.getSubject());
    }



}
