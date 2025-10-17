package com.example.rockStadium.service;

import java.util.List;

import com.example.rockStadium.dto.AddFavoriteGenreRequest;
import com.example.rockStadium.dto.ArtistResponse;
import com.example.rockStadium.dto.DeleteFavoriteGenreRequest;
import com.example.rockStadium.dto.MusicGenreResponse;
import com.example.rockStadium.dto.SuccessResponse;
import com.example.rockStadium.dto.UserPreferenceRequest;
import com.example.rockStadium.dto.UserPreferenceResponse;

/**
 * Service interface for user preferences management
 */
public interface UserPreferenceService {
    
    // ===== SEARCH PREFERENCES =====
    
    /**
     * Create or update user preferences
     */
    UserPreferenceResponse createOrUpdatePreferences(Integer userId, UserPreferenceRequest request);
    
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
     * Get all favorite artists
     */
    List<ArtistResponse> getFavoriteArtists(Integer userId);
    
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
     * Get all favorite genres
     */
    List<MusicGenreResponse> getFavoriteGenres(Integer userId);
    
    /**
     * Get all available genres
     */
    List<MusicGenreResponse> getAllGenres();
}