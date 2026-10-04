package com.taskai.optimizer.dto.request;

import com.taskai.optimizer.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    public String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    public String email;

    @NotBlank(message = "Password is required")
    @Size(min = 4, max = 100, message = "Password must be between 4 and 100 characters")
    public String password;

    @NotNull(message = "Role is required")
    public Role role;
}