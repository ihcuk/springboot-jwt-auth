package com.apexon.auth.service;

import com.apexon.auth.dto.UpdateProfileRequest;
import com.apexon.auth.dto.UserResponse;
import com.apexon.auth.entity.User;
import com.apexon.auth.repository.UserRepository;
import com.apexon.auth.util.ModelMapperUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> ModelMapperUtil.map(user, UserResponse.class))
                .collect(Collectors.toList());
    }

    public UserResponse updateUserProfile(String currentEmail, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setContactNumber(request.getContactNumber());

        userRepository.save(user);

        return ModelMapperUtil.map(user, UserResponse.class);
    }

    public UserResponse getUserResponseByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ModelMapperUtil.map(user, UserResponse.class);
    }
}
