package com.taskai.optimizer.controller;

import com.taskai.optimizer.dto.request.LoginRequest;
import com.taskai.optimizer.dto.request.RegisterRequest;
import com.taskai.optimizer.dto.response.AuthResponse;
import com.taskai.optimizer.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return service.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return service.login(request);
    }
}