package com.ventas.idat.users.security;

import com.ventas.idat.users.model.User;
import com.ventas.idat.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl service;

    @Test
    void loadUserByUsername_roleSinPrefijo_devuelveROLE_ADMIN() {
        User u = new User();
        u.setPassword("123");
        u.setRole("ADMIN");
        Mockito.when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(u));

        UserDetails details = service.loadUserByUsername("john");

        assertEquals("john", details.getUsername());
        assertEquals("123", details.getPassword());
        assertTrue(tieneAuthority(details.getAuthorities(), "ROLE_ADMIN"));
    }

    @Test
    void loadUserByUsername_roleConPrefijo_noDuplicaPrefijo() {
        User u = new User();
        u.setPassword("abc");
        u.setRole("ROLE_USER");

        Mockito.when(userRepository.findByUsername("ana"))
                .thenReturn(Optional.of(u));

        UserDetails details = service.loadUserByUsername("ana");

        assertEquals("ana", details.getUsername());
        assertEquals("abc", details.getPassword());
        assertTrue(tieneAuthority(details.getAuthorities(), "ROLE_USER"));
        assertFalse(tieneAuthority(details.getAuthorities(), "ROLE_ROLE_USER"));
    }

    @Test
    void loadUserByUsername_roleNull_noLanzaExcepcion_yCubreRama() {
        User u = new User();
        u.setPassword("pwd");
        u.setRole(null);

        Mockito.when(userRepository.findByUsername("pepe"))
                .thenReturn(Optional.of(u));

        UserDetails details = service.loadUserByUsername("pepe");

        assertEquals("pepe", details.getUsername());
        assertEquals("pwd", details.getPassword());
        assertNotNull(details.getAuthorities());
    }

    @Test
    void loadUserByUsername_usuarioNoEncontrado_lanzaExcepcion() {
        Mockito.when(userRepository.findByUsername("ghost"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("ghost"));

        assertTrue(ex.getMessage().contains("Usuario no encontrado"));
    }

    private static boolean tieneAuthority(Collection<? extends GrantedAuthority> auths, String role) {
        return auths.contains(new SimpleGrantedAuthority(role));
    }
}
