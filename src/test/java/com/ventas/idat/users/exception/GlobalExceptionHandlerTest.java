package com.ventas.idat.users.exception;

import com.ventas.idat.users.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleUsernameNotFound_devuelve404_yMensaje() {
        UsernameNotFoundException ex = new UsernameNotFoundException("john not found");

        ResponseEntity<ApiResponse<Object>> resp = handler.handleUsernameNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        assertEquals(404, resp.getBody().getResponseCode());
        assertTrue(resp.getBody().getResponseMessage().contains("User not found"));
        assertNull(resp.getBody().getData());
    }

    @Test
    void handleAccessDenied_devuelve403_yMensaje() {
        AccessDeniedException ex = new AccessDeniedException("no roles");

        ResponseEntity<ApiResponse<Object>> resp = handler.handleAccessDenied(ex);

        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
        assertEquals(403, resp.getBody().getResponseCode());
        assertTrue(resp.getBody().getResponseMessage().contains("Access denied"));
        assertNull(resp.getBody().getData());
    }

    @Test
    void handleGeneral_devuelve500_yMensaje() {
        Exception ex = new Exception("algo falló");

        ResponseEntity<ApiResponse<Object>> resp = handler.handleGeneral(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertEquals(500, resp.getBody().getResponseCode());
        assertTrue(resp.getBody().getResponseMessage().contains("Internal server error"));
        assertNull(resp.getBody().getData());
    }
}
