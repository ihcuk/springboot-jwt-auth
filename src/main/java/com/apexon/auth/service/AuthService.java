package com.apexon.auth.service;

import com.apexon.auth.dto.RegisterRequest;
import com.apexon.auth.dto.LoginRequest;
import com.apexon.auth.entity.User;
import com.apexon.auth.exception.InvalidCredentialsException;
import com.apexon.auth.exception.UserAlreadyExistsException;
import com.apexon.auth.repository.UserRepository;
import com.apexon.auth.security.JwtUtil;
import com.apexon.auth.util.ModelMapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists with email: " + request.getEmail());
        }

        User user = ModelMapperUtil.map(request, User.class);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return "User registered successfully";
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Email not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Incorrect password");
        }

        return jwtUtil.generateToken(user.getEmail());
    }
}
