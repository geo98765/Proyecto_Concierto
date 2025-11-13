package com.example.rockStadium.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConcertDto {
    private Integer concertId;
    private String name;
    private LocalDateTime eventDate;
    private BigDecimal priceMin;
    private BigDecimal priceMax;
    private String status;
    private String description;
    private String ticketUrl;
    private String imageUrl;
    
    // Información del artista
    private Integer artistId;
    private String artistName;
    private String artistImageUrl;
    
    // Información del venue
    private Integer venueId;
    private String venueName;
    private String venueCity;
    private BigDecimal venueLatitude;
    private BigDecimal venueLongitude;
    
    // Géneros
    private List<String> genres;
    
    // Distancia (para búsquedas geográficas)
    private Double distanceKm;
}