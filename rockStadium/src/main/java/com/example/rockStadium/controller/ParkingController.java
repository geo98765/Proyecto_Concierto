package com.example.rockStadium.controller;

import com.example.rockStadium.dto.NearbySearchResponse;
import com.example.rockStadium.dto.PlaceInfoResponse;
import com.example.rockStadium.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/parking")
@RequiredArgsConstructor
public class ParkingController {
    
    private final ParkingService parkingService;
    
    /**
     * GET /api/parking/near-venue/{venueId}
     * Funcionalidad 36: Obtener estacionamientos cercanos al recinto
     * Incluye capacidad, precio/hora, horarios, distancia
     */
    @GetMapping("/near-venue/{venueId}")
    public ResponseEntity<NearbySearchResponse> getParkingsNearVenue(@PathVariable Integer venueId) {
        NearbySearchResponse response = parkingService.getParkingsNearVenue(venueId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/parking/within-radius/{venueId}?radius=1500
     * Buscar estacionamientos en un radio específico (metros)
     */
    @GetMapping("/within-radius/{venueId}")
    public ResponseEntity<NearbySearchResponse> getParkingsWithinRadius(
            @PathVariable Integer venueId,
            @RequestParam Integer radius) {
        NearbySearchResponse response = parkingService.getParkingsWithinRadius(venueId, radius);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/parking/details?name=Parking+Name&location=19.4326,-99.1332
     * Funcionalidad 37: Obtener detalles completos del estacionamiento
     */
    @GetMapping("/details")
    public ResponseEntity<PlaceInfoResponse> getParkingDetails(
            @RequestParam String name,
            @RequestParam String location) {
        PlaceInfoResponse response = parkingService.getParkingDetails(name, location);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/parking/filter-by-price/{venueId}?maxPrice=50.00
     * Funcionalidad 38: Estacionamientos con precio máximo especificado
     */
    @GetMapping("/filter-by-price/{venueId}")
    public ResponseEntity<NearbySearchResponse> filterParkingsByPrice(
            @PathVariable Integer venueId,
            @RequestParam BigDecimal maxPrice) {
        NearbySearchResponse response = parkingService.filterParkingsByPrice(venueId, maxPrice);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/parking/free/{venueId}
     * Buscar estacionamientos gratuitos cercanos
     */
    @GetMapping("/free/{venueId}")
    public ResponseEntity<NearbySearchResponse> getFreeParkings(@PathVariable Integer venueId) {
        NearbySearchResponse response = parkingService.getFreeParkings(venueId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/parking/covered/{venueId}
     * Buscar estacionamientos cubiertos cercanos
     */
    @GetMapping("/covered/{venueId}")
    public ResponseEntity<NearbySearchResponse> getCoveredParkings(@PathVariable Integer venueId) {
        NearbySearchResponse response = parkingService.getCoveredParkings(venueId);
        return ResponseEntity.ok(response);
    }
}