package se.voizter.felparkering.api.dto;

import jakarta.validation.constraints.NotNull;
import se.voizter.felparkering.api.enums.Status;

public record UpdateStatusRequest(@NotNull Status status) {}
