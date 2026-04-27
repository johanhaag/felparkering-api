import { Report } from "../types/report";

export type ReportAction = {
    key: string;
    label: string;
    onPress: () => void;
    variant?: "primary" | "secondary" | "danger";
    disabled?: boolean;
};

export function getAttendantActions (
        report: Report,
        currentUserId: number | undefined,
        handlers: {
            onAssign: () => void;
            onResolve: () => void;
            onUnassign: () => void;
            onSelectOther?: () => void;
        }
    ) {
        const isAssignedToMe = currentUserId != null && report.assignedToId === currentUserId;

        const isAssignedToOther = report.assignedToId != null && currentUserId != null && report.assignedToId !== currentUserId;

        switch (report.status) {
            case "NEW":
                return [
                    {
                        key: "assign",
                        label: "Assign to me",
                        onPress: handlers.onAssign,
                        variant: "primary" as const,
                    },
                ];

            case "ASSIGNED": 
                if (isAssignedToMe) {
                    return [
                        {
                            key: "resolve",
                            label: "Resolve",
                            onPress: handlers.onResolve,
                            variant: "primary" as const,
                        },
                        {
                            key: "unassign",
                            label: "Unassign",
                            onPress: handlers.onUnassign,
                            variant: "secondary" as const,
                        },
                    ];
                }
                if (isAssignedToOther) {
                    return [
                        {
                            key: "assigned-other",
                            label: "Assign",
                            onPress: () => {},
                            variant: "secondary" as const,
                            disabled: true,
                        },
                    ];
                }

                return []

            case "RESOLVED":
            
            default:
                return [];
        }
    }