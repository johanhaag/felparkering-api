package se.voizter.felparkering.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.voizter.felparkering.api.testsupport.TestDataFactory.customerUserWithId;
import static se.voizter.felparkering.api.testsupport.TestDataFactory.loginRequest;
import static se.voizter.felparkering.api.testsupport.TestDataFactory.registerRequest;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.voizter.felparkering.api.dto.UserDetailDto;
import se.voizter.felparkering.api.enums.Role;
import se.voizter.felparkering.api.exception.exceptions.InvalidCredentialsException;
import se.voizter.felparkering.api.exception.exceptions.NotFoundException;
import se.voizter.felparkering.api.exception.exceptions.PasswordMismatchException;
import se.voizter.felparkering.api.exception.exceptions.UserConflictException;
import se.voizter.felparkering.api.model.User;
import se.voizter.felparkering.api.repository.UserRepository;
import se.voizter.felparkering.api.security.JwtProvider;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {
    
    @Mock
    UserRepository userRepository;

    @Mock
    JwtProvider jwtProvider;

    @InjectMocks
    AuthService authService;

    @Test
    void loginReturnsTokenWhenCredentialsAreValid() {
        User user = customerUserWithId(10L);
        when(userRepository.findByEmail("customer@example.com"))
            .thenReturn(Optional.of(user));
        when(jwtProvider.generateToken(10L, Role.CUSTOMER))
            .thenReturn("jwt-token");

        UserDetailDto result = authService.login(loginRequest("customer@example.com", "password123"));

        assertEquals("jwt-token", result.token());
    }

    @Test
    void loginThrowsNotFoundExceptionWhenEmailIsMissing() {
        when(userRepository.findByEmail("missing@example.com"))
            .thenReturn(Optional.empty());

        assertThrows(
            NotFoundException.class,
            () -> authService.login(loginRequest("missing@example.com", "password123"))
        );
        verify(jwtProvider, never()).generateToken(any(), any());
    }

    @Test
    void loginThrowsInvalidCredentialsExceptionWhenPasswordIsWrong() {
        User user = customerUserWithId(10L);
        when(userRepository.findByEmail("customer@example.com"))
            .thenReturn(Optional.of(user));

        assertThrows(
            InvalidCredentialsException.class,
            () -> authService.login(loginRequest("customer@example.com", "wrongpass"))
        );
        verify(jwtProvider, never()).generateToken(any(), any());
    }

    @Test
    void registerSavesCustomerAndReturnsToken() {
        when(userRepository.findByEmail("new@example.com"))
            .thenReturn(Optional.empty());
        when(jwtProvider.generateToken(null, Role.CUSTOMER))
            .thenReturn("new-token");

        UserDetailDto result = authService.register(
            registerRequest("new@example.com", "password123", "password123")
        );

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();
        assertEquals("new@example.com", saved.getEmail());
        assertEquals("password123", saved.getPassword());
        assertEquals(Role.CUSTOMER, saved.getRole());
        assertEquals("new-token", result.token());
    }

    @Test
    void registerThrowsUserConflictExceptionWhenEmailAlreadyExists() {
        when(userRepository.findByEmail("existing@example.com"))
            .thenReturn(Optional.of(customerUserWithId(1L)));

        assertThrows(
            UserConflictException.class,
            () -> authService.register(registerRequest("existing@example.com", "password123", "password123"))
        );
        verify(userRepository, never()).save(any());
        verify(jwtProvider, never()).generateToken(any(), any());
    }

    @Test
    void registerThrowsPasswordMismatchExceptionWhenPasswordsDiffer() {
        when(userRepository.findByEmail("new@example.com"))
            .thenReturn(Optional.empty());

        assertThrows(
            PasswordMismatchException.class,
            () -> authService.register(registerRequest("new@example.com", "password123", "different123"))
        );
        verify(userRepository, never()).save(any());
        verify(jwtProvider, never()).generateToken(any(), any());
    }

}
