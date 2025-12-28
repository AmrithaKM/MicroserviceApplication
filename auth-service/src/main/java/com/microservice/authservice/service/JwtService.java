package com.microservice.authservice.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret:my-super-secret-key-for-jwt-token-generation}")
    private String jwtSecret;

    private final long EXPIRATION = 1000 * 60 * 60; // 1 hour

    public String generateToken(String username) {
        Key key = getSigningKey();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256");
    }
}

