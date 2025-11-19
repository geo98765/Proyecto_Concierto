package com.example.rockstadium.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.rockstadium.dto.LoginRequest;
import com.example.rockstadium.dto.UpdatePasswordRequest;
import com.example.rockstadium.dto.UpdateProfileRequest;
import com.example.rockstadium.dto.UserRequest;
import com.example.rockstadium.dto.UserResponse;
import com.example.rockstadium.mapper.UserMapper;
import com.example.rockstadium.model.Profile;
import com.example.rockstadium.model.ProfileLocation;
import com.example.rockstadium.model.User;
import com.example.rockstadium.repository.ProfileLocationRepository;
import com.example.rockstadium.repository.ProfileRepository;
import com.example.rockstadium.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ProfileLocationRepository profileLocationRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public UserResponse registerUser(UserRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        
        // Validar email único
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email already exists: {}", request.getEmail());
            throw new IllegalStateException("Email is already registered");
        }
        
        // Crear usuario con contraseña hasheada
        User user = userMapper.toEntity(request);
        
        // Hashear la contraseña con BCrypt
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // Asignar rol por defecto USER
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);
        
        // Establecer valores por defecto de seguridad
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        user = userRepository.save(user);
        
        // Guardar el ID ANTES de usarlo
        final Integer userId = user.getUserId();
        log.debug("User created with ID: {}", userId);
        
        // Crear perfil
        Profile profile = Profile.builder()
                .name(request.getName())
                .user(user)
                .build();
        profile = profileRepository.save(profile);
        log.debug("Profile created with ID: {}", profile.getProfileId());
        
        // Crear ubicación del perfil
        ProfileLocation location = parseAndCreateLocation(request.getLocation(), profile);
        profileLocationRepository.save(location);
        log.debug("Profile location created");
        
        // Recargar usuario con relaciones usando el ID guardado
        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("User not found with id: '%s'", userId)
                ));
        
        log.info("✅ User registered successfully: {}", savedUser.getEmail());
        return userMapper.toResponse(savedUser);
    }
    
    @Override
    public UserResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        
        // Buscar usuario por email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed: User not found with email: {}", request.getEmail());
                    return new EntityNotFoundException(
                            String.format("User not found with email: '%s'", request.getEmail())
                    );
                });
        
        // Verificar contraseña usando BCrypt
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed: Invalid password for email: {}", request.getEmail());
            throw new IllegalArgumentException("Invalid credentials");
        }
        
        log.info("✅ Login successful for email: {}", request.getEmail());
        return userMapper.toResponse(user);
    }
    
    @Override
    public void logout(Integer userId) {
        log.info("Logout request for user ID: {}", userId);
        
        // Verificar que el usuario exista
        if (!userRepository.existsById(userId)) {
            log.warn("Logout failed: User not found with ID: {}", userId);
            throw new EntityNotFoundException(
                    String.format("User not found with id: '%s'", userId)
            );
        }
        
        // Verificar que el usuario solo pueda cerrar su propia sesión
        validateUserOwnership(userId);
        
        log.info("✅ Logout successful for user ID: {}", userId);
    }
    
    @Override
    @Transactional
    public UserResponse changePassword(Integer userId, UpdatePasswordRequest request) {
        log.info("Password change request for user ID: {}", userId);
        
        // Verificar que el usuario solo pueda cambiar su propia contraseña
        validateUserOwnership(userId);
        
        // Buscar usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Password change failed: User not found with ID: {}", userId);
                    return new EntityNotFoundException(
                            String.format("User not found with id: '%s'", userId)
                    );
                });
        
        // Verificar contraseña antigua usando BCrypt
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            log.warn("Password change failed: Incorrect old password for user ID: {}", userId);
            throw new IllegalArgumentException("Old password is incorrect");
        }
        
        // Actualizar contraseña hasheada con BCrypt
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        user = userRepository.save(user);
        
        log.info("✅ Password changed successfully for user ID: {}", userId);
        return userMapper.toResponse(user);
    }
    
    @Override
    @Transactional
    public UserResponse updateProfile(Integer userId, UpdateProfileRequest request) {
        log.info("Profile update request for user ID: {}", userId);
        
        // Verificar que el usuario solo pueda actualizar su propio perfil
        validateUserOwnership(userId);
        
        // Buscar usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Profile update failed: User not found with ID: {}", userId);
                    return new EntityNotFoundException(
                            String.format("User not found with id: '%s'", userId)
                    );
                });
        
        // Actualizar email si se proporciona
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                log.warn("Profile update failed: Email already exists: {}", request.getEmail());
                throw new IllegalStateException("Email is already registered");
            }
            user.setEmail(request.getEmail());
            log.debug("Email updated for user ID: {}", userId);
        }
        
        // Obtener perfil del usuario
        if (user.getProfiles() == null || user.getProfiles().isEmpty()) {
            log.error("Profile update failed: User has no profile for ID: {}", userId);
            throw new IllegalStateException("User has no profile");
        }
        
        Profile profile = user.getProfiles().get(0);
        
        // Actualizar nombre si se proporciona
        if (request.getName() != null) {
            profile.setName(request.getName());
            log.debug("Name updated for user ID: {}", userId);
        }
        
        // Actualizar ubicación si se proporciona
        if (request.getLocation() != null) {
            ProfileLocation location = profile.getProfileLocation();
            if (location != null) {
                updateLocation(request.getLocation(), location);
                profileLocationRepository.save(location);
                log.debug("Location updated for user ID: {}", userId);
            }
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        profileRepository.save(profile);
        user = userRepository.save(user);
        
        log.info("✅ Profile updated successfully for user ID: {}", userId);
        return userMapper.toResponse(user);
    }
    
    @Override
    public UserResponse getUserById(Integer userId) {
        log.info("Fetching user by ID: {}", userId);
        
        // Verificar que el usuario solo pueda ver su propio perfil
        validateUserOwnership(userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new EntityNotFoundException(
                            String.format("User not found with id: '%s'", userId)
                    );
                });
        
        return userMapper.toResponse(user);
    }
    
    // ==================== MÉTODOS PRIVADOS ====================
    
    /**
     * Valida que el usuario autenticado sea el dueño del recurso o sea ADMIN
     * 
     * @param userId ID del usuario propietario del recurso
     * @throws IllegalStateException si el usuario no tiene permiso
     */
    private void validateUserOwnership(Integer userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }
        
        // Obtener el email del usuario autenticado
        String authenticatedEmail = authentication.getName();
        
        // Verificar si el usuario autenticado es ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        // Si es ADMIN, tiene permiso total
        if (isAdmin) {
            log.debug("Admin user accessing resource for user ID: {}", userId);
            return;
        }
        
        // Si no es ADMIN, verificar que sea el propietario
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("User not found with id: '%s'", userId)
                ));
        
        if (!targetUser.getEmail().equals(authenticatedEmail)) {
            log.warn("Unauthorized access attempt by user: {} for user ID: {}", 
                    authenticatedEmail, userId);
            throw new IllegalStateException("You don't have permission to access this resource");
        }
    }
    
    /**
     * Parsea y crea una ubicación de perfil desde un string
     * Formato esperado: "Ciudad, Estado, País"
     */
    private ProfileLocation parseAndCreateLocation(String locationString, Profile profile) {
        String[] parts = locationString.split(",");
        
        if (parts.length < 3) {
            throw new IllegalArgumentException(
                "Invalid location format. Expected: 'City, State, Country'"
            );
        }
        
        return ProfileLocation.builder()
                .municipality(parts[0].trim())
                .state(parts[1].trim())
                .country(parts[2].trim())
                .profile(profile)
                .build();
    }
    
    /**
     * Actualiza una ubicación existente desde un string
     */
    private void updateLocation(String locationString, ProfileLocation location) {
        String[] parts = locationString.split(",");
        
        if (parts.length < 3) {
            throw new IllegalArgumentException(
                "Invalid location format. Expected: 'City, State, Country'"
            );
        }
        
        location.setMunicipality(parts[0].trim());
        location.setState(parts[1].trim());
        location.setCountry(parts[2].trim());
    }
}