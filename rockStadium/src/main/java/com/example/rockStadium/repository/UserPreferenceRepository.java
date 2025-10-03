package com.example.rockStadium.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.rockStadium.model.UserPreference;


public interface  UserPreferenceRepository extends JpaRepository<UserPreference, Integer> {
    
}
