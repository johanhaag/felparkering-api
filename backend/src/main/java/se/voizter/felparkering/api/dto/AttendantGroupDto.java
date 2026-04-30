package se.voizter.felparkering.api.dto;

import se.voizter.felparkering.api.model.AttendantGroup;

public record AttendantGroupDto(
    Long id,
    String name
) {
    public static AttendantGroupDto fromEntity(AttendantGroup group) {
        if (group == null) {
            return null;
        }

        return new AttendantGroupDto(
            group.getId(),
            group.getName()
        );
    }
}
