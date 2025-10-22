package com.example.rockStadium.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rockStadium.model.FavoriteGenre;

@Repository
public interface FavoriteGenreRepository extends JpaRepository<FavoriteGenre, Integer> {
    
    /**
     * Get all favorite genres for a profile
     * Obtener todos los géneros favoritos de un perfil
     */
    List<FavoriteGenre> findByProfileProfileId(Integer profileId);
    
    /**
     * Get all favorite genres for a profile with pagination
     * Obtener todos los géneros favoritos de un perfil con paginación
     */
    Page<FavoriteGenre> findByProfileProfileId(Integer profileId, Pageable pageable);
    
    /**
     * Check if genre is already favorite
     * Verificar si un género ya es favorito
     */
    boolean existsByProfileProfileIdAndMusicGenreMusicGenreId(Integer profileId, Integer musicGenreId);
    
    /**
     * Find specific favorite genre relationship
     * Encontrar relación específica de género favorito
     */
    Optional<FavoriteGenre> findByProfileProfileIdAndMusicGenreMusicGenreId(Integer profileId, Integer musicGenreId);
    
    /**
     * Count favorite genres for a profile
     * Contar géneros favoritos de un perfil
     */
    long countByProfileProfileId(Integer profileId);
    
    /**
     * Delete favorite genre relationship
     * Eliminar relación de género favorito
     */
    void deleteByProfileProfileIdAndMusicGenreMusicGenreId(Integer profileId, Integer musicGenreId);
}