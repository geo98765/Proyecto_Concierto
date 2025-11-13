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
public class ParkingService {
    
    private final SerpApiService serpApiService;
    private final VenueRepository venueRepository;
    
    /**
     * Funcionalidad 36: Obtener estacionamientos cercanos al recinto
     * Incluye capacidad, precio/hora, horarios, distancia
     */
    public NearbySearchResponse getParkingsNearVenue(Integer venueId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado"));
        
        return serpApiService.searchNearbyParkings(
            venue.getLatitude(),
            venue.getLongitude(),
            2000 // 2 km de radio
        );
    }
    
    /**
     * Buscar estacionamientos en un radio específico
     */
    public NearbySearchResponse getParkingsWithinRadius(Integer venueId, Integer radiusMeters) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado"));
        
        return serpApiService.searchNearbyParkings(
            venue.getLatitude(),
            venue.getLongitude(),
            radiusMeters
        );
    }
    
    /**
     * Funcionalidad 37: Obtener detalles completos de estacionamiento
     * Incluye opiniones, horarios, precios, etc.
     */
    public PlaceInfoResponse getParkingDetails(String parkingName, String location) {
        return serpApiService.getPlaceDetails(parkingName, location);
    }
    
    /**
     * Funcionalidad 38: Filtrar estacionamientos por precio máximo
     * Nota: La información de precios puede venir en las opiniones/descripciones
     */
    public NearbySearchResponse filterParkingsByPrice(Integer venueId, BigDecimal maxPrice) {
        NearbySearchResponse parkings = getParkingsNearVenue(venueId);
        
        // TODO: Implementar filtrado por precio
        // Necesitarás parsear la información de precios de las descripciones
        log.info("Filtrando estacionamientos con precio máximo: {}", maxPrice);
        
        return parkings;
    }
    
    /**
     * Buscar estacionamientos gratuitos
     */
    public NearbySearchResponse getFreeParkings(Integer venueId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado"));
        
        return serpApiService.searchNearbyPlaces(
            venue.getLatitude(),
            venue.getLongitude(),
            "free parking",
            2000
        );
    }
    
    /**
     * Buscar estacionamientos cubiertos
     */
    public NearbySearchResponse getCoveredParkings(Integer venueId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado"));
        
        return serpApiService.searchNearbyPlaces(
            venue.getLatitude(),
            venue.getLongitude(),
            "covered parking garage",
            2000
        );
    }
}