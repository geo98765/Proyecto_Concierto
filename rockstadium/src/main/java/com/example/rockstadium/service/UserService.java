package com.example.rockstadium.service;

import com.example.rockstadium.dto.LoginRequest;
import com.example.rockstadium.dto.UpdatePasswordRequest;
import com.example.rockstadium.dto.UpdateProfileRequest;
import com.example.rockstadium.dto.UserRequest;
import com.example.rockstadium.dto.UserResponse;

public interface UserService {
    UserResponse registerUser(UserRequest request);
    UserResponse login(LoginRequest request);
    void logout(Integer userId);
    UserResponse changePassword(Integer userId, UpdatePasswordRequest request);
    UserResponse updateProfile(Integer userId, UpdateProfileRequest request);
    UserResponse getUserById(Integer userId);
}
