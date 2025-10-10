package com.example.rockStadium.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.rockStadium.dto.NearbyPlaceDto;
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
        log.info("Buscando hoteles para venue {} con distancia máxima {}", venueId, maxDistance);
        
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> {
                    log.error("Recinto no encontrado: {}", venueId);
                    return new RuntimeException("Recinto con ID " + venueId + " no encontrado");
                });
        
        log.info("Venue encontrado: {} en {},{}", venue.getName(), venue.getLatitude(), venue.getLongitude());
        
        try {
            NearbySearchResponse response = serpApiService.searchNearbyHotels(
                venue.getLatitude(),
                venue.getLongitude(),
                maxDistance != null ? maxDistance : 5000
            );
            
            // Limitar a 20 resultados
            if (response.getLocalResults() != null && response.getLocalResults().size() > 20) {
                response.setLocalResults(response.getLocalResults().subList(0, 20));
            }
            
            return response;
        } catch (Exception e) {
            log.error("Error al buscar hoteles: {}", e.getMessage(), e);
            throw new RuntimeException("Error al buscar hoteles: " + e.getMessage(), e);
        }
    }
    
    /**
     * Funcionalidad 29: Obtener detalles de hotel con opiniones
     */
    public PlaceInfoResponse getHotelDetails(String hotelName, String location) {
        log.info("Obteniendo detalles de hotel: {} en {}", hotelName, location);
        
        try {
            return serpApiService.getPlaceDetails(hotelName, location);
        } catch (Exception e) {
            log.error("Error al obtener detalles del hotel: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener detalles del hotel: " + e.getMessage(), e);
        }
    }
    
    /**
     * Funcionalidad 30: Filtrar hoteles por calificación mínima
     */
    public NearbySearchResponse filterHotelsByRating(Integer venueId, BigDecimal minRating) {
        log.info("Filtrando hoteles con rating mínimo: {} para venue {}", minRating, venueId);
        
        NearbySearchResponse hotels = getHotelsNearVenue(venueId, null);
        
        if (hotels.getLocalResults() != null) {
            List<NearbyPlaceDto> filteredHotels = hotels.getLocalResults().stream()
                    .filter(hotel -> hotel.getRating() != null && 
                            hotel.getRating().compareTo(minRating) >= 0)
                    .collect(Collectors.toList());
            
            hotels.setLocalResults(filteredHotels);
            log.info("Hoteles después del filtro: {} de un total original", filteredHotels.size());
        }
        
        return hotels;
    }
    
    /**
     * Funcionalidad 31: Filtrar hoteles por distancia máxima
     */
    public NearbySearchResponse filterHotelsByDistance(Integer venueId, Integer maxDistance) {
        log.info("Filtrando hoteles con distancia máxima: {}m para venue {}", maxDistance, venueId);
        
        // Obtener el venue para calcular distancias
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado"));
        
        NearbySearchResponse hotels = getHotelsNearVenue(venueId, null);
        
        if (hotels.getLocalResults() != null && maxDistance != null) {
            List<NearbyPlaceDto> filteredHotels = hotels.getLocalResults().stream()
                    .filter(hotel -> {
                        if (hotel.getGpsCoordinates() != null) {
                            double distance = calculateDistance(
                                    venue.getLatitude(),
                                    venue.getLongitude(),
                                    hotel.getGpsCoordinates().getLatitude(),
                                    hotel.getGpsCoordinates().getLongitude()
                            );
                            return distance <= maxDistance;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
            
            hotels.setLocalResults(filteredHotels);
            log.info("Hoteles dentro de {}m: {}", maxDistance, filteredHotels.size());
        }
        
        return hotels;
    }
    
    /**
     * Calcula la distancia entre dos puntos geográficos en metros
     * Usando la fórmula de Haversine
     */
    private double calculateDistance(BigDecimal lat1, BigDecimal lon1, 
                                     BigDecimal lat2, BigDecimal lon2) {
        final int R = 6371000; // Radio de la Tierra en metros
        
        double latDistance = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double lonDistance = Math.toRadians(lon2.doubleValue() - lon1.doubleValue());
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1.doubleValue())) 
                * Math.cos(Math.toRadians(lat2.doubleValue()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
}