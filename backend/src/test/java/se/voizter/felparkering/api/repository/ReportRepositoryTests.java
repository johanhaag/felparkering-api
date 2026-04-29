package se.voizter.felparkering.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import se.voizter.felparkering.api.model.Address;
import se.voizter.felparkering.api.model.AttendantGroup;
import se.voizter.felparkering.api.model.Report;
import se.voizter.felparkering.api.model.User;
import se.voizter.felparkering.api.testsupport.TestDataFactory;
import se.voizter.felparkering.api.enums.ParkingViolationCategory;
import se.voizter.felparkering.api.enums.Status;

@DataJpaTest
public class ReportRepositoryTests {
    @Autowired
    ReportRepository reportRepository;

    @Autowired
    AttendantGroupRepository groupRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Test
    void canSaveAndFindById() {
        Report report = reportRepository.save(
            TestDataFactory.report()
        );

        Optional<Report> result = reportRepository.findById(report.getId());

        assertTrue(result.isPresent());
        assertEquals(report.getId(), result.get().getId());
        assertEquals(report.getAddress().toString(), result.get().getAddress().toString());
        assertEquals(report.getLicensePlate(), result.get().getLicensePlate());
        assertEquals(report.getCategory(), result.get().getCategory());
        assertEquals(report.getStatus(), result.get().getStatus());
    }

    @Test
    void findByIdReturnsEmptyWhenNotFound() {
        Optional<Report> result = reportRepository.findById((long) 10);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByAttendantGroup() {
        AttendantGroup group = groupRepository.save(
            TestDataFactory.attendantGroup()
        );

        Address address = addressRepository.save(
            TestDataFactory.address()
        );

        Report report = TestDataFactory.reportWithGroup(group);
        report.setAddress(address);
        reportRepository.save(report);
        
        List<Report> result = reportRepository.findByAttendantGroup(group);

        assertFalse(result.isEmpty());

        AttendantGroup group2 = groupRepository.save(
            TestDataFactory.attendantGroup("no-group")
        );

        List<Report> result2 = reportRepository.findByAttendantGroup(group2);

        assertTrue(result2.isEmpty());
    }

    @Test
    void findByCreatedBy() {
        User user = userRepository.save(
            TestDataFactory.customerUser()
        );

        Address address = addressRepository.save(
            TestDataFactory.address()
        );

        Report report = TestDataFactory.reportCreatedBy(user);
        report.setAddress(address);
        reportRepository.save(report);
        
        List<Report> result = reportRepository.findByCreatedBy(user);

        assertFalse(result.isEmpty());

        User user2 = userRepository.save(
            TestDataFactory.customerUser("no-user@example.com", "password123")
        );

        List<Report> result2 = reportRepository.findByCreatedBy(user2);

        assertTrue(result2.isEmpty());
    }

    @Test
    void findByFilters() {
        Address matchingAddress = addressRepository.save(
            TestDataFactory.address("Matchgatan", "1", "Teststad")
        );

        Address otherAddress = addressRepository.save(
            TestDataFactory.address("Annan gata", "2", "Teststad")
        );

        Report matchingReport = TestDataFactory.report(matchingAddress);
        matchingReport.setStatus(Status.NEW);
        matchingReport.setLicensePlate("ABC123");
        reportRepository.save(matchingReport);

        Report wrongStatusReport = TestDataFactory.report(otherAddress);
        wrongStatusReport.setStatus(Status.RESOLVED);
        wrongStatusReport.setLicensePlate("XYZ123");
        reportRepository.save(wrongStatusReport);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Report> result = reportRepository.findByFilters(
            Status.NEW,
            "match",
            pageable
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(matchingReport.getId(), result.getContent().get(0).getId());
    }

    @Test
    void findByFiltersInGroup() {
        AttendantGroup group = groupRepository.save(
            TestDataFactory.attendantGroup("Testgruppen")
        );

        AttendantGroup otherGroup = groupRepository.save(
            TestDataFactory.attendantGroup("Annan grupp")
        );

        User attendant = TestDataFactory.attendantUser(group);
        User savedAttendant = userRepository.save(attendant);

        Address matchingAddress = addressRepository.save(
            TestDataFactory.address("Gruppgatan", "1", "Teststad")
        );

        Address otherAddress = addressRepository.save(
            TestDataFactory.address("Annan gata", "2", "Teststad")
        );

        Report matchingReport = TestDataFactory.assignedReport(savedAttendant, group);
        matchingReport.setAddress(matchingAddress);
        matchingReport.setLicensePlate("GRP123");
        reportRepository.save(matchingReport);

        Report otherGroupReport = TestDataFactory.assignedReport(savedAttendant, otherGroup);
        otherGroupReport.setAddress(otherAddress);
        otherGroupReport.setLicensePlate("GRP999");
        reportRepository.save(otherGroupReport);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Report> result = reportRepository.findByFiltersInGroup(
            Status.ASSIGNED,
            savedAttendant,
            group,
            "grupp",
            pageable
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(matchingReport.getId(), result.getContent().get(0).getId());
    }

    @Test
    void findByFiltersCreatedBy() {
        User customer = userRepository.save(
            TestDataFactory.customerUser("customer@example.com", "password123")
        );

        User otherCustomer = userRepository.save(
            TestDataFactory.customerUser("other@example.com", "password123")
        );

        Address matchingAddress = addressRepository.save(
            TestDataFactory.address("Kundgatan", "1", "Teststad")
        );

        Address otherAddress = addressRepository.save(
            TestDataFactory.address("Annan gata", "2", "Teststad")
        );

        Report matchingReport = TestDataFactory.reportCreatedBy(customer);
        matchingReport.setAddress(matchingAddress);
        matchingReport.setStatus(Status.NEW);
        matchingReport.setLicensePlate("CUS123");
        reportRepository.save(matchingReport);

        Report otherCustomerReport = TestDataFactory.reportCreatedBy(otherCustomer);
        otherCustomerReport.setAddress(otherAddress);
        otherCustomerReport.setStatus(Status.NEW);
        otherCustomerReport.setLicensePlate("CUS999");
        reportRepository.save(otherCustomerReport);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Report> result = reportRepository.findByFiltersCreatedBy(
            Status.NEW,
            customer,
            "kund",
            pageable
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(matchingReport.getId(), result.getContent().get(0).getId());
    }

    @Test
    void canSaveReportWithDuplicateParams() {
        Report report1 = reportRepository.save(
            TestDataFactory.report()
        );

        Report report2 = reportRepository.save(
            TestDataFactory.report()
        );

        assertNotEquals(report1.getId(), report2.getId());
        assertEquals(report1.getAddress().toString(), report2.getAddress().toString());
        assertEquals(report1.getLicensePlate(), report2.getLicensePlate());
        assertEquals(report1.getCategory(), report2.getCategory());
        assertEquals(report1.getStatus(), report2.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenMissingRequiredField() {
        Report report2 = new Report();
        Address address2 = TestDataFactory.address("Testgatan", "2", "Testia");
        report2.setAddress(address2);
        report2.setCategory(ParkingViolationCategory.NO_PARKING_AREA);
        report2.setStatus(Status.NEW);
        
        assertThrows(DataIntegrityViolationException.class, () -> {
            reportRepository.saveAndFlush(report2);
        });

        Report report3 = new Report();
        Address address3 = TestDataFactory.address("Testgatan", "2", "Testia");
        report3.setAddress(address3);
        report3.setLicensePlate("ITES71");
        report3.setStatus(Status.NEW);
        
        assertThrows(DataIntegrityViolationException.class, () -> {
            reportRepository.saveAndFlush(report3);
        });

        Report report4 = new Report();
        Address address4 = TestDataFactory.address("Testgatan", "2", "Testia");
        report4.setAddress(address4);
        report4.setLicensePlate("ITES71");
        report4.setCategory(ParkingViolationCategory.NO_PARKING_AREA);
        
        assertThrows(DataIntegrityViolationException.class, () -> {
            reportRepository.saveAndFlush(report4);
        });
    }

    @Test
    void canSaveReportWithAttendantGroup() {
        AttendantGroup group = groupRepository.save(
            TestDataFactory.attendantGroup("Testgruppen")
        );

        Report report = reportRepository.save(
            TestDataFactory.reportWithGroup(group)
        );

        assertEquals("Testgruppen", report.getAttendantGroup().getName());
    }

    @Test 
    void canSaveReportWithAssignedUser() {
        User user = userRepository.save(
            TestDataFactory.attendantUser(null)
        );

        Report report = reportRepository.save(
            TestDataFactory.assignedReport(user)
        );

        assertEquals(user.getEmail(), report.getAssignedTo().getEmail());
    }

    @Test
    void creationAndUpdateTimestampsAreSet() {
        Report report = reportRepository.save(
            TestDataFactory.report()
        );

        assertTrue(report.getCreatedOn() != null);
        assertTrue(report.getUpdatedOn() != null);
    }

    @Test
    void canUpdateReportStatus() {
        Report report = reportRepository.save(
            TestDataFactory.report()
        );
        
        report.setStatus(Status.RESOLVED);
        Report updated = reportRepository.save(report);

        assertEquals(Status.RESOLVED, updated.getStatus());
    }
}
