package se.voizter.felparkering.api.dto;

public record ApiResponse<T>(
    T data,
    String message
) {}
