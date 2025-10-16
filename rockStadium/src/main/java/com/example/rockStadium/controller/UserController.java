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

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de usuarios y autenticación")
public class UserController {
    
    private final UserService userService;
    
    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Crea una nueva cuenta de usuario con perfil y ubicación.\n\n" +
                "**Reglas de validación:**\n" +
                "- Email único en el sistema\n" +
                "- Contraseña mínimo 8 caracteres\n" +
                "- Contraseña debe contener al menos una mayúscula y un número\n" +
                "- Ubicación válida (formato: Ciudad, Estado, País)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Usuario registrado exitosamente",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o email ya registrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos del nuevo usuario",
                required = true,
                content = @Content(schema = @Schema(implementation = UserRequest.class))
            )
            @Valid @RequestBody UserRequest request) {
        UserResponse response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica un usuario y crea una sesión activa.\n\n" +
                "Retorna la información del usuario y su perfil."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login exitoso",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Credenciales de acceso",
                required = true
            )
            @Valid @RequestBody LoginRequest request) {
        UserResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Cerrar sesión",
        description = "Finaliza la sesión activa del usuario e invalida el token de autenticación."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Sesión cerrada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/{userId}/logout")
    public ResponseEntity<Void> logout(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Integer userId) {
        userService.logout(userId);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(
        summary = "Cambiar contraseña",
        description = "Actualiza la contraseña del usuario desde una sesión activa.\n\n" +
                "**Reglas:**\n" +
                "- Contraseña anterior debe ser correcta\n" +
                "- Nueva contraseña mínimo 8 caracteres\n" +
                "- Debe contener al menos una mayúscula y un número"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Contraseña actualizada exitosamente"
        ),
        @ApiResponse(responseCode = "400", description = "Contraseña anterior incorrecta o nueva contraseña inválida"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{userId}/password")
    public ResponseEntity<UserResponse> changePassword(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Integer userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Contraseña anterior y nueva contraseña"
            )
            @Valid @RequestBody UpdatePasswordRequest request) {
        UserResponse response = userService.changePassword(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Actualizar perfil",
        description = "Modifica la información del perfil del usuario: nombre, email o ubicación.\n\n" +
                "**Nota:** Si se cambia el email, debe ser único en el sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Perfil actualizado exitosamente"
        ),
        @ApiResponse(responseCode = "400", description = "Email ya está registrado o datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{userId}/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Integer userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos a actualizar (todos opcionales)"
            )
            @Valid @RequestBody UpdateProfileRequest request) {
        UserResponse response = userService.updateProfile(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Obtener usuario por ID",
        description = "Retorna la información completa del usuario incluyendo su perfil y ubicación."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuario encontrado"
        ),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Integer userId) {
        UserResponse response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }
}