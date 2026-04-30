package se.voizter.felparkering.api.dto;

public record ReportUpdatedResponse(
    String message,
    ReportDetailDto report
) {}
