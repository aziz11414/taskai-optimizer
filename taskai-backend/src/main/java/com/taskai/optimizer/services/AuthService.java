package com.taskai.optimizer.services;

import com.taskai.optimizer.dto.request.LoginRequest;
import com.taskai.optimizer.dto.request.RegisterRequest;
import com.taskai.optimizer.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}