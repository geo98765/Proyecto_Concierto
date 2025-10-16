package com.example.rockStadium.service;

import com.example.rockStadium.dto.NearbySearchResponse;
import com.example.rockStadium.dto.PlaceInfoResponse;

public interface VenueService {
    
    // ====== MÉTODOS DE BÚSQUEDA EN GOOGLE MAPS ======
    
    /**
     * Buscar venues en Google Maps por nombre o query
     */
    NearbySearchResponse searchVenuesInGoogleMaps(String query);
    
    /**
     * Buscar venues por ubicación geográfica
     */
    NearbySearchResponse searchVenuesByLocation(Double lat, Double lng, String query);
    
    /**
     * Obtener detalles de un venue por query/nombre
     * (Actualizado para usar búsqueda en lugar de Place ID)
     */
    PlaceInfoResponse getVenueDetails(String query);
    
    /**
     * Encontrar venues cercanos a una ubicación
     */
    NearbySearchResponse findVenuesNearby(Double lat, Double lng, Integer radius);
    
    // ====== MÉTODOS PARA OBTENER SERVICIOS CERCA DE UN VENUE ======
    
    /**
     * Obtener hoteles cerca de un venue (identificado por Place ID)
     */
    NearbySearchResponse getHotelsNearVenue(String placeId, Integer radius);
    
    /**
     * Obtener restaurantes cerca de un venue
     */
    NearbySearchResponse getRestaurantsNearVenue(String placeId, Integer radius);
    
    /**
     * Obtener estacionamientos cerca de un venue
     */
    NearbySearchResponse getParkingNearVenue(String placeId);
    
    /**
     * Obtener transporte público cerca de un venue
     */
    NearbySearchResponse getTransportNearVenue(String placeId);
}