package com.example.rockStadium.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.rockStadium.dto.NearbySearchResponse;
import com.example.rockStadium.dto.VenueRequest;
import com.example.rockStadium.dto.VenueResponse;
import com.example.rockStadium.dto.VenueSearchRequest;
import com.example.rockStadium.service.VenueService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class VenueController {
    
    private final VenueService venueService;
    
    @PostMapping
    public ResponseEntity<VenueResponse> createVenue(@Valid @RequestBody VenueRequest request) {
        VenueResponse response = venueService.createVenue(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{venueId}")
    public ResponseEntity<VenueResponse> updateVenue(
            @PathVariable Integer venueId,
            @Valid @RequestBody VenueRequest request) {
        VenueResponse response = venueService.updateVenue(venueId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{venueId}")
    public ResponseEntity<VenueResponse> getVenueById(@PathVariable Integer venueId) {
        VenueResponse response = venueService.getVenueById(venueId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<VenueResponse>> getAllVenues() {
        List<VenueResponse> response = venueService.getAllVenues();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/city/{city}")
    public ResponseEntity<List<VenueResponse>> getVenuesByCity(@PathVariable String city) {
        List<VenueResponse> response = venueService.getVenuesByCity(city);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/search")
    public ResponseEntity<List<VenueResponse>> searchVenuesNearby(
            @Valid @RequestBody VenueSearchRequest request) {
        List<VenueResponse> response = venueService.searchVenuesNearby(request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{venueId}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Integer venueId) {
        venueService.deleteVenue(venueId);
        return ResponseEntity.noContent().build();
    }
    
    // NUEVOS ENDPOINTS CON SERPAPI
    
    /**
     * Obtiene información enriquecida del venue desde Google Maps
     * GET /api/venues/{venueId}/enriched
     */
    @GetMapping("/{venueId}/enriched")
    public ResponseEntity<VenueResponse> getVenueWithEnrichedInfo(@PathVariable Integer venueId) {
        VenueResponse response = venueService.getVenueWithEnrichedInfo(venueId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Obtiene hoteles cercanos al venue
     * GET /api/venues/{venueId}/nearby/hotels?radius=5000
     */
    @GetMapping("/{venueId}/nearby/hotels")
    public ResponseEntity<NearbySearchResponse> getNearbyHotels(
            @PathVariable Integer venueId,
            @RequestParam(defaultValue = "5000") Integer radius) {
        NearbySearchResponse response = venueService.getNearbyHotels(venueId, radius);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Obtiene restaurantes cercanos al venue
     * GET /api/venues/{venueId}/nearby/restaurants?radius=2000
     */
    @GetMapping("/{venueId}/nearby/restaurants")
    public ResponseEntity<NearbySearchResponse> getNearbyRestaurants(
            @PathVariable Integer venueId,
            @RequestParam(defaultValue = "2000") Integer radius) {
        NearbySearchResponse response = venueService.getNearbyRestaurants(venueId, radius);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Obtiene estacionamientos cercanos al venue
     * GET /api/venues/{venueId}/nearby/parkings
     */
    @GetMapping("/{venueId}/nearby/parkings")
    public ResponseEntity<NearbySearchResponse> getNearbyParkings(@PathVariable Integer venueId) {
        NearbySearchResponse response = venueService.getNearbyParkings(venueId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Obtiene transporte público cercano al venue
     * GET /api/venues/{venueId}/nearby/transport
     */
    @GetMapping("/{venueId}/nearby/transport")
    public ResponseEntity<NearbySearchResponse> getNearbyTransport(@PathVariable Integer venueId) {
        NearbySearchResponse response = venueService.getNearbyTransport(venueId);
        return ResponseEntity.ok(response);
    }
}