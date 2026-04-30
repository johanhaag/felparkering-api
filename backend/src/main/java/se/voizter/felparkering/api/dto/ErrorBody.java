package se.voizter.felparkering.api.dto;

public record ErrorBody(
    String code,
    String message
) {}
