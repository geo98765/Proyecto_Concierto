package com.example.rockStadium.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserPreferenceResponse {
    Integer preferenceId;
    Integer searchRadius;
    Boolean emailNotifications;
}
