package com.example.rockstadium.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rockstadium.model.FavoriteArtist;

@Repository
public interface FavoriteArtistRepository extends JpaRepository<FavoriteArtist, Integer> {
    
    /**
     * Get all favorite artists for a profile
     * Obtener todos los artistas favoritos de un perfil
     */
    List<FavoriteArtist> findByProfileProfileId(Integer profileId);
    
    /**
     * Get all favorite artists for a profile with pagination
     * Obtener todos los artistas favoritos de un perfil con paginación
     */
    Page<FavoriteArtist> findByProfileProfileId(Integer profileId, Pageable pageable);
    
    /**
     * Check if artist is already favorite
     * Verificar si el artista ya es favorito
     */
    boolean existsByProfileProfileIdAndArtistArtistId(Integer profileId, Integer artistId);
    
    /**
     * Find specific favorite artist relationship
     * Encontrar relación específica de artista favorito
     */
    Optional<FavoriteArtist> findByProfileProfileIdAndArtistArtistId(Integer profileId, Integer artistId);
    
    /**
     * Count favorite artists for a profile
     * Contar artistas favoritos de un perfil
     */
    long countByProfileProfileId(Integer profileId);
    
    /**
     * Delete favorite artist relationship
     * Eliminar relación de artista favorito
     */
    void deleteByProfileProfileIdAndArtistArtistId(Integer profileId, Integer artistId);
}