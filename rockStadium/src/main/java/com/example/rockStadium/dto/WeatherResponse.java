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
public class WeatherResponse {
    private Integer weatherId;
    private String weatherType;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal precipitation;
    private BigDecimal wind;
}