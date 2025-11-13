package com.example.rockStadium.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.example.rockStadium.dto.NearbySearchResponse;
import com.example.rockStadium.dto.PlaceInfoResponse;
import com.example.rockStadium.model.Venue;
import com.example.rockStadium.repository.VenueRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransportService {
    
    private final SerpApiService serpApiService;
    private final VenueRepository venueRepository;
    
    /**
     * Funcionalidad 32: Obtener opciones de transporte público
     * Incluye estaciones/paradas cercanas de metro, autobús, tren
     */
    public NearbySearchResponse getTransportOptions(Integer venueId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado"));
        
        return serpApiService.searchNearbyTransport(
            venue.getLatitude(),
            venue.getLongitude()
        );
    }
    
    /**
     * Buscar estaciones de metro cercanas
     */
    public NearbySearchResponse getNearbyMetroStations(Integer venueId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado"));
        
        return serpApiService.searchNearbyPlaces(
            venue.getLatitude(),
            venue.getLongitude(),
            "metro station",
            1000
        );
    }
    
    /**
     * Buscar paradas de autobús cercanas
     */
    public NearbySearchResponse getNearbyBusStops(Integer venueId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado"));
        
        return serpApiService.searchNearbyPlaces(
            venue.getLatitude(),
            venue.getLongitude(),
            "bus stop",
            800
        );
    }
    
    /**
     * Buscar estaciones de tren cercanas
     */
    public NearbySearchResponse getNearbyTrainStations(Integer venueId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado"));
        
        return serpApiService.searchNearbyPlaces(
            venue.getLatitude(),
            venue.getLongitude(),
            "train station",
            2000
        );
    }
    
    /**
     * Funcionalidad 33: Obtener detalles de transporte con opiniones
     */
    public PlaceInfoResponse getTransportDetails(String transportName, String location) {
        return serpApiService.getPlaceDetails(transportName, location);
    }
    
    /**
     * Funcionalidad 34: Obtener rutas de transporte
     * Desde ubicación del usuario hasta el recinto
     * Nota: Para rutas completas, considera usar Google Directions API
     */
    public String getTransportRoute(
            BigDecimal startLat, 
            BigDecimal startLng,
            Integer venueId) {
        
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado"));
        
        // Aquí integrarías Google Directions API a través de SerpApi
        // Por ahora retornamos un placeholder
        String route = String.format(
            "Ruta desde (%s, %s) hasta %s (%s, %s)",
            startLat, startLng,
            venue.getName(),
            venue.getLatitude(), venue.getLongitude()
        );
        
        log.info("Calculando ruta: {}", route);
        return route;
    }
    
    /**
     * Funcionalidad 35: Calcular ruta personalizada
     */
    public String calculateCustomRoute(
            BigDecimal startLat,
            BigDecimal startLng,
            BigDecimal endLat,
            BigDecimal endLng) {
        
        String route = String.format(
            "Ruta personalizada desde (%s, %s) hasta (%s, %s)",
            startLat, startLng, endLat, endLng
        );
        
        log.info("Calculando ruta personalizada: {}", route);
        return route;
    }
}