package com.example.rockStadium.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddFavoriteGenreRequest {
    
    @NotNull(message = "El ID del género es requerido")
    private Integer genreId;
}