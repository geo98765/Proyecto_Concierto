package com.example.rockStadium.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para preferencias del usuario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceResponse {
    private Integer userPreferenceId;
    private Integer profileId;
    private BigDecimal searchRadius;
    private Boolean emailNotifications;
    
    // Listas de favoritos
    private List<ArtistResponse> favoriteArtists;
    private List<MusicGenreResponse> favoriteGenres;
    
    // Contadores
    private Integer favoriteArtistsCount;
    private Integer favoriteGenresCount;
    
    // Límites
    private Integer maxFavoriteArtists;
    private Integer maxFavoriteGenres;
}