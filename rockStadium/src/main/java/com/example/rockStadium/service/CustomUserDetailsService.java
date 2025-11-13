package com.example.rockStadium.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.rockStadium.model.User;
import com.example.rockStadium.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom UserDetailsService implementation for Spring Security
 * Loads user details from the database for authentication
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    /**
     * Carga los detalles del usuario desde la base de datos usando el email
     * 
     * @param email Email del usuario (usado como username)
     * @return UserDetails del usuario encontrado
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Attempting to load user by email: {}", email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new UsernameNotFoundException(
                        String.format("User not found with email: %s", email)
                    );
                });
        
        log.debug("User found: {} with roles: {}", email, user.getRoles());
        
        // User ya implementa UserDetails, por lo que podemos devolverlo directamente
        return user;
    }
}