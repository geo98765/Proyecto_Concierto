package com.example.rockStadium.service;

import java.util.List;

import com.example.rockStadium.dto.ArtistResponse;
import com.example.rockStadium.dto.UserPreferenceRequest;
import com.example.rockStadium.dto.UserPreferenceResponse;

public interface UserPreferenceService {
    
    // Configuración de preferencias
    UserPreferenceResponse create(UserPreferenceRequest req);
    UserPreferenceResponse update(Integer userId, UserPreferenceRequest req);
    UserPreferenceResponse getPreferences(Integer userId);
    
    // Artistas favoritos
    UserPreferenceResponse addFavoriteArtist(Integer userId, String spotifyId);
    UserPreferenceResponse removeFavoriteArtist(Integer userId, Integer artistId);
    List<ArtistResponse> getFavoriteArtists(Integer userId);
    
    // Géneros favoritos
    UserPreferenceResponse addFavoriteGenre(Integer userId, Integer genreId);
    UserPreferenceResponse removeFavoriteGenre(Integer userId, Integer genreId);

}