package com.ventas.idat.users.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JWTUtilTest {

    private JWTUtil jwtUtil;

    private static final String TEST_SECRET = "01234567890123456789012345678901";

    @BeforeEach
    void setUp() {
        jwtUtil = new JWTUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", TEST_SECRET);
    }

    @Test
    void generate_validate_and_extract_ok() {
        var auths = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        String token = jwtUtil.generateToken("john", auths);
        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));

        String username = jwtUtil.extractUsername(token);
        assertEquals("john", username);
    }

    @Test
    void validateToken_devuelveFalse_siTokenEsManipulado() {
        var auths = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        String token = jwtUtil.generateToken("john", auths);

        String tampered = token + "x";

        assertFalse(jwtUtil.validateToken(tampered));
    }
}
