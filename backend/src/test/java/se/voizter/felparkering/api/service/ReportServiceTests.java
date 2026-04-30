package se.voizter.felparkering.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.voizter.felparkering.api.testsupport.TestDataFactory.address;
import static se.voizter.felparkering.api.testsupport.TestDataFactory.adminUserWithId;
import static se.voizter.felparkering.api.testsupport.TestDataFactory.attendantGroup;
import static se.voizter.felparkering.api.testsupport.TestDataFactory.attendantUserWithId;
import static se.voizter.felparkering.api.testsupport.TestDataFactory.customerUserWithId;
import static se.voizter.felparkering.api.testsupport.TestDataFactory.report;
import static se.voizter.felparkering.api.testsupport.TestDataFactory.reportRequest;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import se.voizter.felparkering.api.dto.ReportDetailDto;
import se.voizter.felparkering.api.dto.UserRequest;
import se.voizter.felparkering.api.enums.ParkingViolationCategory;
import se.voizter.felparkering.api.enums.Status;
import se.voizter.felparkering.api.exception.exceptions.AlreadyAssignedException;
import se.voizter.felparkering.api.exception.exceptions.InvalidCredentialsException;
import se.voizter.felparkering.api.exception.exceptions.NotFoundException;
import se.voizter.felparkering.api.model.Address;
import se.voizter.felparkering.api.model.AttendantGroup;
import se.voizter.felparkering.api.model.Report;
import se.voizter.felparkering.api.model.User;
import se.voizter.felparkering.api.repository.AddressRepository;
import se.voizter.felparkering.api.repository.AttendantGroupRepository;
import se.voizter.felparkering.api.repository.ReportRepository;
import se.voizter.felparkering.api.repository.UserRepository;
import se.voizter.felparkering.api.security.JwtProvider;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTests {
    
    @Mock
    AddressRepository addressRepository;

    @Mock
    ReportRepository reportRepository;

    @Mock
    AttendantGroupRepository groupRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    JwtProvider jwtProvider;

    @InjectMocks
    ReportService reportService;

    @Test
    void createReturnsReportWhenCustomerCreatesValidReport() {
        User customer = customerUserWithId(1L);
        Address address = address(10L, "Kungsgatan", "15", "Goteborg");
        AttendantGroup group = attendantGroup(20L, "Goteborg");
        when(addressRepository.findById(10L)).thenReturn(Optional.of(address));
        when(groupRepository.findByName("Goteborg")).thenReturn(Optional.of(group));

        ReportDetailDto result = reportService.create(
            customer,
            reportRequest(10L, "Kungsgatan", "15", "Goteborg", "abc123", ParkingViolationCategory.NO_PARKING_FEE_PAID)
        );

        assertEquals(address, result.address());
        assertEquals("ABC123", result.licensePlate());
        assertEquals(ParkingViolationCategory.NO_PARKING_FEE_PAID, result.category());
        assertEquals(group, result.attendantGroup());
        assertEquals(Status.NEW, result.status());
    }

    @Test
    void createReturnsReportWhenAdminCreatesValidReport() {
        User admin = adminUserWithId(2L);
        Address address = address(10L, "Kungsgatan", "15", "Goteborg");
        AttendantGroup group = attendantGroup(20L, "Goteborg");
        when(addressRepository.findById(10L)).thenReturn(Optional.of(address));
        when(groupRepository.findByName("Goteborg")).thenReturn(Optional.of(group));

        ReportDetailDto result = reportService.create(
            admin,
            reportRequest(10L, "Kungsgatan", "15", "Goteborg", "abc123", ParkingViolationCategory.NO_PARKING_FEE_PAID)
        );

        assertEquals("ABC123", result.licensePlate());
        assertEquals(Status.NEW, result.status());
    }

    @Test
    void createThrowsInvalidCredentialsWhenAttendantCreatesReport() {
        User attendant = attendantUserWithId(3L);

        assertThrows(
            InvalidCredentialsException.class,
            () -> reportService.create(
                attendant,
                reportRequest(10L, "Kungsgatan", "15", "Goteborg", "abc123", ParkingViolationCategory.NO_PARKING_FEE_PAID)
            )
        );
        verify(reportRepository, never()).save(any());
    }

    @Test
    void createThrowsNotFoundWhenAddressDoesNotExist() {
        when(addressRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(
            NotFoundException.class,
            () -> reportService.create(
                customerUserWithId(1L),
                reportRequest(10L, "Kungsgatan", "15", "Goteborg", "abc123", ParkingViolationCategory.NO_PARKING_FEE_PAID)
            )
        );
        verify(reportRepository, never()).save(any());
    }

    @Test
    void createThrowsNotFoundWhenAttendantGroupDoesNotExist() {
        Address address = address(10L, "Kungsgatan", "15", "Goteborg");
        when(addressRepository.findById(10L)).thenReturn(Optional.of(address));
        when(groupRepository.findByName("Goteborg")).thenReturn(Optional.empty());

        assertThrows(
            NotFoundException.class,
            () -> reportService.create(
                customerUserWithId(1L),
                reportRequest(10L, "Kungsgatan", "15", "Goteborg", "abc123", ParkingViolationCategory.NO_PARKING_FEE_PAID)
            )
        );
        verify(reportRepository, never()).save(any());
    }

    @Test
    void createUppercasesLicensePlate() {
        when(addressRepository.findById(10L)).thenReturn(Optional.of(address(10L, "Kungsgatan", "15", "Goteborg")));
        when(groupRepository.findByName("Goteborg")).thenReturn(Optional.of(attendantGroup(20L, "Goteborg")));

        reportService.create(
            customerUserWithId(1L),
            reportRequest(10L, "Kungsgatan", "15", "Goteborg", "abc123", ParkingViolationCategory.NO_PARKING_FEE_PAID)
        );

        ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).save(reportCaptor.capture());
        assertEquals("ABC123", reportCaptor.getValue().getLicensePlate());
    }

    @Test
    void createSetsStatusToNew() {
        when(addressRepository.findById(10L)).thenReturn(Optional.of(address(10L, "Kungsgatan", "15", "Goteborg")));
        when(groupRepository.findByName("Goteborg")).thenReturn(Optional.of(attendantGroup(20L, "Goteborg")));

        reportService.create(
            customerUserWithId(1L),
            reportRequest(10L, "Kungsgatan", "15", "Goteborg", "abc123", ParkingViolationCategory.NO_PARKING_FEE_PAID)
        );

        ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).save(reportCaptor.capture());
        assertEquals(Status.NEW, reportCaptor.getValue().getStatus());
    }

    @Test
    void createSetsCreatedByUser() {
        User customer = customerUserWithId(1L);
        when(addressRepository.findById(10L)).thenReturn(Optional.of(address(10L, "Kungsgatan", "15", "Goteborg")));
        when(groupRepository.findByName("Goteborg")).thenReturn(Optional.of(attendantGroup(20L, "Goteborg")));

        reportService.create(
            customer,
            reportRequest(10L, "Kungsgatan", "15", "Goteborg", "abc123", ParkingViolationCategory.NO_PARKING_FEE_PAID)
        );

        ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).save(reportCaptor.capture());
        assertSame(customer, reportCaptor.getValue().getCreatedBy());
    }

    @Test
    void createSetsAttendantGroupFromAddressCity() {
        Address address = address(10L, "Kungsgatan", "15", "Goteborg");
        AttendantGroup group = attendantGroup(20L, "Goteborg");
        when(addressRepository.findById(10L)).thenReturn(Optional.of(address));
        when(groupRepository.findByName("Goteborg")).thenReturn(Optional.of(group));

        reportService.create(
            customerUserWithId(1L),
            reportRequest(10L, "Kungsgatan", "15", "Goteborg", "abc123", ParkingViolationCategory.NO_PARKING_FEE_PAID)
        );

        ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).save(reportCaptor.capture());
        assertSame(group, reportCaptor.getValue().getAttendantGroup());
    }

    @Test
    void getReturnsReportWhenAdminRequestsAnyReport() {
        Report report = report(100L, customerUserWithId(1L), attendantGroup(20L, "Goteborg"), Status.NEW);
        when(reportRepository.findById(100L)).thenReturn(Optional.of(report));

        ReportDetailDto result = reportService.get(adminUserWithId(2L), 100L);

        assertEquals(100L, result.id());
    }

    @Test
    void getReturnsReportWhenCustomerRequestsOwnReport() {
        User customer = customerUserWithId(1L);
        Report report = report(100L, customer, attendantGroup(20L, "Goteborg"), Status.NEW);
        when(reportRepository.findById(100L)).thenReturn(Optional.of(report));

        ReportDetailDto result = reportService.get(customer, 100L);

        assertEquals(100L, result.id());
    }

    @Test
    void getThrowsInvalidCredentialsWhenCustomerRequestsOtherUsersReport() {
        Report report = report(100L, customerUserWithId(1L), attendantGroup(20L, "Goteborg"), Status.NEW);
        when(reportRepository.findById(100L)).thenReturn(Optional.of(report));

        assertThrows(
            InvalidCredentialsException.class,
            () -> reportService.get(customerUserWithId(2L), 100L)
        );
    }

    @Test
    void getReturnsReportWhenAttendantRequestsReportInSameGroup() {
        AttendantGroup group = attendantGroup(20L, "Goteborg");
        User attendant = attendantUserWithId(3L, group);
        Report report = report(100L, customerUserWithId(1L), group, Status.NEW);
        when(reportRepository.findById(100L)).thenReturn(Optional.of(report));

        ReportDetailDto result = reportService.get(attendant, 100L);

        assertEquals(100L, result.id());
    }

    @Test
    void getThrowsInvalidCredentialsWhenAttendantRequestsReportInOtherGroup() {
        User attendant = attendantUserWithId(3L, attendantGroup(21L, "Stockholm"));
        Report report = report(100L, customerUserWithId(1L), attendantGroup(20L, "Goteborg"), Status.NEW);
        when(reportRepository.findById(100L)).thenReturn(Optional.of(report));

        assertThrows(
            InvalidCredentialsException.class,
            () -> reportService.get(attendant, 100L)
        );
    }

    @Test
    void getThrowsNotFoundWhenReportDoesNotExist() {
        when(reportRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reportService.get(adminUserWithId(1L), 100L));
    }

    @Test
    void updateAllowsCustomerToCancelNewReport() {
        User customer = customerUserWithId(1L);
        Report report = report(100L, customer, attendantGroup(20L, "Goteborg"), Status.NEW);
        when(reportRepository.findById(100L)).thenReturn(Optional.of(report));

        ReportDetailDto result = reportService.update(customer, Status.CANCELLED, 100L);

        assertEquals(Status.CANCELLED, result.status());
        assertEquals(Status.CANCELLED, report.getStatus());
    }

    @Test
    void updateAllowsCustomerToCancelAssignedReport() {
        User customer = customerUserWithId(1L);
        Report report = report(100L, customer, attendantGroup(20L, "Goteborg"), Status.ASSIGNED);
        when(reportRepository.findById(100L)).thenReturn(Optional.of(report));

        ReportDetailDto result = reportService.update(customer, Status.CANCELLED, 100L);

        assertEquals(Status.CANCELLED, result.status());
    }

    @Test
    void updateThrowsInvalidCredentialsWhenCustomerSetsResolved() {
        User customer = customerUserWithId(1L);
        Report report = report(100L, customer, attendantGroup(20L, "Goteborg"), Status.NEW);
        when(reportRepository.findById(100L)).thenReturn(Optional.of(report));

        assertThrows(
            InvalidCredentialsException.class,
            () -> reportService.update(customer, Status.RESOLVED, 100L)
        );
    }

    @Test
    void updateThrowsInvalidCredentialsWhenCustomerUpdatesOtherUsersReport() {
        Report report = report(100L, customerUserWithId(1L), attendantGroup(20L, "Goteborg"), Status.NEW);
        when(reportRepository.findById(100L)).thenReturn(Optional.of(report));

        assertThrows(
            InvalidCredentialsException.class,
            () -> reportService.update(customerUserWithId(2L), Status.CANCELLED, 100L)
        );
    }

    @Test
    void updateAllowsAdminToSetAnyStatus() {
        Report report = report(100L, customerUserWithId(1L), attendantGroup(20L, "Goteborg"), Status.NEW);
        when(reportRepository.findById(100L)).thenReturn(Optional.of(report));

        ReportDetailDto result = reportService.update(adminUserWithId(2L), Status.RESOLVED, 100L);

        assertEquals(Status.RESOLVED, result.status());
        assertEquals(Status.RESOLVED, report.getStatus());
    }

    @Test
    void updateAllowsAttendantToAssignNewReportToSelf() {
        AttendantGroup group = attendantGroup(20L, "Goteborg");
        User attendant = attendantUserWithId(3L, group);
        Report report = report(100L, customerUserWithId(1L), group, Status.NEW);
        when(reportRepository.findById(100L)).thenReturn(Optional.of(report));

        ReportDetailDto result = reportService.update(attendant, Status.ASSIGNED, 100L);

        assertEquals(Status.ASSIGNED, result.status());
        assertEquals(3L, result.assignedToId());
        assertSame(attendant, report.getAssignedTo());
    }

    @Test
    void updateThrowsAlreadyAssignedWhenAttendantAssignsAlreadyAssignedReport() {
        AttendantGroup group = attendantGroup(20L, "Goteborg");
        User attendant = attendantUserWithId(3L, group);
        Report report = report(100L, customerUserWithId(1L), group, Status.ASSIGNED);
        report.setAssignedTo(attendant);
        when(reportRepository.findById(100L)).thenReturn(Optional.of(report));

        assertThrows(
            AlreadyAssignedException.class,
            () -> reportService.update(attendant, Status.ASSIGNED, 100L)
        );
    }

    @Test
    void updateThrowsAlreadyAssignedWhenReportAssignedToAnotherAttendant() {
        AttendantGroup group = attendantGroup(20L, "Goteborg");
        User attendant = attendantUserWithId(3L, group);
        Report report = report(100L, customerUserWithId(1L), group, Status.ASSIGNED);
        report.setAssignedTo(attendantUserWithId(4L, group));
        when(reportRepository.findById(100L)).thenReturn(Optional.of(report));

        assertThrows(
            AlreadyAssignedException.class,
            () -> reportService.update(attendant, Status.RESOLVED, 100L)
        );
    }

    @Test
    void updateAllowsAttendantToUnassignOwnAssignedReport() {
        AttendantGroup group = attendantGroup(20L, "Goteborg");
        User attendant = attendantUserWithId(3L, group);
        Report report = report(100L, customerUserWithId(1L), group, Status.ASSIGNED);
        report.setAssignedTo(attendant);
        when(reportRepository.findById(100L)).thenReturn(Optional.of(report));

        ReportDetailDto result = reportService.update(attendant, Status.NEW, 100L);

        assertEquals(Status.NEW, result.status());
        assertNull(result.assignedToId());
        assertNull(report.getAssignedTo());
    }

    @Test
    void updateAllowsAttendantToResolveOwnAssignedReport() {
        AttendantGroup group = attendantGroup(20L, "Goteborg");
        User attendant = attendantUserWithId(3L, group);
        Report report = report(100L, customerUserWithId(1L), group, Status.ASSIGNED);
        report.setAssignedTo(attendant);
        when(reportRepository.findById(100L)).thenReturn(Optional.of(report));

        ReportDetailDto result = reportService.update(attendant, Status.RESOLVED, 100L);

        assertEquals(Status.RESOLVED, result.status());
    }

    @Test
    void updateThrowsInvalidCredentialsWhenAttendantResolvesUnassignedReport() {
        AttendantGroup group = attendantGroup(20L, "Goteborg");
        User attendant = attendantUserWithId(3L, group);
        Report report = report(100L, customerUserWithId(1L), group, Status.NEW);
        when(reportRepository.findById(100L)).thenReturn(Optional.of(report));

        assertThrows(
            InvalidCredentialsException.class,
            () -> reportService.update(attendant, Status.RESOLVED, 100L)
        );
    }

    @Test
    void updateThrowsInvalidCredentialsWhenAttendantSetsCancelled() {
        AttendantGroup group = attendantGroup(20L, "Goteborg");
        User attendant = attendantUserWithId(3L, group);
        Report report = report(100L, customerUserWithId(1L), group, Status.NEW);
        when(reportRepository.findById(100L)).thenReturn(Optional.of(report));

        assertThrows(
            InvalidCredentialsException.class,
            () -> reportService.update(attendant, Status.CANCELLED, 100L)
        );
    }

    @Test
    void updateThrowsNotFoundWhenReportDoesNotExist() {
        when(reportRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(
            NotFoundException.class,
            () -> reportService.update(adminUserWithId(1L), Status.RESOLVED, 100L)
        );
    }

    @Test
    void getAllUsesAdminFiltersForAdmin() {
        when(reportRepository.findByFilters(eq(Status.NEW), eq("abc"), any(Pageable.class)))
            .thenReturn(Page.empty());

        reportService.getAll(0, 10, "createdOn", "desc", "abc", adminUserWithId(1L), Status.NEW, null);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(reportRepository).findByFilters(eq(Status.NEW), eq("abc"), pageableCaptor.capture());
        assertEquals(0, pageableCaptor.getValue().getPageNumber());
        assertEquals(10, pageableCaptor.getValue().getPageSize());
        assertEquals("createdOn: DESC", pageableCaptor.getValue().getSort().toString());
    }

    @Test
    void getAllUsesGroupFiltersForAttendant() {
        AttendantGroup group = attendantGroup(20L, "Goteborg");
        User attendant = attendantUserWithId(3L, group);
        when(reportRepository.findByFiltersInGroup(eq(Status.ASSIGNED), isNull(), eq(group), eq("abc"), any(Pageable.class)))
            .thenReturn(Page.empty());

        reportService.getAll(0, 10, "id", "asc", "abc", attendant, Status.ASSIGNED, null);

        verify(reportRepository).findByFiltersInGroup(eq(Status.ASSIGNED), isNull(), eq(group), eq("abc"), any(Pageable.class));
    }

    @Test
    void getAllUsesCreatedByFiltersForCustomer() {
        User customer = customerUserWithId(1L);
        when(reportRepository.findByFiltersCreatedBy(eq(Status.NEW), eq(customer), eq("abc"), any(Pageable.class)))
            .thenReturn(Page.empty());

        reportService.getAll(0, 10, "id", "asc", "abc", customer, Status.NEW, null);

        verify(reportRepository).findByFiltersCreatedBy(eq(Status.NEW), eq(customer), eq("abc"), any(Pageable.class));
    }

    @Test
    void getAllResolvesAssignedToFilterWhenProvided() {
        AttendantGroup group = attendantGroup(20L, "Goteborg");
        User attendant = attendantUserWithId(3L, group);
        User assignedTo = attendantUserWithId(4L, group);
        when(userRepository.findById(4L)).thenReturn(Optional.of(assignedTo));
        when(reportRepository.findByFiltersInGroup(isNull(), eq(assignedTo), eq(group), isNull(), any(Pageable.class)))
            .thenReturn(Page.empty());

        reportService.getAll(0, 10, "id", "asc", null, attendant, null, new UserRequest(4L));

        verify(reportRepository).findByFiltersInGroup(isNull(), eq(assignedTo), eq(group), isNull(), any(Pageable.class));
    }

    @Test
    void getAllThrowsNotFoundWhenAssignedToFilterUserDoesNotExist() {
        when(userRepository.findById(4L)).thenReturn(Optional.empty());

        assertThrows(
            NotFoundException.class,
            () -> reportService.getAll(0, 10, "id", "asc", null, adminUserWithId(1L), null, new UserRequest(4L))
        );
        verify(reportRepository, never()).findByFilters(any(), any(), any());
    }

    @Test
    void getAllMapsReportsToDetailDtos() {
        AttendantGroup group = attendantGroup(20L, "Goteborg");
        User assignee = attendantUserWithId(3L, group);
        Report report = report(100L, customerUserWithId(1L), group, Status.ASSIGNED);
        report.setAssignedTo(assignee);
        when(reportRepository.findByFilters(isNull(), isNull(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(report)));

        Page<ReportDetailDto> result = reportService.getAll(0, 10, "id", "asc", null, adminUserWithId(2L), null, null);

        assertEquals(1, result.getTotalElements());
        ReportDetailDto dto = result.getContent().get(0);
        assertEquals(100L, dto.id());
        assertEquals("ABC123", dto.licensePlate());
        assertEquals(ParkingViolationCategory.NO_PARKING_AREA, dto.category());
        assertEquals(group, dto.attendantGroup());
        assertEquals(3L, dto.assignedToId());
        assertEquals(Status.ASSIGNED, dto.status());
    }

}

