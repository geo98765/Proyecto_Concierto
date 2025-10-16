package com.example.rockStadium.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rockStadium.model.FavoriteArtist;

@Repository
public interface FavoriteArtistRepository extends JpaRepository<FavoriteArtist, Integer> {
    
    // Obtener todos los artistas favoritos de un perfil
    List<FavoriteArtist> findByProfileProfileId(Integer profileId);
    
    // Verificar si un artista ya es favorito
    boolean existsByProfileProfileIdAndArtistArtistId(Integer profileId, Integer artistId);
    
    // Encontrar relación específica
    Optional<FavoriteArtist> findByProfileProfileIdAndArtistArtistId(Integer profileId, Integer artistId);
    
    // Contar artistas favoritos de un perfil
    long countByProfileProfileId(Integer profileId);
    
    // Eliminar por perfil y artista
    void deleteByProfileProfileIdAndArtistArtistId(Integer profileId, Integer artistId);
}