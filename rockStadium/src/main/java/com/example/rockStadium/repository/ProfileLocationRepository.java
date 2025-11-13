package com.example.rockStadium.repository;

import com.example.rockStadium.model.ProfileLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileLocationRepository extends JpaRepository<ProfileLocation, Integer> {
}
