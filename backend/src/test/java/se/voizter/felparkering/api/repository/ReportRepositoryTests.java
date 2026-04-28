package se.voizter.felparkering.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

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

    @Test
    void canSaveAndFindById() {
        Report report = TestDataFactory.report();
        
        reportRepository.save(report);
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
        // TODO: Write test
    }

    @Test
    void findByCreatedBy() {
        // TODO: Write test
    }

    @Test
    void findByFilters() {
        // TODO: Write test
    }

    @Test
    void findByFiltersInGroup() {
        // TODO: Write test
    }

    @Test
    void findByFiltersCreatedBy() {
        // TODO: Write test
    }

    @Test
    void canSaveReportWithDuplicateParams() {
        Report report1 = TestDataFactory.report();
        reportRepository.save(report1);

        Report report2 = TestDataFactory.report();
        reportRepository.save(report2);

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
        AttendantGroup group = TestDataFactory.attendantGroup("Testgruppen");
        AttendantGroup savedGroup = groupRepository.save(group);

        Report report = TestDataFactory.reportWithGroup(savedGroup);
        Report savedReport = reportRepository.save(report);

        assertEquals("Testgruppen", savedReport.getAttendantGroup().getName());
    }

    @Test 
    void canSaveReportWithAssignedUser() {
        User user = TestDataFactory.attendantUser();
        user.setAttendantGroup(null);
        User savedUser = userRepository.save(user);

        Report report = TestDataFactory.assignedReport(savedUser);
        Report saved = reportRepository.save(report);

        assertEquals(savedUser.getEmail(), saved.getAssignedTo().getEmail());
    }

    @Test
    void creationAndUpdateTimestampsAreSet() {
        Report report = TestDataFactory.report();
        Report saved = reportRepository.save(report);

        assertTrue(saved.getCreatedOn() != null);
        assertTrue(saved.getUpdatedOn() != null);
    }

    @Test
    void canUpdateReportStatus() {
        Report report = TestDataFactory.report();
        Report saved = reportRepository.save(report);
        saved.setStatus(Status.RESOLVED);
        Report updated = reportRepository.save(saved);

        assertEquals(Status.RESOLVED, updated.getStatus());
    }
}
