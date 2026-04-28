package se.voizter.felparkering.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        // TODO: Write test
    }

    @Test
    void createReturnsReportWhenAdminCreatesValidReport() {
        // TODO: Write test
    }

    @Test
    void createThrowsInvalidCredentialsWhenAttendantCreatesReport() {
        // TODO: Write test
    }

    @Test
    void createThrowsNotFoundWhenAddressDoesNotExist() {
        // TODO: Write test
    }

    @Test
    void createThrowsNotFoundWhenAttendantGroupDoesNotExist() {
        // TODO: Write test
    }

    @Test
    void createUppercasesLicensePlate() {
        // TODO: Write test
    }

    @Test
    void createSetsStatusToNew() {
        // TODO: Write test
    }

    @Test
    void createSetsCreatedByUser() {
        // TODO: Write test
    }

    @Test
    void createSetsAttendantGroupFromAddressCity() {
        // TODO: Write test
    }

    @Test
    void getReturnsReportWhenAdminRequestsAnyReport() {
        // TODO: Write test
    }

    @Test
    void getReturnsReportWhenCustomerRequestsOwnReport() {
        // TODO: Write test
    }

    @Test
    void getThrowsInvalidCredentialsWhenCustomerRequestsOtherUsersReport() {
        // TODO: Write test
    }

    @Test
    void getReturnsReportWhenAttendantRequestsReportInSameGroup() {
        // TODO: Write test
    }

    @Test
    void getThrowsInvalidCredentialsWhenAttendantRequestsReportInOtherGroup() {
        // TODO: Write test
    }

    @Test
    void getThrowsNotFoundWhenReportDoesNotExist() {
        // TODO: Write test
    }

    @Test
    void updateAllowsCustomerToCancelNewReport() {
        // TODO: Write test
    }

    @Test
    void updateAllowsCustomerToCancelAssignedReport() {
        // TODO: Write test
    }

    @Test
    void updateThrowsInvalidCredentialsWhenCustomerSetsResolved() {
        // TODO: Write test
    }

    @Test
    void updateThrowsInvalidCredentialsWhenCustomerUpdatesOtherUsersReport() {
        // TODO: Write test
    }


    @Test
    void updateAllowsAdminToSetAnyStatus() {
        // TODO: Write test
    }


    @Test
    void updateAllowsAttendantToAssignNewReportToSelf() {
        // TODO: Write test
    }

    @Test
    void updateThrowsAlreadyAssignedWhenAttendantAssignsAlreadyAssignedReport() {
        // TODO: Write test
    }

    @Test
    void updateThrowsAlreadyAssignedWhenReportAssignedToAnotherAttendant() {
        // TODO: Write test
    }

    @Test
    void updateAllowsAttendantToUnassignOwnAssignedReport() {
        // TODO: Write test
    }

    @Test
    void updateAllowsAttendantToResolveOwnAssignedReport() {
        // TODO: Write test
    }

    @Test
    void updateThrowsInvalidCredentialsWhenAttendantResolvesUnassignedReport() {
        // TODO: Write test
    }

    @Test
    void updateThrowsInvalidCredentialsWhenAttendantSetsCancelled() {
        // TODO: Write test
    }

    @Test
    void updateThrowsNotFoundWhenReportDoesNotExist() {
        // TODO: Write test
    }

    @Test
    void getAllUsesAdminFiltersForAdmin() {
        // TODO: Write test
    }

    @Test
    void getAllUsesGroupFiltersForAttendant() {
        // TODO: Write test
    }

    @Test
    void getAllUsesCreatedByFiltersForCustomer() {
        // TODO: Write test
    }

    @Test
    void getAllResolvesAssignedToFilterWhenProvided() {
        // TODO: Write test
    }

    @Test
    void getAllThrowsNotFoundWhenAssignedToFilterUserDoesNotExist() {
        // TODO: Write test
    }

    @Test
    void getAllMapsReportsToDetailDtos() {
        // TODO: Write test
    }

}
