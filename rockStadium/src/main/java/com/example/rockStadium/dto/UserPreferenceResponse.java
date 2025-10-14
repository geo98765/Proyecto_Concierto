package com.example.rockStadium.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceResponse {
    private Integer preferenceId;
    private Integer userId;
    private BigDecimal searchRadiusKm;
    private Boolean emailNotifications;
    private Boolean pushNotifications;
    private List<ArtistResponse> favoriteArtists;
    private Integer favoriteArtistsCount;
    private Integer favoriteGenresCount;
}