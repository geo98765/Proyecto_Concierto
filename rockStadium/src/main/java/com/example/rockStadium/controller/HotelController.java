package com.example.rockStadium.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.rockStadium.dto.NearbySearchResponse;
import com.example.rockStadium.dto.PlaceInfoResponse;
import com.example.rockStadium.service.HotelService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
public class HotelController {
    
    private final HotelService hotelService;
    
    /**
     * GET /api/hotels/near-venue/{venueId}?maxDistance=5000
     * Funcionalidad 28: Obtener hoteles cercanos a recinto
     */
    @GetMapping("/near-venue/{venueId}")
    public ResponseEntity<NearbySearchResponse> getHotelsNearVenue(
            @PathVariable Integer venueId,
            @RequestParam(required = false) Integer maxDistance) {
        NearbySearchResponse response = hotelService.getHotelsNearVenue(venueId, maxDistance);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/hotels/details?name=Hotel+Name&location=19.4326,-99.1332
     * Funcionalidad 29: Obtener detalles de hotel con opiniones
     */
    @GetMapping("/details")
    public ResponseEntity<PlaceInfoResponse> getHotelDetails(
            @RequestParam String name,
            @RequestParam String location) {
        PlaceInfoResponse response = hotelService.getHotelDetails(name, location);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/hotels/filter-by-rating/{venueId}?minRating=4.0
     * Funcionalidad 30: Filtrar hoteles por calificación mínima (1-5 estrellas)
     */
    @GetMapping("/filter-by-rating/{venueId}")
    public ResponseEntity<NearbySearchResponse> filterHotelsByRating(
            @PathVariable Integer venueId,
            @RequestParam BigDecimal minRating) {
        NearbySearchResponse response = hotelService.filterHotelsByRating(venueId, minRating);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/hotels/filter-by-distance/{venueId}?maxDistance=3000
     * Funcionalidad 31: Filtrar hoteles dentro de distancia máxima del recinto
     */
    @GetMapping("/filter-by-distance/{venueId}")
    public ResponseEntity<NearbySearchResponse> filterHotelsByDistance(
            @PathVariable Integer venueId,
            @RequestParam Integer maxDistance) {
        NearbySearchResponse response = hotelService.filterHotelsByDistance(venueId, maxDistance);
        return ResponseEntity.ok(response);
    }

    
}