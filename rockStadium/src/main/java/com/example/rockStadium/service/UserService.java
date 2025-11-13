package com.example.rockStadium.service;

import com.example.rockStadium.dto.LoginRequest;
import com.example.rockStadium.dto.UpdatePasswordRequest;
import com.example.rockStadium.dto.UpdateProfileRequest;
import com.example.rockStadium.dto.UserRequest;
import com.example.rockStadium.dto.UserResponse;

public interface UserService {
    UserResponse registerUser(UserRequest request);
    UserResponse login(LoginRequest request);
    void logout(Integer userId);
    UserResponse changePassword(Integer userId, UpdatePasswordRequest request);
    UserResponse updateProfile(Integer userId, UpdateProfileRequest request);
    UserResponse getUserById(Integer userId);
}
