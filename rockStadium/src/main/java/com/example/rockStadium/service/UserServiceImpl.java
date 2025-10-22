package com.example.rockStadium.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.rockStadium.dto.LoginRequest;
import com.example.rockStadium.dto.UpdatePasswordRequest;
import com.example.rockStadium.dto.UpdateProfileRequest;
import com.example.rockStadium.dto.UserRequest;
import com.example.rockStadium.dto.UserResponse;
import com.example.rockStadium.mapper.UserMapper;
import com.example.rockStadium.model.Profile;
import com.example.rockStadium.model.ProfileLocation;
import com.example.rockStadium.model.User;
import com.example.rockStadium.repository.ProfileLocationRepository;
import com.example.rockStadium.repository.ProfileRepository;
import com.example.rockStadium.repository.UserRepository;

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
    
    @Override
    @Transactional
    public UserResponse registerUser(UserRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        
        // Validar email único
    if (userRepository.existsByEmail(request.getEmail())) {
        log.warn("Registration failed: Email already exists: {}", request.getEmail());
        throw new IllegalStateException("Email is already registered");
    }
    
    // Crear usuario
    // TODO: IMPORTANTE - Implementar hash de contraseña con BCrypt antes de guardar
    User user = userMapper.toEntity(request);
    user = userRepository.save(user);
    
    // Guardar el ID ANTES de usarlo en el lambda
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
        
        // Verificar contraseña
        // TODO: IMPORTANTE - Usar BCrypt para comparar contraseñas
        // Ejemplo: if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
        if (!user.getPassword().equals(request.getPassword())) {
            log.warn("Login failed: Invalid password for email: {}", request.getEmail());
            throw new IllegalArgumentException("Invalid credentials");
        }
        
        log.info("✅ Login successful for user: {}", user.getEmail());
        return userMapper.toResponse(user);
    }
    
    @Override
    public void logout(Integer userId) {
        log.info("Logout request for user ID: {}", userId);
        
        // Verificar que el usuario existe
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Logout failed: User not found with ID: {}", userId);
                    return new EntityNotFoundException(
                            String.format("User not found with id: '%s'", userId)
                    );
                });
        
        // TODO: Implementar lógica de invalidación de token si usas JWT
        // Ejemplo: tokenBlacklistService.invalidateToken(userId);
        
        log.info("✅ Logout successful for user ID: {}", userId);
    }
    
    @Override
    @Transactional
    public UserResponse changePassword(Integer userId, UpdatePasswordRequest request) {
        log.info("Password change request for user ID: {}", userId);
        
        // Buscar usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Password change failed: User not found with ID: {}", userId);
                    return new EntityNotFoundException(
                            String.format("User not found with id: '%s'", userId)
                    );
                });
        
        // Verificar contraseña anterior
        // TODO: IMPORTANTE - Usar BCrypt para comparar contraseñas
        // Ejemplo: if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()))
        if (!user.getPassword().equals(request.getOldPassword())) {
            log.warn("Password change failed: Incorrect old password for user ID: {}", userId);
            throw new IllegalArgumentException("Old password is incorrect");
        }
        
        // Actualizar contraseña
        // TODO: IMPORTANTE - Hash de contraseña con BCrypt
        // Ejemplo: user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPassword(request.getNewPassword());
        user = userRepository.save(user);
        
        log.info("✅ Password changed successfully for user ID: {}", userId);
        return userMapper.toResponse(user);
    }
    
    @Override
    @Transactional
    public UserResponse updateProfile(Integer userId, UpdateProfileRequest request) {
        log.info("Profile update request for user ID: {}", userId);
        
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
            updateOrCreateLocation(request.getLocation(), profile);
            log.debug("Location updated for user ID: {}", userId);
        }
        
        userRepository.save(user);
        
        log.info("✅ Profile updated successfully for user ID: {}", userId);
        return userMapper.toResponse(user);
    }
    
    @Override
    public UserResponse getUserById(Integer userId) {
        log.info("Fetching user by ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new EntityNotFoundException(
                            String.format("User not found with id: '%s'", userId)
                    );
                });
        
        return userMapper.toResponse(user);
    }
    
    // ===== HELPER METHODS =====
    
    /**
     * Parse location string and create ProfileLocation entity
     * Parsea el string de ubicación y crea la entidad ProfileLocation
     * 
     * Format expected: "Municipality, State, Country"
     * Example: "Ciudad de México, CDMX, México"
     * 
     * @param locationStr Location string in format "Municipality, State, Country"
     * @param profile Profile to associate with this location
     * @return ProfileLocation entity
     */
    private ProfileLocation parseAndCreateLocation(String locationStr, Profile profile) {
        String[] locationParts = locationStr.split(",");
        
        return ProfileLocation.builder()
                .municipality(locationParts.length > 0 ? locationParts[0].trim() : "")
                .state(locationParts.length > 1 ? locationParts[1].trim() : "")
                .country(locationParts.length > 2 ? locationParts[2].trim() : "")
                .profile(profile)
                .build();
    }
    
    /**
     * Update or create location for a profile
     * Actualiza o crea la ubicación para un perfil
     * 
     * @param locationStr Location string in format "Municipality, State, Country"
     * @param profile Profile to update location for
     */
    private void updateOrCreateLocation(String locationStr, Profile profile) {
        String[] locationParts = locationStr.split(",");
        
        ProfileLocation location = profile.getProfileLocation();
        
        if (location == null) {
            // Crear nueva ubicación
            location = new ProfileLocation();
            location.setProfile(profile);
        }
        
        // Actualizar campos
        location.setMunicipality(locationParts.length > 0 ? locationParts[0].trim() : "");
        location.setState(locationParts.length > 1 ? locationParts[1].trim() : "");
        location.setCountry(locationParts.length > 2 ? locationParts[2].trim() : "");
        
        profileLocationRepository.save(location);
    }
}