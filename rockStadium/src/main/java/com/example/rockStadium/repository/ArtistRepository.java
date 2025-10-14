package com.example.rockStadium.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rockStadium.model.Artist;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Integer> {
    
    Optional<Artist> findBySpotifyId(String spotifyId);
    
    Optional<Artist> findByName(String name);
    
    List<Artist> findByNameContainingIgnoreCase(String name);

    
    boolean existsBySpotifyId(String spotifyId);
}