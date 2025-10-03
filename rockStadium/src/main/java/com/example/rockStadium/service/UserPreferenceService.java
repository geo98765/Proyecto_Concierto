package com.example.rockStadium.service;

import com.example.rockStadium.dto.UserPreferenceRequest;
import com.example.rockStadium.dto.UserPreferenceResponce;

public interface UserPreferenceService {

    UserPreferenceResponce create(UserPreferenceRequest req);

	UserPreferenceResponce update(Integer controlNumber, UserPreferenceRequest preferenceId);

}
