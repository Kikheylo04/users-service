package com.ventas.idat.users.controller;

import com.ventas.idat.users.dto.ApiResponse;
import com.ventas.idat.users.dto.LoginRequest;
import com.ventas.idat.users.dto.UserDTO;
import com.ventas.idat.users.model.User;
import com.ventas.idat.users.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    @Test
    void register_ok() {
        User u = new User();
        u.setUsername("john");

        Mockito.when(userService.register(any(User.class))).thenReturn(u);

        ResponseEntity<User> resp = controller.register(u);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("john", resp.getBody().getUsername());
    }

    @Test
    void login_ok() {
        LoginRequest req = new LoginRequest();
        req.setUsername("john");
        req.setPassword("123");

        ApiResponse<String> svc = new ApiResponse<>(HttpStatus.OK.value(), "Login Exitoso", "jwt");
        Mockito.when(userService.login(any(LoginRequest.class))).thenReturn(svc);

        ResponseEntity<ApiResponse<String>> resp = controller.login(req);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(200, resp.getBody().getResponseCode());
        assertEquals("jwt", resp.getBody().getData());
    }

    @Test
    void profile_enSesion_ok() {
        ApiResponse<UserDTO> svc = ApiResponse.<UserDTO>builder()
                .responseCode(200)
                .responseMessage("ok")
                .data(UserDTO.builder().username("john").build())
                .build();
        Mockito.when(userService.getUserDetail("john")).thenReturn(svc);

        var auth = new UsernamePasswordAuthenticationToken("john", "x");
        ResponseEntity<ApiResponse<UserDTO>> resp = controller.getUserDetailInSession(auth);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("john", resp.getBody().getData().getUsername());
    }

    @Test
    void profile_porUsername_ok() {
        ApiResponse<UserDTO> svc = ApiResponse.<UserDTO>builder()
                .responseCode(200)
                .responseMessage("ok")
                .data(UserDTO.builder().username("ana").build())
                .build();
        Mockito.when(userService.getUserDetail("ana")).thenReturn(svc);

        ResponseEntity<ApiResponse<UserDTO>> resp = controller.getUserDetail("ana");

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("ana", resp.getBody().getData().getUsername());
    }

    @Test
    void getAll_ok() {
        ApiResponse<List<UserDTO>> svc = ApiResponse.<List<UserDTO>>builder()
                .responseCode(200)
                .responseMessage("ok")
                .data(List.of(UserDTO.builder().username("u1").build()))
                .build();
        Mockito.when(userService.getAll()).thenReturn(svc);

        ResponseEntity<ApiResponse<List<UserDTO>>> resp = controller.getAllUsers();

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("u1", resp.getBody().getData().get(0).getUsername());
    }
}
