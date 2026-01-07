package com.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import com.Exceptions.BadRequestException;
import com.Exceptions.UserNotFoundException;
import com.Model.UserStruct;
import com.Repository.UserRepo;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final UserRepo userRepo;

    public JwtService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            "zone01secretkey_jwt_for_testssss".getBytes());

    public String generateToken(String username, String userUuid) {
        return Jwts.builder()
                .setSubject(username) // 👈 KEEP IT
                .claim("uuid", userUuid)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600_000))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        if (token == null || token.isEmpty()) {
            throw new BadRequestException("JWT token is missing");
        }
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String username = claims.getSubject();
        String uuidFromToken = claims.get("uuid", String.class);

        UserStruct user = userRepo.findByUsername(username);

        if (!user.getUserUuid().equals(uuidFromToken)) {
            throw new SecurityException("Invalid token");
        }

        return username;
    }

}
