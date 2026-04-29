package se.voizter.felparkering.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import se.voizter.felparkering.api.security.JwtProvider;
import se.voizter.felparkering.api.service.AuthService;

@WebMvcTest(AuthController.class)
public class AuthControllerTests {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean AuthService authService;

    @MockitoBean JwtProvider jwtProvider;

    @Test
    void loginReturnsOkWithUserAndMessage() {
        // TODO: Write test
    }

    @Test
    void loginReturnsBadRequestWhenEmailIsMissing() {
        // TODO: Write test
    }

    @Test
    void loginReturnsBadRequestWhenPasswordIsMissing() {
        // TODO: Write test
    }

    @Test
    void loginReturnsBadRequestWhenEmailIsInvalid() {
        // TODO: Write test
    }

    @Test
    void loginReturnsNotFoundWhenServiceThrowsNotFoundException() {
        // TODO: Write test
    }

    @Test
    void loginReturnsForbiddenWhenServiceThrowsInvalidCredentialsException() {
        // TODO: Write test
    }


    @Test
    void registerReturnsOkWithUserAndMessage() {
        // TODO: Write test
    }

    @Test
    void registerReturnsBadRequestWhenEmailIsInvalid() {
        // TODO: Write test
    }

    @Test
    void registerReturnsBadRequestWhenPasswordIsTooShort() {
        // TODO: Write test
    }

    @Test
    void registerReturnsBadRequestWhenConfirmationPasswordIsMissing() {
        // TODO: Write test
    }

    @Test
    void registerReturnsConflictWhenServiceThrowsUserConflictException() {
        // TODO: Write test
    }

    @Test
    void registerReturnsBadRequestWhenServiceThrowsPasswordMismatchException() {
        // TODO: Write test
    }

}
