package se.voizter.felparkering.api.enums;

public enum Message {
    LOGIN("User logged in successfully"),
    REGISTER("User registered successfully"),
    UNAUTHORIZED("Missing or invalid token"),
    ACCESS_DENIED("Access Denied"),
    MISSING_CREDENTIALS("Missing credentials"),
    PASSWORD_MISMATCH("Password and confirmation does not match"),
    USER_EXISTS("User already exists"),
    INVALID_CREDENTIALS("Invalid credentials"),
    USER_NOT_FOUND("User not found"),
    USER_DELETED("User deleted successfully"),
    USER_CREATED("User was created successfully"),
    REPORT_NOT_FOUND("Report not found"),
    ADDRESS_NOT_FOUND("Address not found"),
    ATTENDANT_GROUP_NOT_FOUND("Attendant group not found"),
    REPORT_CREATED_SUCCESSFULLY("Report created successfully"),
    REPORT_NO_PERMISSION("You do not have permission to get all reports"),
    REPORT_ALREADY_ASSIGNED("Report is already assigned to a different user"),
    REPORT_UPDATED_SUCCESSFULLY("Report updated successfully"),
    ADMIN_USERS_FETCHED("Users fetched successfully"),
    ADMIN_GROUPS_FETCHED("Attendant groups fetched successfully"),
    ;

    private final String prettyName;

    Message(String prettyName) {
        this.prettyName = prettyName;
    }

    @Override
    public String toString() {
        return prettyName;
    }
}
