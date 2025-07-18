package com.apexon.auth.controller;

import com.apexon.auth.dto.LoginRequest;
import com.apexon.auth.dto.RegisterRequest;
import com.apexon.auth.security.JwtFilter;
import com.apexon.auth.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;

@WebMvcTest(AuthController.class)
@Import(AuthControllerTest.TestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private AuthService authService;

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        public AuthService authService() {
            return Mockito.mock(AuthService.class);
        }

        @Bean
        @Primary
        public JwtFilter jwtFilter() {
            return Mockito.mock(JwtFilter.class);
        }
    }

    @BeforeEach
    void setup() {
        when(authService.register(any())).thenReturn("RegisteredUser");
        when(authService.login(any())).thenReturn("mock-jwt-token");
    }

    @Test
    @DisplayName("POST /api/auth/register - should register user successfully")
    void registerUser_success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Alice");
        request.setEmail("alice@example.com");
        request.setPassword("secure123");
        request.setContactNumber("9876543210"); // ✅ required field

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("RegisteredUser"))
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }


    @Test
    @DisplayName("POST /api/auth/login - should return token")
    void loginUser_success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("alice@example.com");
        request.setPassword("secure123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("mock-jwt-token"))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    @DisplayName("GET /api/auth/hello - should return hello message")
    void sayHello_success() throws Exception {
        mockMvc.perform(get("/api/auth/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Hello, authenticated user!"))
                .andExpect(jsonPath("$.message").value("Success"));
    }
}
