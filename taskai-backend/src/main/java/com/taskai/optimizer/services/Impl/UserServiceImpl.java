package com.taskai.optimizer.services.Impl;

import com.taskai.optimizer.dto.request.UserRequest;
import com.taskai.optimizer.dto.response.UserResponse;
import com.taskai.optimizer.entity.User;
import com.taskai.optimizer.enums.NotificationType;
import com.taskai.optimizer.enums.Role;
import com.taskai.optimizer.exception.BusinessException;
import com.taskai.optimizer.exception.ResourceNotFoundException;
import com.taskai.optimizer.repository.UserRepository;
import com.taskai.optimizer.services.NotificationService;
import com.taskai.optimizer.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           NotificationService notificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
    }

    @Override
    public UserResponse create(UserRequest request) {
        validateCreateRequest(request);

        String email = request.email.trim().toLowerCase();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessException("Email already exists");
        }

        User user = new User();
        user.setFullName(request.fullName.trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password));
        user.setRole(request.role != null ? request.role : Role.USER);

        User savedUser = userRepository.save(user);

        notificationService.createForUser(
                savedUser,
                "Welcome to TaskAI Optimizer",
                "Your account has been created successfully.",
                NotificationType.SUCCESS
        );

        return map(savedUser);
    }

    @Override
    public List<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getById(Long id) {
        return map(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found")));
    }

    @Override
    public UserResponse update(Long id, UserRequest request) {
        validateUpdateRequest(request);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        String email = request.email.trim().toLowerCase();

        userRepository.findByEmail(email)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException("Email already exists");
                });

        user.setFullName(request.fullName.trim());
        user.setEmail(email);
        user.setRole(request.role != null ? request.role : user.getRole());

        if (request.password != null && !request.password.isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password));
        }

        User updatedUser = userRepository.save(user);

        notificationService.createForUser(
                updatedUser,
                "Profile updated",
                "Your account information has been updated.",
                NotificationType.INFO
        );

        return map(updatedUser);
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        userRepository.delete(user);
    }

    private void validateCreateRequest(UserRequest request) {
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

    private void validateUpdateRequest(UserRequest request) {
        if (request == null) {
            throw new BusinessException("Request body is required");
        }
        if (request.fullName == null || request.fullName.isBlank()) {
            throw new BusinessException("Full name is required");
        }
        if (request.email == null || request.email.isBlank()) {
            throw new BusinessException("Email is required");
        }
    }

    private UserResponse map(User user) {
        UserResponse response = new UserResponse();
        response.id = user.getId();
        response.fullName = user.getFullName();
        response.email = user.getEmail();
        response.role = user.getRole() != null ? user.getRole().name() : null;
        return response;
    }
}