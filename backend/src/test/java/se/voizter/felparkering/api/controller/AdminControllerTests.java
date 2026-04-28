package se.voizter.felparkering.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import se.voizter.felparkering.api.security.JwtProvider;
import se.voizter.felparkering.api.service.AdminService;

@WebMvcTest(AdminController.class)
public class AdminControllerTests {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean AdminService adminService;

    @MockitoBean JwtProvider jwtProvider;

    @Test
    void allUsersReturnsUsersForAdmin() {
        // TODO: Write test
    }

    @Test
    void allUsersReturnsForbiddenForNonAdmin() {
        // TODO: Write test
    }

    @Test
    void allUsersReturnsUnauthorizedWithoutAuthentication() {
        // TODO: Write test
    }


    @Test
    void createAttendantReturnsOkForAdmin() {
        // TODO: Write test
    }

    @Test
    void createAttendantCallsService() {
        // TODO: Write test
    }

    @Test
    void deleteUserReturnsOkForAdmin() {
        // TODO: Write test
    }

    @Test
    void deleteUserCallsServiceWithPathId() {
        // TODO: Write test
    }


    @Test
    void allAttendantGroupsReturnsGroupsForAdmin() {
        // TODO: Write test
    }
}
