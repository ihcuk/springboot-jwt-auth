package com.apexon.auth.controller;

import com.apexon.auth.dto.UpdateProfileRequest;
import com.apexon.auth.dto.UserResponse;
import com.apexon.auth.security.JwtUtil;
import com.apexon.auth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.beans.factory.annotation.Autowired;

@WebMvcTest(UserController.class)
@Import(UserControllerTest.MockServiceConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserService userService;

    private final String token = "Bearer mocked-jwt-token";

    @TestConfiguration
    static class MockServiceConfig {
        @Bean @Primary
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }

        @Bean @Primary
        public JwtUtil jwtUtil() {
            return Mockito.mock(JwtUtil.class);
        }
    }

    @BeforeEach
    void setUp() {
        when(jwtUtil.extractUsername("mocked-jwt-token")).thenReturn("user@example.com");
    }

    @Test
    void getAllUsers_shouldReturnList() throws Exception {
        UserResponse user1 = UserResponse.builder().name("Alice").email("alice@example.com").build();
        UserResponse user2 = UserResponse.builder().name("Bob").email("bob@example.com").build();

        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].email").value("alice@example.com"))
                .andExpect(jsonPath("$.data[1].name").value("Bob"));
    }

    @Test
    void getUserProfile_shouldReturnUser() throws Exception {
        UserResponse user = UserResponse.builder()
                .email("user@example.com")
                .name("John Doe")
                .contactNumber("9999999999")
                .build();

        when(userService.getUserResponseByEmail("user@example.com")).thenReturn(user);

        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("user@example.com"))
                .andExpect(jsonPath("$.message").value("User profile fetched successfully"));
    }

    @Test
    void updateUserProfile_shouldUpdateUser() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setEmail("updated@example.com");
        request.setName("Updated Name");
        request.setContactNumber("8888888888");

        UserResponse updated = UserResponse.builder()
                .email("updated@example.com")
                .name("Updated Name")
                .contactNumber("8888888888")
                .build();

        when(userService.updateUserProfile(eq("user@example.com"), any(UpdateProfileRequest.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/users/profile")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("updated@example.com"))
                .andExpect(jsonPath("$.data.name").value("Updated Name"))
                .andExpect(jsonPath("$.message").value("Profile updated successfully"));
    }
}
