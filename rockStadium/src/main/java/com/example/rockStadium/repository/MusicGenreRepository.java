package com.example.rockStadium.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rockStadium.model.MusicGenre;

@Repository
public interface MusicGenreRepository extends JpaRepository<MusicGenre, Integer> {
    
    // Buscar género por nombre
    Optional<MusicGenre> findByNameIgnoreCase(String name);
    
    // Buscar géneros que contengan un texto
    List<MusicGenre> findByNameContainingIgnoreCase(String name);
    
    // Verificar si existe un género por nombre
    boolean existsByNameIgnoreCase(String name);
}
