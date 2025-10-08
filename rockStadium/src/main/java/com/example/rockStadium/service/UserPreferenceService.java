package com.example.rockStadium.service;

import com.example.rockStadium.dto.UserPreferenceRequest;
import com.example.rockStadium.dto.UserPreferenceResponse;

public interface UserPreferenceService {

    UserPreferenceResponse create(UserPreferenceRequest req);

	UserPreferenceResponse update(Integer controlNumber, UserPreferenceRequest preferenceId);

}
