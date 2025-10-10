package com.example.rockStadium.controller;

import com.example.rockStadium.dto.*;
import com.example.rockStadium.service.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class VenueController {
    
    private final VenueService venueService;
    
    // ====== SOLO ENDPOINTS DE CONSULTA (READ) ======
    
    /**
     * GET /api/venues/search-maps?query=Foro+Sol+Ciudad+Mexico
     * Buscar venues directamente en Google Maps vía SerpApi
     */
    @GetMapping("/search-maps")
    public ResponseEntity<NearbySearchResponse> searchVenuesInGoogleMaps(
            @RequestParam String query) {
        NearbySearchResponse response = venueService.searchVenuesInGoogleMaps(query);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/venues/search-maps-by-location?lat=19.4326&lng=-99.1332&query=concert+venue
     * Buscar venues por ubicación y tipo
     */
    @GetMapping("/search-maps-by-location")
    public ResponseEntity<NearbySearchResponse> searchVenuesByLocation(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "concert venue") String query) {
        NearbySearchResponse response = venueService.searchVenuesByLocation(lat, lng, query);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/venues/details?placeId=ChIJWcJp4Hvf3IAROrEQwsquI48
     * Obtener detalles completos de un venue por su Place ID de Google
     */
    @GetMapping("/details")
    public ResponseEntity<PlaceInfoResponse> getVenueDetailsByPlaceId(
            @RequestParam String placeId) {
        PlaceInfoResponse response = venueService.getVenueDetailsByPlaceId(placeId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/venues/nearby?lat=19.4326&lng=-99.1332&radius=10000
     * Buscar venues cercanos a una ubicación
     */
    @GetMapping("/nearby")
    public ResponseEntity<NearbySearchResponse> findVenuesNearby(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "10000") Integer radius) {
        NearbySearchResponse response = venueService.findVenuesNearby(lat, lng, radius);
        return ResponseEntity.ok(response);
    }
    
    // ====== ENDPOINTS DE SERVICIOS CERCANOS A UN VENUE ======
    
    /**
     * GET /api/venues/hotels?placeId=ChIJ...&radius=5000
     * Obtiene hoteles cercanos a un venue específico (por Place ID)
     */
    @GetMapping("/hotels")
    public ResponseEntity<NearbySearchResponse> getHotelsNearVenue(
            @RequestParam String placeId,
            @RequestParam(defaultValue = "5000") Integer radius) {
        NearbySearchResponse response = venueService.getHotelsNearVenue(placeId, radius);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/venues/restaurants?placeId=ChIJ...&radius=2000
     * Obtiene restaurantes cercanos al venue
     */
    @GetMapping("/restaurants")
    public ResponseEntity<NearbySearchResponse> getRestaurantsNearVenue(
            @RequestParam String placeId,
            @RequestParam(defaultValue = "2000") Integer radius) {
        NearbySearchResponse response = venueService.getRestaurantsNearVenue(placeId, radius);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/venues/parking?placeId=ChIJ...
     * Obtiene estacionamientos cercanos al venue
     */
    @GetMapping("/parking")
    public ResponseEntity<NearbySearchResponse> getParkingNearVenue(@RequestParam String placeId) {
        NearbySearchResponse response = venueService.getParkingNearVenue(placeId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/venues/transport?placeId=ChIJ...
     * Obtiene transporte público cercano al venue
     */
    @GetMapping("/transport")
    public ResponseEntity<NearbySearchResponse> getTransportNearVenue(@RequestParam String placeId) {
        NearbySearchResponse response = venueService.getTransportNearVenue(placeId);
        return ResponseEntity.ok(response);
    }
}