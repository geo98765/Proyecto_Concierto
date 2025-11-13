package com.example.rockStadium.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VenueRequest {
    
    @NotBlank(message = "El nombre del recinto es requerido")
    @Size(max = 255)
    private String name;
    
    @NotBlank(message = "La ciudad es requerida")
    @Size(max = 100)
    private String city;
    
    @NotNull(message = "La latitud es requerida")
    private BigDecimal latitude;
    
    @NotNull(message = "La longitud es requerida")
    private BigDecimal longitude;
    
    private Integer capacity;
    
    private Boolean parkingAvailable;
}
