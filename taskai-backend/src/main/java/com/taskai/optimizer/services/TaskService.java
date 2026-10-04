package com.taskai.optimizer.services;

import com.taskai.optimizer.dto.request.TaskRequest;
import com.taskai.optimizer.dto.response.TaskResponse;

import java.util.List;

public interface TaskService {

    TaskResponse create(TaskRequest request);

    List<TaskResponse> getAll();

    TaskResponse getById(Long id);

    TaskResponse update(Long id, TaskRequest request);

    void delete(Long id);
}