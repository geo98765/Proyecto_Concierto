package com.example.rockStadium.controller;


import com.example.rockStadium.dto.*;
import com.example.rockStadium.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        UserResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{userId}/logout")
    public ResponseEntity<Void> logout(@PathVariable Integer userId) {
        userService.logout(userId);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{userId}/password")
    public ResponseEntity<UserResponse> changePassword(
            @PathVariable Integer userId,
            @Valid @RequestBody UpdatePasswordRequest request) {
        UserResponse response = userService.changePassword(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{userId}/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @PathVariable Integer userId,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserResponse response = userService.updateProfile(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer userId) {
        UserResponse response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }
}
