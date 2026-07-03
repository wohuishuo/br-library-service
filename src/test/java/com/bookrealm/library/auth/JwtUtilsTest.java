package com.bookrealm.library.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtUtilsTest {

    @Test
    void constructor_shouldRejectBlankSecret() {
        IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> new JwtUtils("   ")
        );

        assertEquals("jwt.secret or JWT_SECRET must be configured", thrown.getMessage());
    }
}
