package com.example.rockStadium.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VenueSearchRequest {
    
    @NotNull(message = "La latitud es requerida")
    private BigDecimal latitude;
    
    @NotNull(message = "La longitud es requerida")
    private BigDecimal longitude;
    
    @NotNull(message = "El radio de búsqueda es requerido")
    private BigDecimal radius;
}