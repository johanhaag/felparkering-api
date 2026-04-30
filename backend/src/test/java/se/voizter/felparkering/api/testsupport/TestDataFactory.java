package se.voizter.felparkering.api.testsupport;

import java.util.List;

import se.voizter.felparkering.api.dto.LoginRequest;
import se.voizter.felparkering.api.dto.RegisterRequest;
import se.voizter.felparkering.api.dto.ReportRequest;
import se.voizter.felparkering.api.dto.RouteRequest;
import se.voizter.felparkering.api.dto.UpdateStatusRequest;
import se.voizter.felparkering.api.enums.ParkingViolationCategory;
import se.voizter.felparkering.api.enums.Role;
import se.voizter.felparkering.api.enums.Status;
import se.voizter.felparkering.api.model.Address;
import se.voizter.felparkering.api.model.AttendantGroup;
import se.voizter.felparkering.api.model.Report;
import se.voizter.felparkering.api.model.User;

public final class TestDataFactory {
    
    private TestDataFactory() {

    }

    // Users
    public static User customerUser() {
        User user = new User();
        user.setEmail("customer@example.com");
        user.setPassword("password123");
        user.setRole(Role.CUSTOMER);
        return user;
    }

    public static User customerUser(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(Role.CUSTOMER);
        return user;
    }

    public static User customerUserWithId(Long id) {
        User user = customerUser();
        user.setId(id);
        return user;
    }

    public static User adminUser() {
        User user = new User();
        user.setEmail("admin@example.com");
        user.setPassword("password123");
        user.setRole(Role.ADMIN);
        return user;
    }

    public static User adminUser(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(Role.ADMIN);
        return user;
    }

    public static User adminUserWithId(Long id) {
        User user = adminUser();
        user.setId(id);
        return user;
    }

    public static User attendantUser() {
        User user = new User();
        user.setEmail("attendant@example.com");
        user.setPassword("password123");
        user.setRole(Role.ATTENDANT);
        return user;
    }

    public static User attendantUser(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(Role.ATTENDANT);
        return user;
    }

    public static User attendantUserWithId(Long id) {
        User user = attendantUser();
        user.setId(id);
        return user;
    }

    public static User attendantUser(AttendantGroup group) {
        User user = attendantUser();
        user.setAttendantGroup(group);
        return user;
    }

    public static User attendantUserWithId(Long id, AttendantGroup group) {
        User user = attendantUserWithId(id);
        user.setAttendantGroup(group);
        return user;
    }

    // Attendant groups
    public static AttendantGroup attendantGroup() {
        AttendantGroup attendantGroup = new AttendantGroup();
        attendantGroup.setName("Testgruppen");
        return attendantGroup;
    }

    public static AttendantGroup attendantGroup(String name) {
        AttendantGroup attendantGroup = attendantGroup();
        attendantGroup.setName(name);
        return attendantGroup;
    }

    public static AttendantGroup attendantGroup(Long id, String name) {
        AttendantGroup attendantGroup = attendantGroup(name);
        attendantGroup.setId(id);
        return attendantGroup;
    }

    // Addresses
    public static Address address() {
        Address address = new Address();
        address.setStreet("Testvägen");
        address.setHouseNumbers(List.of("2"));
        address.setCity("Teststaden");
        address.setLatitude(59.3293);
        address.setLongitude(18.0686);
        address.setDistanceFromCity(0.5);
        return address;
    }

    public static Address address(String street, String houseNumber, String city) {
        Address address = address();
        address.setStreet(street);
        address.setHouseNumbers(List.of(houseNumber));
        address.setCity(city);
        return address;
    }

    public static Address address(String street, List<String> houseNumbers, String city) {
        Address address = address();
        address.setStreet(street);
        address.setHouseNumbers(houseNumbers);
        address.setCity(city);
        return address;
    }

    public static Address address(Long id, String street, List<String> houseNumbers, String city) {
        Address address = address(street, houseNumbers, city);
        address.setId(id);
        return address;
    }

    public static Address address(Long id, String street, String houseNumber, String city) {
        return address(id, street, List.of(houseNumber), city);
    }

    // Reports
    public static Report report() {
        Report report = new Report();
        report.setAddress(address());
        report.setLatitude(59.3293);
        report.setLongitude(18.0686);
        report.setLicensePlate("ABC123");
        report.setCategory(ParkingViolationCategory.NO_PARKING_AREA);
        report.setStatus(Status.NEW);
        return report;
    }

    public static Report report(Address address) {
        Report report = report();
        report.setAddress(address);
        return report;
    }

    public static Report report(Long id, User createdBy, AttendantGroup group, Status status) {
        Report report = report();
        report.setId(id);
        report.setCreatedBy(createdBy);
        report.setAttendantGroup(group);
        report.setStatus(status);
        return report;
    }

    public static Report reportCreatedBy(User user) {
        Report report = report();
        report.setCreatedBy(user);
        return report;
    }

    public static Report assignedReport(User attendant) {
        Report report = report();
        report.setAssignedTo(attendant);
        report.setStatus(Status.ASSIGNED);
        return report;
    }

    public static Report assignedReport(User attendant, AttendantGroup group) {
        Report report = assignedReport(attendant);
        report.setAttendantGroup(group);
        return report;
    }

    public static Report reportWithGroup(AttendantGroup group) {
        Report report = report();
        report.setAttendantGroup(group);
        return report;
    }

    public static Report reportWithStatus(Status status) {
        Report report = report();
        report.setStatus(status);
        return report;
    }

    public static Report reportWithLicensePlate(String licensePlate) {
        Report report = report();
        report.setLicensePlate(licensePlate);
        return report;
    }

    public static Report reportWithCategory(ParkingViolationCategory category) {
        Report report = report();
        report.setCategory(category);
        return report;
    }

    // Request DTOs
    public static LoginRequest loginRequest() {
        return new LoginRequest("customer@example.com", "password123");
    }

    public static LoginRequest loginRequest(String email, String password) {
        return new LoginRequest(email, password);
    }

    public static RegisterRequest registerRequest() {
        return new RegisterRequest(
            "customer@example.com", 
            "password123", 
            "password123"
        );
    }

    public static RegisterRequest registerRequest(String email, String password) {
        return new RegisterRequest(
            email, 
            password, 
            password
        );
    }

    public static RegisterRequest registerRequest(String email, String password, String confPassword) {
        return new RegisterRequest(
            email, 
            password, 
            confPassword
        );
    }

    public static ReportRequest reportRequest() {
        return new ReportRequest(
            1L, 
            "Testvägen", 
            "2", 
            "Teststaden", 
            "ABC123", 
            ParkingViolationCategory.NO_PARKING_AREA
        );
    }

    public static ReportRequest reportRequest(
        Long id,
        String street,
        String houseNumber,
        String city,
        String licensePlate,
        ParkingViolationCategory category
    ) {
        return new ReportRequest(id, street, houseNumber, city, licensePlate, category);
    }

    public static UpdateStatusRequest updateStatusRequest(Status status) {
        return new UpdateStatusRequest(status);
    }

    public static RouteRequest routeRequest() {
        return new RouteRequest(
            new double[] { 59.3293, 18.0686 }, 
            new double[] { 59.3346, 18.0686 }
        );
    }

}
