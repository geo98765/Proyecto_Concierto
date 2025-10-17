package com.example.rockStadium.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for adding favorite genre
 * Supports adding by either genre ID or genre name
 * At least one must be provided
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddFavoriteGenreRequest {
    
    /**
     * Genre ID (from database)
     * Either genreId or genreName must be provided
     */
    private Integer genreId;
    
    /**
     * Genre name (case-insensitive search)
     * Either genreId or genreName must be provided
     */
    private String genreName;
    
    /**
     * Validate that at least one search parameter is provided
     */
    public boolean isValid() {
        return genreId != null || (genreName != null && !genreName.trim().isEmpty());
    }
}