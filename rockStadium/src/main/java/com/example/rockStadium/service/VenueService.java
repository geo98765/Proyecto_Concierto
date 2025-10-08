package com.example.rockStadium.service;

import com.example.rockStadium.dto.VenueRequest;
import com.example.rockStadium.dto.VenueResponse;
import com.example.rockStadium.dto.VenueSearchRequest;

import java.util.List;

public interface VenueService {
    VenueResponse createVenue(VenueRequest request);
    VenueResponse updateVenue(Integer venueId, VenueRequest request);
    VenueResponse getVenueById(Integer venueId);
    List<VenueResponse> getAllVenues();
    List<VenueResponse> getVenuesByCity(String city);
    List<VenueResponse> searchVenuesNearby(VenueSearchRequest request);
    void deleteVenue(Integer venueId);
}