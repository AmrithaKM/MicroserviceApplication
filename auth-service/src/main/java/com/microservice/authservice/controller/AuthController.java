package com.microservice.authservice.controller;

import com.microservice.authservice.dto.LoginRequest;
import com.microservice.authservice.dto.LoginResponse;
import com.microservice.authservice.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final JwtService jwtService;

    @Autowired
    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        // Simple in-memory validation (replace with DB lookup in production)
        logger.info("Attempting login for user: {}", req.getUsername());
        logger.info("Password provided: {}", req.getPassword());
        if ("user".equals(req.getUsername()) && "password".equals(req.getPassword())) {
            String token = jwtService.generateToken(req.getUsername());
            return ResponseEntity.ok(new LoginResponse(token));
        } else {
            return ResponseEntity.status(401).build();
        }
    }
}
