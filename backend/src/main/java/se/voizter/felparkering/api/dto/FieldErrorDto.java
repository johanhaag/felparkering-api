package se.voizter.felparkering.api.dto;

public record FieldErrorDto(
    String field,
    String message
) {}
