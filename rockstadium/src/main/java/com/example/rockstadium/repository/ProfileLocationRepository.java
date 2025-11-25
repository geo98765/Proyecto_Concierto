package com.example.rockstadium.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rockstadium.model.ProfileLocation;

@Repository
public interface ProfileLocationRepository extends JpaRepository<ProfileLocation, Integer> {
}
