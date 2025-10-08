package com.example.rockStadium.mapper;

import com.example.rockStadium.dto.*;
import com.example.rockStadium.model.Profile;
import com.example.rockStadium.model.ProfileLocation;
import com.example.rockStadium.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    public User toEntity(UserRequest request) {
        return User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .userType("USER")
                .build();
    }
    
    public UserResponse toResponse(User user) {
        Profile profile = user.getProfiles() != null && !user.getProfiles().isEmpty() 
                ? user.getProfiles().get(0) 
                : null;
        
        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .userType(user.getUserType())
                .profile(profile != null ? toProfileResponse(profile) : null)
                .build();
    }
    
    public ProfileResponse toProfileResponse(Profile profile) {
        return ProfileResponse.builder()
                .profileId(profile.getProfileId())
                .name(profile.getName())
                .location(profile.getProfileLocation() != null 
                        ? toProfileLocationResponse(profile.getProfileLocation()) 
                        : null)
                .build();
    }
    
    public ProfileLocationResponse toProfileLocationResponse(ProfileLocation location) {
        return ProfileLocationResponse.builder()
                .profileLocationId(location.getProfileLocationId())
                .municipality(location.getMunicipality())
                .state(location.getState())
                .country(location.getCountry())
                .build();
    }
}
