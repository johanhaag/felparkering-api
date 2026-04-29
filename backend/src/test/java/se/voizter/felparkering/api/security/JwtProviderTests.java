package se.voizter.felparkering.api.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.JwtException;
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
    void validateTokenReturnsTrueForGeneratedToken() {
        String token = jwtProvider.generateToken(1L, Role.ADMIN);

        assertNotNull(token);
        assertTrue(jwtProvider.validateToken(token));
        assertEquals(1L, jwtProvider.getId(token));
    }

    @Test
    void validateTokenReturnsFalseForTamperedToken() {
        String token = jwtProvider.generateToken(1L, Role.ADMIN);
        String tamperedToken = token + "a";

        assertFalse(jwtProvider.validateToken(tamperedToken));
    }

    @Test
    void validateTokenReturnsFalseForExpiredToken() throws InterruptedException {
        ReflectionTestUtils.setField(jwtProvider, "expiration", 1L);
        String token = jwtProvider.generateToken(1L, Role.ADMIN);

        Thread.sleep(10);

        assertFalse(jwtProvider.validateToken(token));
    }

    @Test
    void getRoleReturnsTokenRole() {
        for (Role role : Role.values()) {
            String token = jwtProvider.generateToken(1L, role);

            assertEquals(role.name(), jwtProvider.getRole(token));
        }
    }

    @Test
    void getIdReturnsTokenSubjectAsLong() {
        String token = jwtProvider.generateToken(42L, Role.CUSTOMER);

        assertEquals(42L, jwtProvider.getId(token));
    }

    @Test
    void validateTokenReturnsFalseForMalformedToken() {
        assertFalse(jwtProvider.validateToken("not-a-jwt"));
    }

    @Test
    void getIdThrowsForInvalidToken() {
        assertThrows(JwtException.class, () -> jwtProvider.getId("not-a-jwt"));
    }
}
