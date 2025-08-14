package com.ventas.idat.users.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.*;

class CustomAuthenticationEntryPointTest {

    private final CustomAuthenticationEntryPoint entryPoint = new CustomAuthenticationEntryPoint();
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void commence_escribe401_yJsonDeApiResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException authEx = new AuthenticationException("Token inválido") {
        };

        entryPoint.commence(request, response, authEx);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertEquals("application/json", response.getContentType());

        String body = response.getContentAsString();
        JsonNode json = mapper.readTree(body);

        assertEquals(401, json.get("responseCode").asInt());
        assertTrue(json.get("responseMessage").asText().contains("Unauthorized"));
        assertTrue(json.get("responseMessage").asText().contains("Token inválido"));
        assertTrue(json.get("data").isNull());
    }
}
