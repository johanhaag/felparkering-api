package se.voizter.felparkering.api.dto;

import java.util.List;

public record ValidationErrorResponse(
    List<FieldErrorDto> errors
){}