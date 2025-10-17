package com.example.rockStadium.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating user preferences
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceRequest {
    
    @DecimalMin(value = "5.0", message = "Minimum search radius is 5 km")
    @DecimalMax(value = "30.0", message = "Maximum search radius is 30 km")
    private BigDecimal searchRadiusKm;
    
    private Boolean emailNotifications;
}