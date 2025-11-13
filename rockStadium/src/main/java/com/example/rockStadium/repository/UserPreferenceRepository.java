package com.example.rockStadium.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rockStadium.model.UserPreference;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Integer> {
    
    /**
     * Find user preference by profile ID
     */
    Optional<UserPreference> findByProfileProfileId(Integer profileId);
    
    /**
     * Find user preference by user ID (through profile)
     */
    Optional<UserPreference> findByProfileUserUserId(Integer userId);
    
    /**
     * Check if user preference exists for a profile
     */
    boolean existsByProfileProfileId(Integer profileId);
}