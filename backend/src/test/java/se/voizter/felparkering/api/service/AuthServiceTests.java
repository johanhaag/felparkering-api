package se.voizter.felparkering.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        // TODO: Write test
    }

    @Test
    void loginThrowsNotFoundExceptionWhenEmailIsMissing() {
        // TODO: Write test
    }

    @Test
    void loginThrowsInvalidCredentialsExceptionWhenPasswordIsWrong() {
        // TODO: Write test
    }

    @Test
    void registerSavesCustomerAndReturnsToken() {
        // TODO: Write test
    }

    @Test
    void registerThrowsUserConflictExceptionWhenEmailAlreadyExists() {
        // TODO: Write test
    }

    @Test
    void registerThrowsPasswordMismatchExceptionWhenPasswordsDiffer() {
        // TODO: Write test
    }
}
