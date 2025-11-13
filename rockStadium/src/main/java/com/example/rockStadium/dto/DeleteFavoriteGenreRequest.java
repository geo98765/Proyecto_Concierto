package com.example.rockStadium.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for removing favorite genre
 * Supports removing by either genre ID or genre name
 * At least one must be provided
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteFavoriteGenreRequest {
    
    @Schema(description = "Genre ID from database", example = "5")
    private Integer genreId;
    
    @Schema(description = "Genre name (case-insensitive)", example = "Rock")
    private String genreName;
    
    /**
     * Validate that at least one search parameter is provided
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return genreId != null || (genreName != null && !genreName.trim().isEmpty());
    }
}