package com.example.rockStadium.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rockStadium.model.FavoriteGenre;

@Repository
public interface FavoriteGenreRepository extends JpaRepository<FavoriteGenre, Integer> {
    
    // Obtener todos los géneros favoritos de un perfil
    List<FavoriteGenre> findByProfileProfileId(Integer profileId);
    
    // Verificar si un género ya es favorito
    boolean existsByProfileProfileIdAndMusicGenreMusicGenreId(Integer profileId, Integer musicGenreId);
    
    // Encontrar relación específica
    Optional<FavoriteGenre> findByProfileProfileIdAndMusicGenreMusicGenreId(Integer profileId, Integer musicGenreId);
    
    // Contar géneros favoritos de un perfil
    long countByProfileProfileId(Integer profileId);
    
    // Eliminar por perfil y género
    void deleteByProfileProfileIdAndMusicGenreMusicGenreId(Integer profileId, Integer musicGenreId);
}