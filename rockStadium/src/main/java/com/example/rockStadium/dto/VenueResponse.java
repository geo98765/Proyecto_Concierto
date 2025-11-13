package com.example.rockStadium.dto;


import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
