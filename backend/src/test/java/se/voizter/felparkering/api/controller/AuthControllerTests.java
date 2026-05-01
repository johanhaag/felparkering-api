package se.voizter.felparkering.api.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import se.voizter.felparkering.api.configuration.SecurityConfig;
import se.voizter.felparkering.api.dto.LoginRequest;
import se.voizter.felparkering.api.dto.RegisterRequest;
import se.voizter.felparkering.api.dto.UserDetailDto;
import se.voizter.felparkering.api.enums.Message;
import se.voizter.felparkering.api.exception.exceptions.InvalidCredentialsException;
import se.voizter.felparkering.api.exception.exceptions.NotFoundException;
import se.voizter.felparkering.api.exception.exceptions.PasswordMismatchException;
import se.voizter.felparkering.api.exception.exceptions.UserConflictException;
import se.voizter.felparkering.api.security.JwtProvider;
import se.voizter.felparkering.api.service.AuthService;
import se.voizter.felparkering.api.testsupport.OpenApiValidation;
import se.voizter.felparkering.api.testsupport.TestDataFactory;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTests {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean AuthService authService;
    @MockitoBean JwtProvider jwtProvider;

    @Test
    void loginReturnsOkWithUserAndMessage() throws Exception {
        when(authService.login(any(LoginRequest.class)))
            .thenReturn(new UserDetailDto("jwt-token"));

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestDataFactory.loginRequest())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.token").value("jwt-token"))
            .andExpect(jsonPath("$.message").value(Message.LOGIN.toString()))
            .andExpect(OpenApiValidation.matchesOpenApiSpec());
    }

    @Test
    void loginReturnsBadRequestWhenEmailIsMissing() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    TestDataFactory.loginRequest(null, "password123"))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[*].field", hasItem("email")));
    }

    @Test
    void loginReturnsBadRequestWhenPasswordIsMissing() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    TestDataFactory.loginRequest("customer@example.com", null))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[*].field", hasItem("password")));
    }

    @Test
    void loginReturnsBadRequestWhenEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                TestDataFactory.loginRequest("not-an-email", "password123"))))
        .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[*].field", hasItem("email")))
            .andExpect(OpenApiValidation.matchesOpenApiSpec());
    }

    @Test
    void loginReturnsNotFoundWhenServiceThrowsNotFoundException() throws Exception {
        when(authService.login(any(LoginRequest.class)))
            .thenThrow(new NotFoundException(Message.USER_NOT_FOUND.toString()));

        mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(TestDataFactory.loginRequest())))
        .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error.message").value(Message.USER_NOT_FOUND.toString()))
            .andExpect(OpenApiValidation.matchesOpenApiSpec());
    }

    @Test
    void loginReturnsUnauthorizedWhenServiceThrowsInvalidCredentialsException() throws Exception {
        when(authService.login(any(LoginRequest.class)))
            .thenThrow(new InvalidCredentialsException(Message.INVALID_CREDENTIALS.toString()));

        mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(TestDataFactory.loginRequest())))
        .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error.message").value(Message.INVALID_CREDENTIALS.toString()))
            .andExpect(OpenApiValidation.matchesOpenApiSpec());
    }

    @Test
    void registerReturnsCreatedWithUserAndMessage() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
            .thenReturn(new UserDetailDto("jwt-token"));

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestDataFactory.registerRequest())))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.token").value("jwt-token"))
            .andExpect(jsonPath("$.message").value(Message.REGISTER.toString()))
            .andExpect(OpenApiValidation.matchesOpenApiSpec());
    }

    @Test
    void registerReturnsBadRequestWhenEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                TestDataFactory.registerRequest("bad-email", "password123"))))
        .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[*].field", hasItem("email")))
            .andExpect(OpenApiValidation.matchesOpenApiSpec());
    }

    @Test
    void registerReturnsBadRequestWhenPasswordIsTooShort() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                TestDataFactory.registerRequest("customer@example.com", "short"))))
        .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[*].field", hasItem("password")))
            .andExpect(OpenApiValidation.matchesOpenApiSpec());
    }

    @Test
    void registerReturnsBadRequestWhenConfirmationPasswordIsMissing() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    TestDataFactory.registerRequest("customer@example.com", "password123", null))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[*].field", hasItem("confPassword")));
    }

    @Test
    void registerReturnsConflictWhenServiceThrowsUserConflictException() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
            .thenThrow(new UserConflictException(Message.USER_EXISTS.toString()));

        mockMvc.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(TestDataFactory.registerRequest())))
        .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error.message").value(Message.USER_EXISTS.toString()))
            .andExpect(OpenApiValidation.matchesOpenApiSpec());
    }

    @Test
    void registerReturnsBadRequestWhenServiceThrowsPasswordMismatchException() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
            .thenThrow(new PasswordMismatchException(Message.PASSWORD_MISMATCH.toString()));

        mockMvc.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(TestDataFactory.registerRequest())))
        .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error.message").value(Message.PASSWORD_MISMATCH.toString()))
            .andExpect(OpenApiValidation.matchesOpenApiSpec());
    }
}
