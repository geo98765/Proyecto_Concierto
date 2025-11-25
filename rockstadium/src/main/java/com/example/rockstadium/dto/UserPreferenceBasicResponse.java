package com.example.rockstadium.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Basic response DTO for user preferences update operations
 * Returns only the essential updated fields
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceBasicResponse {
    private Integer userId;
    private Integer userPreferenceId;
    private Integer profileId;
    private BigDecimal searchRadius;
    private Boolean emailNotifications;
}