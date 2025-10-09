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
import com.example.rockStadium.service.TransportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transport")
@RequiredArgsConstructor
public class TransportController {
    
    private final TransportService transportService;
    
    /**
     * GET /api/transport/options/{venueId}
     * Funcionalidad 32: Obtener opciones de transporte disponibles
     * Incluye estaciones/paradas cercanas (metro, autobús, tren)
     */
    @GetMapping("/options/{venueId}")
    public ResponseEntity<NearbySearchResponse> getTransportOptions(@PathVariable Integer venueId) {
        NearbySearchResponse response = transportService.getTransportOptions(venueId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/transport/metro/{venueId}
     * Obtener estaciones de metro cercanas
     */
    @GetMapping("/metro/{venueId}")
    public ResponseEntity<NearbySearchResponse> getNearbyMetroStations(@PathVariable Integer venueId) {
        NearbySearchResponse response = transportService.getNearbyMetroStations(venueId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/transport/bus/{venueId}
     * Obtener paradas de autobús cercanas
     */
    @GetMapping("/bus/{venueId}")
    public ResponseEntity<NearbySearchResponse> getNearbyBusStops(@PathVariable Integer venueId) {
        NearbySearchResponse response = transportService.getNearbyBusStops(venueId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/transport/train/{venueId}
     * Obtener estaciones de tren cercanas
     */
    @GetMapping("/train/{venueId}")
    public ResponseEntity<NearbySearchResponse> getNearbyTrainStations(@PathVariable Integer venueId) {
        NearbySearchResponse response = transportService.getNearbyTrainStations(venueId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/transport/details?name=Metro+Station&location=19.4326,-99.1332
     * Funcionalidad 33: Obtener detalles específicos de transporte con opiniones
     */
    @GetMapping("/details")
    public ResponseEntity<PlaceInfoResponse> getTransportDetails(
            @RequestParam String name,
            @RequestParam String location) {
        PlaceInfoResponse response = transportService.getTransportDetails(name, location);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/transport/route/{venueId}?startLat=19.4326&startLng=-99.1332
     * Funcionalidad 34: Obtener rutas desde ubicación del usuario hasta el recinto
     * Incluye punto partida/destino, duración y costo aproximado
     */
    @GetMapping("/route/{venueId}")
    public ResponseEntity<String> getTransportRoute(
            @PathVariable Integer venueId,
            @RequestParam BigDecimal startLat,
            @RequestParam BigDecimal startLng) {
        String route = transportService.getTransportRoute(startLat, startLng, venueId);
        return ResponseEntity.ok(route);
    }
    
    /**
     * GET /api/transport/custom-route?startLat=19.4326&startLng=-99.1332&endLat=19.4200&endLng=-99.1500
     * Funcionalidad 35: Calcular ruta personalizada desde coordenadas específicas
     */
    @GetMapping("/custom-route")
    public ResponseEntity<String> calculateCustomRoute(
            @RequestParam BigDecimal startLat,
            @RequestParam BigDecimal startLng,
            @RequestParam BigDecimal endLat,
            @RequestParam BigDecimal endLng) {
        String route = transportService.calculateCustomRoute(startLat, startLng, endLat, endLng);
        return ResponseEntity.ok(route);
    }
}