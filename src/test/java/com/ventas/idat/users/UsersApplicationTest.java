package com.ventas.idat.users;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class UsersApplicationTest {

    @Test
    void main_iniciaSinErrores() {
        assertDoesNotThrow(() -> UsersApplication.main(new String[]{}));
    }
}
