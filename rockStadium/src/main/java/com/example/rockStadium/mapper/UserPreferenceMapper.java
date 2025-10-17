package com.example.rockStadium.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.rockStadium.dto.ArtistResponse;
import com.example.rockStadium.dto.MusicGenreResponse;
import com.example.rockStadium.dto.UserPreferenceResponse;
import com.example.rockStadium.model.Artist;
import com.example.rockStadium.model.MusicGenre;
import com.example.rockStadium.model.UserPreference;
import com.example.rockStadium.service.SpotifyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Mapper for UserPreference entities and DTOs
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserPreferenceMapper {
    
    private final SpotifyService spotifyService;
    
    private static final int MAX_FAVORITE_ARTISTS = 50;
    private static final int MAX_FAVORITE_GENRES = 30;
    
    /**
     * Convert UserPreference entity to Response DTO
     */
    public UserPreferenceResponse toResponse(UserPreference preference, 
                                             List<ArtistResponse> artists,
                                             List<MusicGenreResponse> genres) {
        return UserPreferenceResponse.builder()
                .userPreferenceId(preference.getUserPreferenceId())
                .profileId(preference.getProfile().getProfileId())
                .searchRadius(preference.getSearchRadius())
                .emailNotifications(preference.getEmailNotifications())
                .favoriteArtists(artists)
                .favoriteGenres(genres)
                .favoriteArtistsCount(artists.size())
                .favoriteGenresCount(genres.size())
                .maxFavoriteArtists(MAX_FAVORITE_ARTISTS)
                .maxFavoriteGenres(MAX_FAVORITE_GENRES)
                .build();
    }
    
    /**
     * Convert Artist entity to Response DTO
     * Attempts to fetch fresh data from Spotify if available
     */
    public ArtistResponse toArtistResponse(Artist artist) {
        // Try to get fresh data from Spotify
        if (artist.getSpotifyId() != null) {
            try {
                return spotifyService.getArtistById(artist.getSpotifyId());
            } catch (Exception e) {
                log.warn("Failed to fetch Spotify info for artist: {} - Error: {}", 
                    artist.getSpotifyId(), e.getMessage());
            }
        }
        
        // Fallback to database data
        return buildFallbackArtistResponse(artist);
    }
    
    /**
     * Convert MusicGenre entity to Response DTO
     */
    public MusicGenreResponse toGenreResponse(MusicGenre genre) {
        return MusicGenreResponse.builder()
                .musicGenreId(genre.getMusicGenreId())
                .name(genre.getName())
                .description(genre.getDescription())
                .build();
    }
    
    /**
     * Build fallback artist response from database data
     */
    private ArtistResponse buildFallbackArtistResponse(Artist artist) {
        return ArtistResponse.builder()
                .spotifyId(artist.getSpotifyId())
                .name(artist.getName())
                .build();
    }
}