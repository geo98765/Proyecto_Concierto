package com.example.rockStadium.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserPreferenceRequest {
    @NotBlank
    @Size(max = 100)
    private Integer searchRadius;

}
