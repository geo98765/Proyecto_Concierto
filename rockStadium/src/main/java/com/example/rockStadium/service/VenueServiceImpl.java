package com.example.rockStadium.service;

import com.example.rockStadium.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class VenueServiceImpl implements VenueService {
    
    private final SerpApiService serpApiService;
    
    @Override
    public NearbySearchResponse searchVenuesInGoogleMaps(String query) {
        log.info("Buscando venues en Google Maps: {}", query);
        try {
            return serpApiService.searchVenuesByQuery(query);
        } catch (Exception e) {
            log.error("Error buscando venues: {}", e.getMessage(), e);
            throw new RuntimeException("Error al buscar venues en Google Maps", e);
        }
    }
    
    @Override
    public NearbySearchResponse searchVenuesByLocation(Double lat, Double lng, String query) {
        log.info("Buscando venues por ubicación: {},{} con query: {}", lat, lng, query);
        try {
            return serpApiService.searchNearbyPlaces(
                BigDecimal.valueOf(lat),
                BigDecimal.valueOf(lng),
                query,
                10000 // Radio de 10km por defecto
            );
        } catch (Exception e) {
            log.error("Error buscando venues por ubicación: {}", e.getMessage(), e);
            throw new RuntimeException("Error al buscar venues por ubicación", e);
        }
    }
    
    @Override
    public PlaceInfoResponse getVenueDetailsByPlaceId(String placeId) {
        log.info("Obteniendo detalles del venue con Place ID: {}", placeId);
        try {
            return serpApiService.getPlaceDetailsByPlaceId(placeId);
        } catch (Exception e) {
            log.error("Error obteniendo detalles del venue: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener detalles del venue", e);
        }
    }
    
    @Override
    public NearbySearchResponse findVenuesNearby(Double lat, Double lng, Integer radius) {
        log.info("Buscando venues cercanos a: {},{} con radio: {}m", lat, lng, radius);
        try {
            return serpApiService.searchNearbyPlaces(
                BigDecimal.valueOf(lat),
                BigDecimal.valueOf(lng),
                "concert venue",
                radius
            );
        } catch (Exception e) {
            log.error("Error buscando venues cercanos: {}", e.getMessage(), e);
            throw new RuntimeException("Error al buscar venues cercanos", e);
        }
    }
    
    @Override
    public NearbySearchResponse getHotelsNearVenue(String placeId, Integer radius) {
        log.info("Obteniendo hoteles cerca del venue: {}", placeId);
        try {
            // Primero obtenemos los detalles del venue para obtener sus coordenadas
            PlaceInfoResponse venueDetails = getVenueDetailsByPlaceId(placeId);
            
            if (venueDetails.getLocalResults() != null && !venueDetails.getLocalResults().isEmpty()) {
                PlaceInfoResponse.PlaceDetail venue = venueDetails.getLocalResults().get(0);
                
                if (venue.getGpsCoordinates() != null) {
                    return serpApiService.searchNearbyHotels(
                        venue.getGpsCoordinates().getLatitude(),
                        venue.getGpsCoordinates().getLongitude(),
                        radius
                    );
                }
            }
            
            throw new RuntimeException("No se pudieron obtener las coordenadas del venue");
        } catch (Exception e) {
            log.error("Error obteniendo hoteles cerca del venue: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener hoteles", e);
        }
    }
    
    @Override
    public NearbySearchResponse getRestaurantsNearVenue(String placeId, Integer radius) {
        log.info("Obteniendo restaurantes cerca del venue: {}", placeId);
        try {
            PlaceInfoResponse venueDetails = getVenueDetailsByPlaceId(placeId);
            
            if (venueDetails.getLocalResults() != null && !venueDetails.getLocalResults().isEmpty()) {
                PlaceInfoResponse.PlaceDetail venue = venueDetails.getLocalResults().get(0);
                
                if (venue.getGpsCoordinates() != null) {
                    return serpApiService.searchNearbyRestaurants(
                        venue.getGpsCoordinates().getLatitude(),
                        venue.getGpsCoordinates().getLongitude(),
                        radius
                    );
                }
            }
            
            throw new RuntimeException("No se pudieron obtener las coordenadas del venue");
        } catch (Exception e) {
            log.error("Error obteniendo restaurantes cerca del venue: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener restaurantes", e);
        }
    }
    
    @Override
    public NearbySearchResponse getParkingNearVenue(String placeId) {
        log.info("Obteniendo estacionamientos cerca del venue: {}", placeId);
        try {
            PlaceInfoResponse venueDetails = getVenueDetailsByPlaceId(placeId);
            
            if (venueDetails.getLocalResults() != null && !venueDetails.getLocalResults().isEmpty()) {
                PlaceInfoResponse.PlaceDetail venue = venueDetails.getLocalResults().get(0);
                
                if (venue.getGpsCoordinates() != null) {
                    return serpApiService.searchNearbyParkings(
                        venue.getGpsCoordinates().getLatitude(),
                        venue.getGpsCoordinates().getLongitude(),
                        2000
                    );
                }
            }
            
            throw new RuntimeException("No se pudieron obtener las coordenadas del venue");
        } catch (Exception e) {
            log.error("Error obteniendo estacionamientos cerca del venue: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener estacionamientos", e);
        }
    }
    
    @Override
    public NearbySearchResponse getTransportNearVenue(String placeId) {
        log.info("Obteniendo transporte público cerca del venue: {}", placeId);
        try {
            PlaceInfoResponse venueDetails = getVenueDetailsByPlaceId(placeId);
            
            if (venueDetails.getLocalResults() != null && !venueDetails.getLocalResults().isEmpty()) {
                PlaceInfoResponse.PlaceDetail venue = venueDetails.getLocalResults().get(0);
                
                if (venue.getGpsCoordinates() != null) {
                    return serpApiService.searchNearbyTransport(
                        venue.getGpsCoordinates().getLatitude(),
                        venue.getGpsCoordinates().getLongitude()
                    );
                }
            }
            
            throw new RuntimeException("No se pudieron obtener las coordenadas del venue");
        } catch (Exception e) {
            log.error("Error obteniendo transporte cerca del venue: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener transporte", e);
        }
    }
}