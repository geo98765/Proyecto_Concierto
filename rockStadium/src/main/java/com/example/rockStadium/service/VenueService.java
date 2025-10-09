package com.example.rockStadium.service;

import java.util.List;

import com.example.rockStadium.dto.NearbySearchResponse;
import com.example.rockStadium.dto.VenueRequest;
import com.example.rockStadium.dto.VenueResponse;
import com.example.rockStadium.dto.VenueSearchRequest;

public interface VenueService {
    // Métodos originales
    VenueResponse createVenue(VenueRequest request);
    VenueResponse updateVenue(Integer venueId, VenueRequest request);
    VenueResponse getVenueById(Integer venueId);
    List<VenueResponse> getAllVenues();
    List<VenueResponse> getVenuesByCity(String city);
    List<VenueResponse> searchVenuesNearby(VenueSearchRequest request);
    void deleteVenue(Integer venueId);
    
    // Nuevos métodos con SerpApi
    VenueResponse getVenueWithEnrichedInfo(Integer venueId);
    NearbySearchResponse getNearbyHotels(Integer venueId, Integer radius);
    NearbySearchResponse getNearbyRestaurants(Integer venueId, Integer radius);
    NearbySearchResponse getNearbyParkings(Integer venueId);
    NearbySearchResponse getNearbyTransport(Integer venueId);
}