package com.example.rockStadium.service;

import java.util.List;

import com.example.rockStadium.dto.ArtistResponse;
import com.example.rockStadium.dto.MusicGenreResponse;
import com.example.rockStadium.dto.UserPreferenceRequest;
import com.example.rockStadium.dto.UserPreferenceResponse;

public interface UserPreferenceService {
    
    // ===== PREFERENCIAS DE BÚSQUEDA =====
    UserPreferenceResponse createOrUpdatePreferences(Integer userId, UserPreferenceRequest request);
    UserPreferenceResponse getPreferences(Integer userId);
    
    // ===== ARTISTAS FAVORITOS =====
    UserPreferenceResponse addFavoriteArtist(Integer userId, String spotifyId);
    UserPreferenceResponse removeFavoriteArtist(Integer userId, Integer artistId);
    List<ArtistResponse> getFavoriteArtists(Integer userId);
    
    // ===== GÉNEROS FAVORITOS =====
    UserPreferenceResponse addFavoriteGenre(Integer userId, Integer genreId);
    UserPreferenceResponse removeFavoriteGenre(Integer userId, Integer genreId);
    List<MusicGenreResponse> getFavoriteGenres(Integer userId);
    List<MusicGenreResponse> getAllGenres();
}