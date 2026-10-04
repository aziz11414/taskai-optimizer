package com.taskai.optimizer.services.Impl;

import com.taskai.optimizer.dto.request.LoginRequest;
import com.taskai.optimizer.dto.request.RegisterRequest;
import com.taskai.optimizer.dto.response.AuthResponse;
import com.taskai.optimizer.entity.User;
import com.taskai.optimizer.enums.Role;
import com.taskai.optimizer.exception.BusinessException;
import com.taskai.optimizer.repository.UserRepository;
import com.taskai.optimizer.security.JwtService;
import com.taskai.optimizer.services.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        validateRegisterRequest(request);

        String email = request.email.trim().toLowerCase();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessException("Email already exists");
        }

        User user = new User();
        user.setFullName(request.fullName.trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password));
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser.getEmail(), savedUser.getRole().name());

        return map(savedUser, token, "User registered successfully");
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        validateLoginRequest(request);

        String email = request.email.trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password, user.getPassword())) {
            throw new BusinessException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return map(user, token, "Login successful");
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (request == null) {
            throw new BusinessException("Request body is required");
        }
        if (request.fullName == null || request.fullName.isBlank()) {
            throw new BusinessException("Full name is required");
        }
        if (request.email == null || request.email.isBlank()) {
            throw new BusinessException("Email is required");
        }
        if (request.password == null || request.password.isBlank()) {
            throw new BusinessException("Password is required");
        }
    }

    private void validateLoginRequest(LoginRequest request) {
        if (request == null) {
            throw new BusinessException("Request body is required");
        }
        if (request.email == null || request.email.isBlank()) {
            throw new BusinessException("Email is required");
        }
        if (request.password == null || request.password.isBlank()) {
            throw new BusinessException("Password is required");
        }
    }

    private AuthResponse map(User user, String token, String message) {
        AuthResponse response = new AuthResponse();
        response.id = user.getId();
        response.fullName = user.getFullName();
        response.email = user.getEmail();
        response.role = user.getRole().name();
        response.token = token;
        response.message = message;
        return response;
    }
}