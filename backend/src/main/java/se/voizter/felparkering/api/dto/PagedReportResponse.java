package se.voizter.felparkering.api.dto;

import java.util.List;

public record PagedReportResponse(
    List<ReportDetailDto> items,
    int page,
    int size,
    long totalElements,
    int totalPages
) {}
