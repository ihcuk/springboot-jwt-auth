package com.apexon.auth.service;

import com.apexon.auth.dto.UpdateProfileRequest;
import com.apexon.auth.dto.UserResponse;
import com.apexon.auth.entity.User;
import com.apexon.auth.repository.UserRepository;
import com.apexon.auth.util.ModelMapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    @DisplayName("Get all users - success")
    void getAllUsers_success() {
        User user1 = User.builder()
                .id(1L)
                .name("Alice")
                .email("alice@example.com")
                .contactNumber("1234567890")
                .password("password")
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("Bob")
                .email("bob@example.com")
                .contactNumber("9876543210")
                .password("password")
                .build();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserResponse> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEmail()).isEqualTo("alice@example.com");
        assertThat(result.get(1).getName()).isEqualTo("Bob");
    }

    @Test
    @DisplayName("Update user profile - success")
    void updateUserProfile_success() {
        String currentEmail = "old@example.com";

        User existingUser = User.builder()
                .id(1L)
                .name("Old Name")
                .email("old@example.com")
                .contactNumber("9999999999")
                .password("password")
                .build();

        UpdateProfileRequest updateRequest = new UpdateProfileRequest();
        updateRequest.setName("New Name");
        updateRequest.setEmail("new@example.com");
        updateRequest.setContactNumber("8888888888");

        when(userRepository.findByEmail(currentEmail)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserResponse response = userService.updateUserProfile(currentEmail, updateRequest);

        assertThat(response.getName()).isEqualTo("New Name");
        assertThat(response.getEmail()).isEqualTo("new@example.com");
        assertThat(response.getContactNumber()).isEqualTo("8888888888");
    }

    @Test
    @DisplayName("Update user profile - user not found")
    void updateUserProfile_userNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        UpdateProfileRequest updateRequest = new UpdateProfileRequest();
        updateRequest.setEmail("updated@example.com");

        assertThatThrownBy(() -> userService.updateUserProfile("unknown@example.com", updateRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    @DisplayName("Get user by email - success")
    void getUserByEmail_success() {
        User user = User.builder()
                .id(1L)
                .name("Jane")
                .email("jane@example.com")
                .contactNumber("1112223333")
                .password("pass")
                .build();

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserResponseByEmail("jane@example.com");

        assertThat(response.getEmail()).isEqualTo("jane@example.com");
        assertThat(response.getName()).isEqualTo("Jane");
    }

    @Test
    @DisplayName("Get user by email - user not found")
    void getUserByEmail_userNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserResponseByEmail("notfound@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }
}
