package com.example.rockStadium.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// ===== ADD ARTIST REQUEST =====
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddFavoriteArtistRequest {
    @NotNull(message = "El Spotify ID del artista es requerido")
    private String spotifyId;
}