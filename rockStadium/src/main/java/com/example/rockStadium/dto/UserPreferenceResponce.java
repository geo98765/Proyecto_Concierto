package com.example.rockStadium.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserPreferenceResponce {
    Integer preferenceId;
    Integer searchRadius;
    Boolean emailNotifications;
}
