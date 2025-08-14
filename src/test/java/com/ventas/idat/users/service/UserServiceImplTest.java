package com.ventas.idat.users.service;

import com.ventas.idat.users.config.JWTUtil;
import com.ventas.idat.users.dto.ApiResponse;
import com.ventas.idat.users.dto.LoginRequest;
import com.ventas.idat.users.dto.UserDTO;
import com.ventas.idat.users.model.User;
import com.ventas.idat.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JWTUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void findByUsername_devuelveUsuario() {
        User u = new User();
        u.setUsername("john");
        Mockito.when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(u));

        Optional<User> found = userService.findByUsername("john");

        assertTrue(found.isPresent());
        assertEquals("john", found.get().getUsername());
    }

    @Test
    void register_encodeaPassword_yGuarda() {
        User input = new User();
        input.setUsername("john");
        input.setPassword("123");

        Mockito.when(passwordEncoder.encode("123")).thenReturn("ENC_123");
        Mockito.when(userRepository.save(any(User.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        User saved = userService.register(input);

        assertEquals("john", saved.getUsername());
        assertEquals("ENC_123", saved.getPassword());
        Mockito.verify(passwordEncoder).encode("123");
        Mockito.verify(userRepository).save(any(User.class));
    }

    @Test
    void getAll_mapeaAUserDTO_yDevuelve200() {
        User u1 = new User();
        u1.setId(1L);
        u1.setFirstName("John");
        u1.setLastName("Doe");
        u1.setUsername("jdoe");
        u1.setRole("ADMIN");
        User u2 = new User();
        u2.setId(2L);
        u2.setFirstName("Ana");
        u2.setLastName("Roe");
        u2.setUsername("aroe");
        u2.setRole("USER");
        Mockito.when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        ApiResponse<List<UserDTO>> resp = userService.getAll();

        assertEquals(HttpStatus.OK.value(), resp.getResponseCode());
        assertEquals(2, resp.getData().size());
        assertEquals("jdoe", resp.getData().get(0).getUsername());
        assertEquals("aroe", resp.getData().get(1).getUsername());
    }

    @Test
    void login_exitoso_devuelve200_yToken() {
        LoginRequest req = new LoginRequest();
        req.setUsername("john");
        req.setPassword("123");

        Authentication authMock = Mockito.mock(Authentication.class);
        UserDetails userDetails = Mockito.mock(UserDetails.class);

        Mockito.when(authMock.getPrincipal()).thenReturn(userDetails);
        Mockito.when(userDetails.getUsername()).thenReturn("john");
        Mockito.when(userDetails.getAuthorities()).thenReturn(List.of());

        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authMock);
        Mockito.when(jwtUtil.generateToken(anyString(), any(Collection.class)))
                .thenReturn("jwt-token");

        ApiResponse<String> resp = userService.login(req);

        assertEquals(HttpStatus.OK.value(), resp.getResponseCode());
        assertEquals("jwt-token", resp.getData());
    }

    @Test
    void login_credencialesInvalidas_devuelve401() {
        LoginRequest req = new LoginRequest();
        req.setUsername("john");
        req.setPassword("bad");

        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Bad credentials") {
                });

        ApiResponse<String> resp = userService.login(req);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), resp.getResponseCode());
        assertEquals("Credenciales Invalidas", resp.getResponseMessage());
        assertNull(resp.getData());
    }

    @Test
    void getUserDetail_encontrado_devuelve200_yDTO() {
        User u = new User();
        u.setId(10L);
        u.setFirstName("John");
        u.setLastName("Doe");
        u.setUsername("jdoe");
        u.setRole("ADMIN");

        Mockito.when(userRepository.findByUsername("jdoe")).thenReturn(Optional.of(u));

        ApiResponse<UserDTO> resp = userService.getUserDetail("jdoe");

        assertEquals(HttpStatus.OK.value(), resp.getResponseCode());
        assertEquals("jdoe", resp.getData().getUsername());
        assertEquals("John", resp.getData().getFirstName());
    }

    @Test
    void getUserDetail_noEncontrado_devuelve404() {
        Mockito.when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        ApiResponse<UserDTO> resp = userService.getUserDetail("ghost");

        assertEquals(HttpStatus.NOT_FOUND.value(), resp.getResponseCode());
        assertNull(resp.getData());
    }
}
