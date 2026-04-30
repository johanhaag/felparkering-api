package se.voizter.felparkering.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import se.voizter.felparkering.api.configuration.SecurityConfig;
import se.voizter.felparkering.api.dto.AddressDto;
import se.voizter.felparkering.api.dto.AttendantGroupDto;
import se.voizter.felparkering.api.dto.ReportDetailDto;
import se.voizter.felparkering.api.dto.ReportRequest;
import se.voizter.felparkering.api.enums.Message;
import se.voizter.felparkering.api.enums.ParkingViolationCategory;
import se.voizter.felparkering.api.enums.Status;
import se.voizter.felparkering.api.exception.exceptions.AlreadyAssignedException;
import se.voizter.felparkering.api.exception.exceptions.InvalidCredentialsException;
import se.voizter.felparkering.api.exception.exceptions.NotFoundException;
import se.voizter.felparkering.api.model.Address;
import se.voizter.felparkering.api.model.AttendantGroup;
import se.voizter.felparkering.api.model.User;
import se.voizter.felparkering.api.repository.UserRepository;
import se.voizter.felparkering.api.security.JwtProvider;
import se.voizter.felparkering.api.service.ReportService;
import se.voizter.felparkering.api.testsupport.OpenApiValidation;
import se.voizter.felparkering.api.testsupport.TestDataFactory;

@WebMvcTest(ReportController.class)
@Import(SecurityConfig.class)
public class ReportControllerTests {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean ReportService reportService;
    @MockitoBean UserRepository userRepository;
    @MockitoBean JwtProvider jwtProvider;

    @Test
    void allReturnsReportsForAuthenticatedUser() throws Exception {
        User user = TestDataFactory.customerUserWithId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reportService.getAll(
                eq(0), eq(10), eq("createdOn"), eq("desc"),
                eq(null), eq(user), eq(null), eq(null)))
            .thenReturn(new PageImpl<>(List.of(reportDetail(12L)), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/reports")
                .header("Authorization", "Bearer test-token")
                .with(authentication(auth(1L, "ROLE_CUSTOMER"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.items[0].id").value(12))
            .andExpect(OpenApiValidation.matchesOpenApiSpec());
    }

    @Test
    void allPassesPagingSortingSearchAndStatusToService() throws Exception {
        User user = TestDataFactory.adminUserWithId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reportService.getAll(
                eq(2), eq(5), eq("status"), eq("asc"),
                eq("abc"), eq(user), eq(Status.NEW), eq(null)))
            .thenReturn(new PageImpl<>(List.of(), PageRequest.of(2, 5), 0));

        mockMvc.perform(get("/reports")
                .param("page", "2")
                .param("size", "5")
                .param("sortBy", "status")
                .param("sortDir", "asc")
                .param("search", "abc")
                .param("status", "NEW")
                .with(authentication(auth(1L, "ROLE_ADMIN"))))
            .andExpect(status().isOk());

        verify(reportService).getAll(2, 5, "status", "asc", "abc", user, Status.NEW, null);
    }

    @Test
    void allReturnsUnauthorizedWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/reports"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void createReportReturnsCreatedWhenRequestIsValid() throws Exception {
        User user = TestDataFactory.customerUserWithId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reportService.create(eq(user), any(ReportRequest.class)))
            .thenReturn(reportDetail(12L));

        mockMvc.perform(post("/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer test-token")
                .content(objectMapper.writeValueAsString(TestDataFactory.reportRequest()))
                .with(authentication(auth(1L, "ROLE_CUSTOMER"))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message").value("Report created successfully"))
            .andExpect(jsonPath("$.data.id").value(12))
            .andExpect(jsonPath("$.data.createdOn").exists())
            .andExpect(OpenApiValidation.matchesOpenApiSpec());
    }

    @Test
    void createReportReturnsBadRequestWhenRequestIsInvalid() throws Exception {
        ReportRequest request = TestDataFactory.reportRequest(null, "", "2", "", "bad", null);

        mockMvc.perform(post("/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(authentication(auth(1L, "ROLE_CUSTOMER"))))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createReportUsesCurrentUser() throws Exception {
        User user = TestDataFactory.customerUserWithId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reportService.create(eq(user), any(ReportRequest.class)))
            .thenReturn(reportDetail(12L));

        mockMvc.perform(post("/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestDataFactory.reportRequest()))
                .with(authentication(auth(1L, "ROLE_CUSTOMER"))))
            .andExpect(status().isCreated());

        verify(reportService).create(eq(user), any(ReportRequest.class));
    }

    @Test
    void createReportReturnsForbiddenWhenServiceThrowsInvalidCredentialsException() throws Exception {
        User user = TestDataFactory.attendantUserWithId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reportService.create(eq(user), any(ReportRequest.class)))
            .thenThrow(new InvalidCredentialsException(Message.REPORT_NO_PERMISSION.toString()));

        mockMvc.perform(post("/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestDataFactory.reportRequest()))
                .with(authentication(auth(1L, "ROLE_ATTENDANT"))))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error.message").value(Message.REPORT_NO_PERMISSION.toString()));
    }

    @Test
    void getOneReturnsReportWhenUserCanAccess() throws Exception {
        User user = TestDataFactory.customerUserWithId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reportService.get(user, 12L)).thenReturn(reportDetail(12L));

        mockMvc.perform(get("/reports/12")
                .header("Authorization", "Bearer test-token")
                .with(authentication(auth(1L, "ROLE_CUSTOMER"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(12))
            .andExpect(OpenApiValidation.matchesOpenApiSpec());
    }

    @Test
    void getOneReturnsNotFoundWhenServiceThrowsNotFoundException() throws Exception {
        User user = TestDataFactory.customerUserWithId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reportService.get(user, 99L))
            .thenThrow(new NotFoundException(Message.REPORT_NOT_FOUND.toString()));

        mockMvc.perform(get("/reports/99")
                .with(authentication(auth(1L, "ROLE_CUSTOMER"))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error.message").value(Message.REPORT_NOT_FOUND.toString()));
    }

    @Test
    void getOneReturnsForbiddenWhenServiceThrowsInvalidCredentialsException() throws Exception {
        User user = TestDataFactory.customerUserWithId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reportService.get(user, 12L))
            .thenThrow(new InvalidCredentialsException(Message.REPORT_NO_PERMISSION.toString()));

        mockMvc.perform(get("/reports/12")
                .with(authentication(auth(1L, "ROLE_CUSTOMER"))))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error.message").value(Message.REPORT_NO_PERMISSION.toString()));
    }

    @Test
    void updateStatusReturnsOkWhenRequestIsValid() throws Exception {
        User user = TestDataFactory.adminUserWithId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reportService.update(user, Status.RESOLVED, 12L))
            .thenReturn(reportDetail(12L, Status.RESOLVED));

        mockMvc.perform(put("/reports/12")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer test-token")
                .content(objectMapper.writeValueAsString(TestDataFactory.updateStatusRequest(Status.RESOLVED)))
                .with(authentication(auth(1L, "ROLE_ADMIN"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Report updated successfully"))
            .andExpect(jsonPath("$.data.id").value(12))
            .andExpect(jsonPath("$.data.status").value(Status.RESOLVED.toString()))
            .andExpect(OpenApiValidation.matchesOpenApiSpec());
    }

    @Test
    void updateStatusCallsServiceWithStatusAndId() throws Exception {
        User user = TestDataFactory.adminUserWithId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reportService.update(user, Status.RESOLVED, 12L))
            .thenReturn(reportDetail(12L, Status.RESOLVED));

        mockMvc.perform(put("/reports/12")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestDataFactory.updateStatusRequest(Status.RESOLVED)))
                .with(authentication(auth(1L, "ROLE_ADMIN"))))
            .andExpect(status().isOk());

        verify(reportService).update(user, Status.RESOLVED, 12L);
    }

    @Test
    void updateStatusReturnsConflictWhenServiceThrowsAlreadyAssignedException() throws Exception {
        User user = TestDataFactory.attendantUserWithId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reportService.update(user, Status.ASSIGNED, 12L))
            .thenThrow(new AlreadyAssignedException(Message.REPORT_ALREADY_ASSIGNED.toString()));

        mockMvc.perform(put("/reports/12")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestDataFactory.updateStatusRequest(Status.ASSIGNED)))
                .with(authentication(auth(1L, "ROLE_ATTENDANT"))))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error.message").value(Message.REPORT_ALREADY_ASSIGNED.toString()));
    }

    private static UsernamePasswordAuthenticationToken auth(Long id, String role) {
        return new UsernamePasswordAuthenticationToken(
            id,
            null,
            List.of(new SimpleGrantedAuthority(role))
        );
    }

    private static ReportDetailDto reportDetail(Long id) {
        return reportDetail(id, Status.NEW);
    }

    private static ReportDetailDto reportDetail(Long id, Status status) {
        Address address = TestDataFactory.address(1L, "Testgatan", "2", "Teststad");
        AttendantGroup group = TestDataFactory.attendantGroup(1L, "Testgruppen");
        return new ReportDetailDto(
            id,
            AddressDto.fromEntity(address),
            "ABC123",
            ParkingViolationCategory.NO_PARKING_AREA,
            AttendantGroupDto.fromEntity(group),
            null,
            Instant.parse("2026-04-30T10:00:00Z"),
            Instant.parse("2026-04-30T10:05:00Z"),
            status
        );
    }
}
