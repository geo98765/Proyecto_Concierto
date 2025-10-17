package com.example.rockStadium.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rockStadium.model.FavoriteArtist;

@Repository
public interface FavoriteArtistRepository extends JpaRepository<FavoriteArtist, Integer> {
    
    /**
     * Get all favorite artists for a profile
     */
    List<FavoriteArtist> findByProfileProfileId(Integer profileId);
    
    /**
     * Check if artist is already favorite
     */
    boolean existsByProfileProfileIdAndArtistArtistId(Integer profileId, Integer artistId);
    
    /**
     * Find specific favorite artist relationship
     */
    Optional<FavoriteArtist> findByProfileProfileIdAndArtistArtistId(Integer profileId, Integer artistId);
    
    /**
     * Count favorite artists for a profile
     */
    long countByProfileProfileId(Integer profileId);
    
    /**
     * Delete favorite artist relationship
     */
    void deleteByProfileProfileIdAndArtistArtistId(Integer profileId, Integer artistId);
}