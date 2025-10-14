package com.example.rockStadium.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceRequest {
    
    @DecimalMin(value = "5.0", message = "El radio mínimo es 5 km")
    @DecimalMax(value = "50.0", message = "El radio máximo es 50 km")
    private BigDecimal searchRadiusKm;
    
    private Boolean emailNotifications;
    private Boolean pushNotifications;
}