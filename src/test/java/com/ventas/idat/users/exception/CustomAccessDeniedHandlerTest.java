package com.ventas.idat.users.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.*;

class CustomAccessDeniedHandlerTest {

    private final CustomAccessDeniedHandler handler = new CustomAccessDeniedHandler();
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void handle_escribe403_yJsonDeApiResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AccessDeniedException ex = new AccessDeniedException("no permissions");

        handler.handle(request, response, ex);

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
        assertEquals("application/json", response.getContentType());

        String body = response.getContentAsString();
        JsonNode json = mapper.readTree(body);

        assertEquals(403, json.get("responseCode").asInt());
        assertTrue(json.get("responseMessage").asText().contains("Access Denied"));
        assertTrue(json.get("responseMessage").asText().contains("no permissions"));
        assertTrue(json.get("data").isNull());
    }
}
