package com.example.rockStadium.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.rockStadium.dto.AddFavoriteArtistRequest;
import com.example.rockStadium.dto.AddFavoriteGenreRequest;
import com.example.rockStadium.dto.ArtistResponse;
import com.example.rockStadium.dto.DeleteFavoriteGenreRequest;
import com.example.rockStadium.dto.MusicGenreResponse;
import com.example.rockStadium.dto.SuccessResponse;
import com.example.rockStadium.dto.UserPreferenceRequest;
import com.example.rockStadium.dto.UserPreferenceResponse;
import com.example.rockStadium.exception.BusinessRuleException;
import com.example.rockStadium.exception.ErrorResponse;
import com.example.rockStadium.exception.ResourceNotFoundException;
import com.example.rockStadium.service.UserPreferenceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    
    // ===== EXCEPTION HANDLERS (Local to this controller) =====
    
    /**
     * Handle BusinessRuleException - Returns 400 Bad Request
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleException(BusinessRuleException ex) {
        log.error("❌ Business rule violation: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .build();
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle ResourceNotFoundException - Returns 404 Not Found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("❌ Resource not found: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .build();
        
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    
    // ===== SEARCH PREFERENCES =====
    
    @Operation(
        summary = "Get user preferences",
        description = "Returns user search preferences. Use 'full=true' to include complete lists of artists and genres, or 'full=false' for summary with counts only."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Preferences retrieved successfully"),
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
    
    @Operation(
        summary = "Configure or update preferences",
        description = "Configures search radius and notification preferences for the user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Preferences updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid data"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping
    public ResponseEntity<UserPreferenceResponse> updatePreferences(
            @Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Integer userId,
            @Valid @RequestBody UserPreferenceRequest request) {
        log.info("⚙️ Updating preferences for user: {}", userId);
        UserPreferenceResponse response = preferenceService.createOrUpdatePreferences(userId, request);
        return ResponseEntity.ok(response);
    }
    
    // ===== FAVORITE ARTISTS =====
    
    @Operation(
        summary = "Get favorite artists",
        description = "Retrieves the list of artists marked as favorites by the user, with pagination support"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Artists retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/artists")
    public ResponseEntity<Page<ArtistResponse>> getFavoriteArtists(
            @Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Integer userId,
            
            @Parameter(
                description = "Page number (starts at 0)",
                example = "0"
            )
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(
                description = "Number of favorite artists per page",
                example = "10"
            )
            @RequestParam(defaultValue = "10") int pageSize) {
        
        log.info("🎸 Getting favorite artists for user: {} (page: {}, size: {})", 
            userId, page, pageSize);
        
        // Get all artists from service
        List<ArtistResponse> allArtists = preferenceService.getFavoriteArtists(userId);
        
        // MANUAL PAGINATION LOGIC
        int start = page * pageSize;
        int end = Math.min((start + pageSize), allArtists.size());
        
        // Handle edge case where start is beyond list size
        if (start >= allArtists.size()) {
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<ArtistResponse> emptyPage = new PageImpl<>(List.of(), pageable, allArtists.size());
            return ResponseEntity.ok(emptyPage);
        }
        
        List<ArtistResponse> pageContent = allArtists.subList(start, end);
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ArtistResponse> pageResponse = new PageImpl<>(pageContent, pageable, allArtists.size());
        
        return ResponseEntity.ok(pageResponse);
    }
    
    @Operation(
        summary = "Add favorite artist",
        description = "Adds an artist to user's favorites. Maximum 50 artists allowed. Returns only the added artist information."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Artist added successfully"),
        @ApiResponse(responseCode = "400", description = "Limit reached or artist already exists in favorites"),
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
    
    @Operation(
        summary = "Remove favorite artist",
        description = "Removes an artist from user's favorites. Returns a success message."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Artist removed successfully"),
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
    
    @Operation(
        summary = "Get favorite genres",
        description = "Retrieves the list of music genres marked as favorites by the user, with pagination support"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Genres retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/genres")
    public ResponseEntity<Page<MusicGenreResponse>> getFavoriteGenres(
            @Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Integer userId,
            
            @Parameter(
                description = "Page number (starts at 0)",
                example = "0"
            )
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(
                description = "Number of favorite genres per page",
                example = "10"
            )
            @RequestParam(defaultValue = "10") int pageSize) {
        
        log.info("🎵 Getting favorite genres for user: {} (page: {}, size: {})", 
            userId, page, pageSize);
        
        // Get all genres from service
        List<MusicGenreResponse> allGenres = preferenceService.getFavoriteGenres(userId);
        
        // MANUAL PAGINATION LOGIC
        int start = page * pageSize;
        int end = Math.min((start + pageSize), allGenres.size());
        
        // Handle edge case where start is beyond list size
        if (start >= allGenres.size()) {
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<MusicGenreResponse> emptyPage = new PageImpl<>(List.of(), pageable, allGenres.size());
            return ResponseEntity.ok(emptyPage);
        }
        
        List<MusicGenreResponse> pageContent = allGenres.subList(start, end);
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<MusicGenreResponse> pageResponse = new PageImpl<>(pageContent, pageable, allGenres.size());
        
        return ResponseEntity.ok(pageResponse);
    }
    
    @Operation(
        summary = "Add favorite genre",
        description = "Adds a music genre to user's favorites. Maximum 10 genres allowed. Returns only the added genre information. You can add by either genreId or genreName."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Genre added successfully"),
        @ApiResponse(responseCode = "400", description = "Limit reached, genre already exists in favorites, or invalid request (must provide genreId or genreName)"),
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
    
    @Operation(
        summary = "Remove favorite genre",
        description = "Removes a music genre from user's favorites. You can remove by either genreId or genreName. Returns a success message."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Genre removed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request (must provide genreId or genreName)"),
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
    
    @Operation(
        summary = "Get all available genres",
        description = "Returns the complete catalog of available music genres with pagination support"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Genres retrieved successfully")
    })
    @GetMapping("/genres/available")
    public ResponseEntity<Page<MusicGenreResponse>> getAllGenres(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Integer userId,
            
            @Parameter(
                description = "Page number (starts at 0)",
                example = "0"
            )
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(
                description = "Number of genres per page",
                example = "20"
            )
            @RequestParam(defaultValue = "20") int pageSize) {
        
        log.info("📚 Getting genres catalog (page: {}, size: {})", page, pageSize);
        
        // Get all genres from service
        List<MusicGenreResponse> allGenres = preferenceService.getAllGenres();
        
        // MANUAL PAGINATION LOGIC
        int start = page * pageSize;
        int end = Math.min((start + pageSize), allGenres.size());
        
        // Handle edge case where start is beyond list size
        if (start >= allGenres.size()) {
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<MusicGenreResponse> emptyPage = new PageImpl<>(List.of(), pageable, allGenres.size());
            return ResponseEntity.ok(emptyPage);
        }
        
        List<MusicGenreResponse> pageContent = allGenres.subList(start, end);
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<MusicGenreResponse> pageResponse = new PageImpl<>(pageContent, pageable, allGenres.size());
        
        return ResponseEntity.ok(pageResponse);
    }
}