package se.voizter.felparkering.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "Email address is required")
    @Email(message = "Invalid email address")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    String password,

    @NotBlank(message = "Confirmation password is required")
    @Size(min = 8, message = "Confirmation password must be at least 8 characters long")
    String confPassword
) {}
