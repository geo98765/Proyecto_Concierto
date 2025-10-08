package com.example.rockStadium.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileLocationResponse {
    private Integer profileLocationId;
    private String municipality;
    private String state;
    private String country;
}
