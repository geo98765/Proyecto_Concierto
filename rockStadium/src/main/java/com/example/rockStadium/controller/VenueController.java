package com.example.rockStadium.controller;

import com.example.rockStadium.dto.VenueRequest;
import com.example.rockStadium.dto.VenueResponse;
import com.example.rockStadium.dto.VenueSearchRequest;
import com.example.rockStadium.service.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}