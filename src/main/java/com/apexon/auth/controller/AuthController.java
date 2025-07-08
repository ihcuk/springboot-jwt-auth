package com.apexon.auth.controller;

import com.apexon.auth.dto.ApiResponse;
import com.apexon.auth.dto.LoginRequest;
import com.apexon.auth.dto.RegisterRequest;
import com.apexon.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth API", description = "Handles user authentication")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User registered"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PostMapping("/register")
    public ResponseEntity<com.apexon.auth.dto.ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        String result = authService.register(request);
        return ResponseEntity.ok(com.apexon.auth.dto.ApiResponse.success(result, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<com.apexon.auth.dto.ApiResponse<String>> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(com.apexon.auth.dto.ApiResponse.success(token, "Login successful"));
    }

    @GetMapping("/hello")
    public ResponseEntity<ApiResponse<String>> sayHello() {
        return ResponseEntity.ok(ApiResponse.success("Hello, authenticated user!", "Success"));
    }
}
