package com.example.rockstadium.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.rockstadium.dto.AddFavoriteArtistRequest;
import com.example.rockstadium.dto.AddFavoriteGenreRequest;
import com.example.rockstadium.dto.ArtistResponse;
import com.example.rockstadium.dto.DeleteFavoriteGenreRequest;
import com.example.rockstadium.dto.MusicGenreResponse;
import com.example.rockstadium.dto.SuccessResponse;
import com.example.rockstadium.dto.UserPreferenceBasicResponse;
import com.example.rockstadium.dto.UserPreferenceRequest;
import com.example.rockstadium.dto.UserPreferenceResponse;
import com.example.rockstadium.service.UserPreferenceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/users/{userId}/preferences")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Preferences", description = "Endpoints for managing user preferences, favorite artists and genres")
public class UserPreferenceController {
    
    private final UserPreferenceService preferenceService;
    
    // ===== SEARCH PREFERENCES ===== 
    
    /**
     * Endpoint protegido - requiere autenticación
     * Solo el propio usuario puede ver sus preferencias
     */
    @Operation(
        summary = "Get user preferences",
        description = """
                Returns user search preferences.
                
                **Authentication required** 🔒
                
                **Access:** 
                - Regular users (ROLE_USER): Only their own preferences
                - Administrators (ROLE_ADMIN): Any user's preferences
                
                Use 'full=true' to include complete lists of artists and genres, 
                or 'full=false' for summary with counts only.
                """,
        security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Preferences retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Not authorized to view these preferences"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping
    public ResponseEntity<UserPreferenceResponse> getPreferences(
            @Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Integer userId,
            
            @Parameter(description = "Include full lists of artists and genres", example = "false")
            @RequestParam(defaultValue = "false") boolean full) {
        
        log.info("📋 Getting preferences for user: {} (full: {})", userId, full);
        UserPreferenceResponse response = preferenceService.getPreferences(userId, full);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint protegido - requiere autenticación
     * Solo el propio usuario puede actualizar sus preferencias
     */
    @Operation(
        summary = "Configure or update preferences",
        description = """
                Configures search radius and notification preferences for the user.
                
                **Authentication required** 🔒
                
                **Access:** 
                - Regular users (ROLE_USER): Only their own preferences
                - Administrators (ROLE_ADMIN): Any user's preferences
                
                Returns only the updated basic fields.
                """,
        security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Preferences updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid data"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Not authorized to update these preferences"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping
    public ResponseEntity<UserPreferenceBasicResponse> updatePreferences(
            @Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Integer userId,
            @Valid @RequestBody UserPreferenceRequest request) {
        log.info("⚙️ Updating preferences for user: {}", userId);
        UserPreferenceBasicResponse response = preferenceService.createOrUpdatePreferences(userId, request);
        return ResponseEntity.ok(response);
    }
    
    // ===== FAVORITE ARTISTS =====
    
    /**
     * Endpoint protegido - requiere autenticación
     * Solo el propio usuario puede ver sus artistas favoritos
     */
    @Operation(
        summary = "Get favorite artists",
        description = """
                Retrieves the list of artists marked as favorites by the user with pagination support.
                
                **Authentication required** 🔒
                
                **Access:** 
                - Regular users (ROLE_USER): Only their own favorites
                - Administrators (ROLE_ADMIN): Any user's favorites
                """,
        security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Favorite artists retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Not authorized to view these favorites"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/artists")
    public ResponseEntity<List<ArtistResponse>> getFavoriteArtists(
            @Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Integer userId,
            
            @Parameter(description = "Page number (starts at 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Number of artists per page", example = "10")
            @RequestParam(defaultValue = "10") int pageSize) 
    {
        
        log.info("🎤 Getting favorite artists for user: {} (page: {}, size: {})", userId, page, pageSize);
        
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ArtistResponse> artistsPage = preferenceService.getFavoriteArtists(userId, pageable);
        
        return ResponseEntity.ok(artistsPage.getContent());
    }
    
    /**
     * Endpoint protegido - requiere autenticación
     * Solo el propio usuario puede agregar artistas a sus favoritos
     */
    @Operation(
        summary = "Add favorite artist",
        description = """
                Adds a new artist to user's favorites using Spotify ID.
                
                **Authentication required** 🔒
                
                **Access:** Only the user himself can add to their favorites
                
                Maximum 40 artists allowed. Returns only the added artist information.
                """,
        security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Artist added successfully"),
        @ApiResponse(responseCode = "400", description = "Limit reached, artist already exists in favorites, or invalid Spotify ID"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Not authorized to modify these favorites"),
        @ApiResponse(responseCode = "404", description = "User or artist not found")
    })
    @PostMapping("/artists")
    public ResponseEntity<ArtistResponse> addFavoriteArtist(
            @Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Integer userId,
            @Valid @RequestBody AddFavoriteArtistRequest request) {
        log.info("➕ Adding favorite artist for user: {}", userId);
        ArtistResponse response = preferenceService.addFavoriteArtist(userId, request.getSpotifyId());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint protegido - requiere autenticación
     * Solo el propio usuario puede eliminar artistas de sus favoritos
     */
    @Operation(
        summary = "Remove favorite artist",
        description = """
                Removes an artist from user's favorites.
                
                **Authentication required** 🔒
                
                **Access:** Only the user himself can remove from their favorites
                
                Returns a success message.
                """,
        security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Artist removed successfully"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Not authorized to modify these favorites"),
        @ApiResponse(responseCode = "404", description = "User or artist not found in favorites")
    })
    @DeleteMapping("/artists/{artistId}")
    public ResponseEntity<SuccessResponse> removeFavoriteArtist(
            @Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Integer userId,
            @Parameter(description = "Artist ID in database", example = "5", required = true)
            @PathVariable Integer artistId) {
        log.info("➖ Removing favorite artist for user: {}", userId);
        SuccessResponse response = preferenceService.removeFavoriteArtist(userId, artistId);
        return ResponseEntity.ok(response);
    }
    
    // ===== FAVORITE GENRES =====
    
    /**
     * Endpoint protegido - requiere autenticación
     * Solo el propio usuario puede ver sus géneros favoritos
     */
    @Operation(
        summary = "Get favorite genres",
        description = """
                Retrieves the list of music genres marked as favorites by the user with pagination support.
                
                **Authentication required** 🔒
                
                **Access:** 
                - Regular users (ROLE_USER): Only their own favorites
                - Administrators (ROLE_ADMIN): Any user's favorites
                """,
        security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Favorite genres retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Not authorized to view these favorites"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/genres")
    public ResponseEntity<List<MusicGenreResponse>> getFavoriteGenres(
            @Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Integer userId,
            
            @Parameter(description = "Page number (starts at 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Number of genres per page", example = "10")
            @RequestParam(defaultValue = "10") int pageSize) {
        
        log.info("🎵 Getting favorite genres for user: {} (page: {}, size: {})", userId, page, pageSize);
        
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<MusicGenreResponse> genresPage = preferenceService.getFavoriteGenres(userId, pageable);
        
        return ResponseEntity.ok(genresPage.getContent());
    }
    
    /**
     * Endpoint protegido - requiere autenticación
     * Solo el propio usuario puede agregar géneros a sus favoritos
     */
    @Operation(
        summary = "Add favorite genre",
        description = """
                Adds a music genre to user's favorites.
                
                **Authentication required** 🔒
                
                **Access:** Only the user himself can add to their favorites
                
                Maximum 30 genres allowed. Returns only the added genre information. 
                You can add by either genreId or genreName.
                """,
        security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Genre added successfully"),
        @ApiResponse(responseCode = "400", description = "Limit reached, genre already exists in favorites, or invalid request (must provide genreId or genreName)"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Not authorized to modify these favorites"),
        @ApiResponse(responseCode = "404", description = "User or genre not found")
    })
    @PostMapping("/genres")
    public ResponseEntity<MusicGenreResponse> addFavoriteGenre(
            @Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Integer userId,
            @Valid @RequestBody AddFavoriteGenreRequest request) {
        log.info("➕ Adding favorite genre for user: {}", userId);
        MusicGenreResponse response = preferenceService.addFavoriteGenre(userId, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint protegido - requiere autenticación
     * Solo el propio usuario puede eliminar géneros de sus favoritos
     */
    @Operation(
        summary = "Remove favorite genre",
        description = """
                Removes a music genre from user's favorites.
                
                **Authentication required** 🔒
                
                **Access:** Only the user himself can remove from their favorites
                
                You can remove by either genreId or genreName. Returns a success message.
                """,
        security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Genre removed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request (must provide genreId or genreName)"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Not authorized to modify these favorites"),
        @ApiResponse(responseCode = "404", description = "User or genre not found in favorites")
    })
    @DeleteMapping("/genres")
    public ResponseEntity<SuccessResponse> removeFavoriteGenre(
            @Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Integer userId,
            @Valid @RequestBody DeleteFavoriteGenreRequest request) {
        log.info("➖ Removing favorite genre for user: {}", userId);
        SuccessResponse response = preferenceService.removeFavoriteGenre(userId, request);
        return ResponseEntity.ok(response);
    }
}