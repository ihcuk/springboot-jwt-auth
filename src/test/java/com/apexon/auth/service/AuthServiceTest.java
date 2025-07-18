package com.apexon.auth.service;

import com.apexon.auth.dto.LoginRequest;
import com.apexon.auth.dto.RegisterRequest;
import com.apexon.auth.entity.User;
import com.apexon.auth.exception.InvalidCredentialsException;
import com.apexon.auth.exception.UserAlreadyExistsException;
import com.apexon.auth.repository.UserRepository;
import com.apexon.auth.security.JwtUtil;
import com.apexon.auth.util.ModelMapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(BCryptPasswordEncoder.class);
        jwtUtil = mock(JwtUtil.class);
        authService = new AuthService(userRepository, passwordEncoder, jwtUtil);
    }

    @Test
    @DisplayName("Register - success")
    void register_success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setName("Test User");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        String response = authService.register(request);

        verify(userRepository, times(1)).save(any(User.class));
        assertThat(response).isEqualTo("User registered successfully");
    }

    @Test
    @DisplayName("Register - user already exists")
    void register_userAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");

        when(userRepository.findByEmail("existing@example.com"))
                .thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("User already exists with email");
    }

    @Test
    @DisplayName("Login - success")
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("test@example.com")).thenReturn("mock-jwt-token");

        String token = authService.login(request);

        assertThat(token).isEqualTo("mock-jwt-token");
    }

    @Test
    @DisplayName("Login - email not found")
    void login_emailNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("notfound@example.com");

        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Email not found");
    }

    @Test
    @DisplayName("Login - password mismatch")
    void login_passwordMismatch() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongPassword");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Incorrect password");
    }
}
