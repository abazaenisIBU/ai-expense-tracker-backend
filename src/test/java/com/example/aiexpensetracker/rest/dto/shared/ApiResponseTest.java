package com.example.aiexpensetracker.rest.dto.shared;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void testNoArgConstructor_and_settersGetters() {
        ApiResponse<String> resp = new ApiResponse<>();

        assertNull(resp.getTime());
        assertEquals(0, resp.getStatusCode());
        assertNull(resp.getMessage());
        assertNull(resp.getData());

        LocalDateTime now = LocalDateTime.of(2025, 6, 15, 12, 30);
        resp.setTime(now);
        resp.setStatusCode(201);
        resp.setMessage("Created");
        resp.setData("payload");

        assertEquals(now, resp.getTime());
        assertEquals(201, resp.getStatusCode());
        assertEquals("Created", resp.getMessage());
        assertEquals("payload", resp.getData());
    }

    @Test
    void testAllArgsConstructor_and_getters() {
        LocalDateTime ts = LocalDateTime.of(2025, 1, 1, 0, 0);
        int code = 404;
        String msg = "Not Found";
        Integer payload = 123;

        ApiResponse<Integer> resp = new ApiResponse<>(ts, code, msg, payload);

        assertEquals(ts, resp.getTime());
        assertEquals(code, resp.getStatusCode());
        assertEquals(msg, resp.getMessage());
        assertEquals(payload, resp.getData());
    }
}