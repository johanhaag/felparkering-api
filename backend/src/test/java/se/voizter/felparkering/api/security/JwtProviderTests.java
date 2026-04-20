package se.voizter.felparkering.api.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import se.voizter.felparkering.api.enums.Role;

public class JwtProviderTests {
    
    private JwtProvider jwtProvider;

    private final String secret = "my-secret-test-key-that-is-very-secretive!";
    private final long expirationMs = 1000 * 60;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider();

        ReflectionTestUtils.setField(jwtProvider, "secretKey", secret);
        ReflectionTestUtils.setField(jwtProvider, "expiration", expirationMs);
    }

    @Test
    void testGenerateAndValidateToken() {
        String token = jwtProvider.generateToken(1L, Role.ADMIN);

        assertNotNull(token);
        assertTrue(jwtProvider.validateToken(token));
        assertEquals(1L, jwtProvider.getId(token));
    }

    @Test
    void testValidateTokenReturnsFalseForTamperedToken() {
        String token = jwtProvider.generateToken(1L, Role.ADMIN);
        String tamperedToken = token + "a";

        assertFalse(jwtProvider.validateToken(tamperedToken));
    }

    @Test
    void testValidateTokenReturnsFalseForExpiredToken() throws InterruptedException {
        ReflectionTestUtils.setField(jwtProvider, "expiration", 1L);
        String token = jwtProvider.generateToken(1L, Role.ADMIN);

        Thread.sleep(10);

        assertFalse(jwtProvider.validateToken(token));
    }
}
