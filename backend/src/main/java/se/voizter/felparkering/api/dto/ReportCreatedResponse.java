package se.voizter.felparkering.api.dto;

public record ReportCreatedResponse(
    String message,
    ReportDetailDto report
) {}
