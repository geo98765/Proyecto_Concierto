package com.example.rockStadium.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VenueResponse {
    private Integer venueId;
    private String name;
    private String city;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer capacity;
    private Boolean parkingAvailable;
    private WeatherResponse weather;
}
