package com.example.rockStadium.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para información detallada de un lugar (hotel, restaurante, parking, etc.)
 * desde SerpApi Google Maps
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceInfoResponse {
    private String name;
    private String address;
    private BigDecimal rating;
    private Integer userRatingsTotal;
    private String phoneNumber;
    private String website;
    private List<String> types;
    private String priceLevel;
    private OpeningHoursDto openingHours;
    private List<ReviewDto> reviews;
}