package com.apexon.auth.controller;

import com.apexon.auth.dto.LoginRequest;
import com.apexon.auth.dto.RegisterRequest;
import com.apexon.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String jwtToken = authService.login(request);
        return ResponseEntity.ok(jwtToken);
    }
}
