package com.example.rockStadium.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rockStadium.dto.LoginRequest;
import com.example.rockStadium.dto.UpdatePasswordRequest;
import com.example.rockStadium.dto.UpdateProfileRequest;
import com.example.rockStadium.dto.UserRequest;
import com.example.rockStadium.dto.UserResponse;
import com.example.rockStadium.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "User management and authentication endpoints")
public class UserController {
    
    private final UserService userService;
    
    @Operation(
        summary = "Register new user",
        description = "Creates a new user account with profile and location.\n\n" +
                "**Validation rules:**\n" +
                "- Email must be unique in the system\n" +
                "- Password minimum 8 characters\n" +
                "- Password must contain at least one uppercase letter and one number\n" +
                "- Valid location (format: City, State, Country)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid data or email already registered"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "New user data",
                required = true,
                content = @Content(schema = @Schema(implementation = UserRequest.class))
            )
            @Valid @RequestBody UserRequest request) {
        log.info("📝 Registering new user: {}", request.getEmail());
        UserResponse response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Operation(
        summary = "Login",
        description = "Authenticates a user and creates an active session.\n\n" +
                "Returns user information and profile."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Login credentials",
                required = true
            )
            @Valid @RequestBody LoginRequest request) {
        log.info("🔐 Login attempt for: {}", request.getEmail());
        UserResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Logout",
        description = "Ends the user's active session and invalidates the authentication token."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Logout successful"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/{userId}/logout")
    public ResponseEntity<Void> logout(
            @Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Integer userId) {
        log.info("👋 Logout request for user ID: {}", userId);
        userService.logout(userId);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(
        summary = "Change password",
        description = "Updates the user's password.\n\n" +
                "Requires the old password for verification."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Password changed successfully"
        ),
        @ApiResponse(responseCode = "400", description = "Incorrect old password"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{userId}/password")
    public ResponseEntity<UserResponse> changePassword(
            @Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Integer userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Password update data"
            )
            @Valid @RequestBody UpdatePasswordRequest request) {
        log.info("🔑 Password change request for user ID: {}", userId);
        UserResponse response = userService.changePassword(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Update profile",
        description = "Updates user profile information.\n\n" +
                "All fields are optional. Only provided fields will be updated."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Profile updated successfully"
        ),
        @ApiResponse(responseCode = "400", description = "Email already registered or invalid data"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{userId}/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Integer userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Data to update (all optional)"
            )
            @Valid @RequestBody UpdateProfileRequest request) {
        log.info("✏️ Profile update request for user ID: {}", userId);
        UserResponse response = userService.updateProfile(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Get user by ID",
        description = "Returns complete user information including profile and location."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User found"
        ),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Integer userId) {
        log.info("👤 Fetching user by ID: {}", userId);
        UserResponse response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }
}