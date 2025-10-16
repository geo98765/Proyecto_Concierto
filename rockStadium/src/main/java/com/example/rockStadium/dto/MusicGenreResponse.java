package com.example.rockStadium.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de género musical
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicGenreResponse {
    private Integer musicGenreId;
    private String name;
    private String description;
}