package com.example.rockStadium.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rockStadium.model.MusicGenre;

@Repository
public interface MusicGenreRepository extends JpaRepository<MusicGenre, Integer> {
    
    /**
     * Find genre by name (case-insensitive)
     */
    Optional<MusicGenre> findByNameIgnoreCase(String name);
    
    /**
     * Check if genre exists by name (case-insensitive)
     */
    boolean existsByNameIgnoreCase(String name);
}