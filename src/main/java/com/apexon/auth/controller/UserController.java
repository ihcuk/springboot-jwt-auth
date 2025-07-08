package com.apexon.auth.controller;

import com.apexon.auth.dto.ApiResponse;
import com.apexon.auth.dto.UserResponse;
import com.apexon.auth.security.JwtUtil;
import com.apexon.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User API", description = "Operations related to user profiles")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Operation(summary = "Get all user profiles (secured)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of user profiles"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users, "Fetched user profiles successfully"));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get the current logged-in user's profile")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<UserResponse>> getUserProfile(
            @RequestHeader("Authorization") String authHeader) {

        log.info("Fetching user profile from token");

        // Extract token from "Bearer <token>"
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        String email = jwtUtil.extractUsername(token);

        UserResponse user = userService.getUserResponseByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(user, "User profile fetched successfully"));
    }
}
