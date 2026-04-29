package se.voizter.felparkering.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import se.voizter.felparkering.api.repository.UserRepository;
import se.voizter.felparkering.api.security.JwtProvider;
import se.voizter.felparkering.api.service.ReportService;

@WebMvcTest(ReportController.class)
public class ReportControllerTests {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean ReportService reportService;

    @MockitoBean UserRepository userRepository;

    @MockitoBean JwtProvider jwtProvider;

    @Test
    void allReturnsReportsForAuthenticatedUser() {
        // TODO: Write test
    }

    @Test
    void allPassesPagingSortingSearchAndStatusToService() {
        // TODO: Write test
    }

    @Test
    void allReturnsUnauthorizedWithoutAuthentication() {
        // TODO: Write test
    }


    @Test
    void createReportReturnsOkWhenRequestIsValid() {
        // TODO: Write test
    }

    @Test
    void createReportReturnsBadRequestWhenRequestIsInvalid() {
        // TODO: Write test
    }

    @Test
    void createReportUsesCurrentUser() {
        // TODO: Write test
    }

    @Test
    void createReportReturnsForbiddenWhenServiceThrowsInvalidCredentialsException() {
        // TODO: Write test
    }


    @Test
    void getOneReturnsReportWhenUserCanAccess() {
        // TODO: Write test
    }

    @Test
    void getOneReturnsNotFoundWhenServiceThrowsNotFoundException() {
        // TODO: Write test
    }

    @Test
    void getOneReturnsForbiddenWhenServiceThrowsInvalidCredentialsException() {
        // TODO: Write test
    }


    @Test
    void updateStatusReturnsOkWhenRequestIsValid() {
        // TODO: Write test
    }

    @Test
    void updateStatusCallsServiceWithStatusAndId() {
        // TODO: Write test
    }

    @Test
    void updateStatusReturnsConflictWhenServiceThrowsAlreadyAssignedException() {
        // TODO: Write test
    }
}
