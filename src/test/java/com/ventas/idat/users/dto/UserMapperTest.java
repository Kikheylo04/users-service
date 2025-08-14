package com.ventas.idat.users.dto;

import com.ventas.idat.users.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

class UserMapperTest {

    @Test
    void constructor_privado_noInstanciable_y_lanzaAssertionError() throws Exception {
        Constructor<UserMapper> ctor = UserMapper.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(ctor.getModifiers()), "El constructor debe ser privado");
        ctor.setAccessible(true);

        InvocationTargetException ex = assertThrows(InvocationTargetException.class, ctor::newInstance);

        assertTrue(ex.getCause() instanceof AssertionError,
                "El constructor debe lanzar AssertionError");
    }

    @Test
    void toDTO_mapeaTodosLosCampos() {
        User u = new User();
        u.setId(1L);
        u.setFirstName("John");
        u.setLastName("Doe");
        u.setUsername("jdoe");
        u.setRole("ADMIN");

        UserDTO dto = UserMapper.toDTO(u);

        assertEquals(1L, dto.getId());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("jdoe", dto.getUsername());
        assertEquals("ADMIN", dto.getRole());
    }

    @Test
    void toDTO_manejaNulosSinExcepcion() {
        User u = new User();
        UserDTO dto = UserMapper.toDTO(u);

        assertNull(dto.getId());
        assertNull(dto.getFirstName());
        assertNull(dto.getLastName());
        assertNull(dto.getUsername());
        assertNull(dto.getRole());
    }
}
