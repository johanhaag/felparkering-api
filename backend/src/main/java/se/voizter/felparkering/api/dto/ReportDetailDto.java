package se.voizter.felparkering.api.dto;

import java.time.Instant;

import se.voizter.felparkering.api.enums.ParkingViolationCategory;
import se.voizter.felparkering.api.enums.Status;

public record ReportDetailDto(
    Long id,
    AddressDto address,
    String licensePlate,
    ParkingViolationCategory category,
    AttendantGroupDto attendantGroup,
    Long assignedToId,
    Instant createdOn,
    Instant updatedOn,
    Status status
) {}
