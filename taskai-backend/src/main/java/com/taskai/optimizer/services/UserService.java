package com.taskai.optimizer.services;

import com.taskai.optimizer.dto.request.UserRequest;
import com.taskai.optimizer.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse create(UserRequest request);

    List<UserResponse> getAll();

    UserResponse getById(Long id);

    UserResponse update(Long id, UserRequest request);

    void delete(Long id);
}