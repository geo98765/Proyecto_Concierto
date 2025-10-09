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
public class HotelService {
    
    private final SerpApiService serpApiService;
    private final VenueRepository venueRepository;
    
    /**
     * Funcionalidad 28: Obtener hoteles cercanos a recinto
     * Ordenados por distancia, máximo 20 resultados
     */
    public NearbySearchResponse getHotelsNearVenue(Integer venueId, Integer maxDistance) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado"));
        
        return serpApiService.searchNearbyHotels(
            venue.getLatitude(),
            venue.getLongitude(),
            maxDistance != null ? maxDistance : 5000
        );
    }
    
    /**
     * Funcionalidad 29: Obtener detalles de hotel con opiniones
     */
    public PlaceInfoResponse getHotelDetails(String hotelName, String location) {
        return serpApiService.getPlaceDetails(hotelName, location);
    }
    
    /**
     * Funcionalidad 30: Filtrar hoteles por calificación mínima
     */
    public NearbySearchResponse filterHotelsByRating(Integer venueId, BigDecimal minRating) {
        NearbySearchResponse hotels = getHotelsNearVenue(venueId, null);
        // Aquí filtrarías los resultados por rating >= minRating
        // TODO: Implementar filtrado en la respuesta
        return hotels;
    }
    
    /**
     * Funcionalidad 31: Filtrar hoteles por distancia máxima
     */
    public NearbySearchResponse filterHotelsByDistance(Integer venueId, Integer maxDistance) {
        return getHotelsNearVenue(venueId, maxDistance);
    }
}