package com.example.rockStadium.service;

import com.example.rockStadium.dto.*;
import com.example.rockStadium.mapper.UserMapper;
import com.example.rockStadium.model.Profile;
import com.example.rockStadium.model.ProfileLocation;
import com.example.rockStadium.model.User;
import com.example.rockStadium.repository.ProfileLocationRepository;
import com.example.rockStadium.repository.ProfileRepository;
import com.example.rockStadium.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ProfileLocationRepository profileLocationRepository;
    private final UserMapper userMapper;
    
    @Override
    @Transactional
    public UserResponse registerUser(UserRequest request) {
        // Validar email único
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        // Crear usuario
        User user = userMapper.toEntity(request);
        user = userRepository.save(user);
        
        // Crear perfil
        Profile profile = Profile.builder()
                .name(request.getName())
                .user(user)
                .build();
        profile = profileRepository.save(profile);
        
        // Crear ubicación del perfil
        String[] locationParts = request.getLocation().split(",");
        ProfileLocation location = ProfileLocation.builder()
                .municipality(locationParts.length > 0 ? locationParts[0].trim() : "")
                .state(locationParts.length > 1 ? locationParts[1].trim() : "")
                .country(locationParts.length > 2 ? locationParts[2].trim() : "")
                .profile(profile)
                .build();
        profileLocationRepository.save(location);
        
        // Recargar usuario con relaciones
        user = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return userMapper.toResponse(user);
    }
    
    @Override
    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
        
        // Aquí deberías usar BCrypt para comparar contraseñas
        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        
        return userMapper.toResponse(user);
    }
    
    @Override
    public void logout(Integer userId) {
        // Implementar lógica de invalidación de token si usas JWT
        // Por ahora solo verificamos que el usuario existe
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    
    @Override
    @Transactional
    public UserResponse changePassword(Integer userId, UpdatePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Verificar contraseña anterior
        if (!user.getPassword().equals(request.getOldPassword())) {
            throw new RuntimeException("La contraseña anterior es incorrecta");
        }
        
        // Actualizar contraseña
        user.setPassword(request.getNewPassword());
        user = userRepository.save(user);
        
        return userMapper.toResponse(user);
    }
    
    @Override
    @Transactional
    public UserResponse updateProfile(Integer userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Actualizar email si se proporciona
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("El email ya está registrado");
            }
            user.setEmail(request.getEmail());
        }
        
        // Actualizar perfil
        Profile profile = user.getProfiles().get(0);
        if (request.getName() != null) {
            profile.setName(request.getName());
        }
        
        // Actualizar ubicación si se proporciona
        if (request.getLocation() != null) {
            String[] locationParts = request.getLocation().split(",");
            ProfileLocation location = profile.getProfileLocation();
            if (location == null) {
                location = new ProfileLocation();
                location.setProfile(profile);
            }
            location.setMunicipality(locationParts.length > 0 ? locationParts[0].trim() : "");
            location.setState(locationParts.length > 1 ? locationParts[1].trim() : "");
            location.setCountry(locationParts.length > 2 ? locationParts[2].trim() : "");
            profileLocationRepository.save(location);
        }
        
        userRepository.save(user);
        
        return userMapper.toResponse(user);
    }
    
    @Override
    public UserResponse getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return userMapper.toResponse(user);
    }
}