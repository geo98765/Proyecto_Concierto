package com.example.rockstadium.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.rockstadium.dto.AddFavoriteGenreRequest;
import com.example.rockstadium.dto.ArtistResponse;
import com.example.rockstadium.dto.DeleteFavoriteGenreRequest;
import com.example.rockstadium.dto.MusicGenreResponse;
import com.example.rockstadium.dto.SuccessResponse;
import com.example.rockstadium.dto.UserPreferenceBasicResponse;
import com.example.rockstadium.dto.UserPreferenceRequest;
import com.example.rockstadium.dto.UserPreferenceResponse;

/**
 * Service interface for user preferences management
 */
public interface UserPreferenceService {
    
    // ===== SEARCH PREFERENCES =====
    
    /**
     * Create or update user preferences
     */
UserPreferenceBasicResponse createOrUpdatePreferences(Integer userId, UserPreferenceRequest request);

// ========================================
    /**
     * Get user preferences (with optional pagination for lists)
     */
    UserPreferenceResponse getPreferences(Integer userId, boolean includeFullLists);
    
    // ===== FAVORITE ARTISTS =====
    
    /**
     * Add favorite artist
     * @return Only the added artist information
     */
    ArtistResponse addFavoriteArtist(Integer userId, String spotifyId);
    
    /**
     * Remove favorite artist
     * @return Success message
     */
    SuccessResponse removeFavoriteArtist(Integer userId, Integer artistId);
    
    /**
     * Get all favorite artists with pagination
     * Obtener todos los artistas favoritos con paginación
     */
    Page<ArtistResponse> getFavoriteArtists(Integer userId, Pageable pageable);
    
    // ===== FAVORITE GENRES =====
    
    /**
     * Add favorite genre (by ID or name)
     * @return Only the added genre information
     */
    MusicGenreResponse addFavoriteGenre(Integer userId, AddFavoriteGenreRequest request);
    
    /**
     * Remove favorite genre (by ID or name)
     * @return Success message
     */
    SuccessResponse removeFavoriteGenre(Integer userId, DeleteFavoriteGenreRequest request);
    
    /**
     * Get all favorite genres with pagination
     * Obtener todos los géneros favoritos con paginación
     */
    Page<MusicGenreResponse> getFavoriteGenres(Integer userId, Pageable pageable);
    
    /**
     * Get all available music genres
     * Obtener todos los géneros musicales disponibles
     */
    List<MusicGenreResponse> getAllGenres();
}