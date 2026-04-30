package se.voizter.felparkering.api.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRequest(
    @NotBlank(message = "Id is required")
    Long id
) {}
