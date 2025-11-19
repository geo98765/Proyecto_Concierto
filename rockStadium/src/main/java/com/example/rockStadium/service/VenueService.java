package com.example.rockstadium.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.rockstadium.dto.NearbyPlaceDto;
import com.example.rockstadium.dto.PlaceInfoResponse;

/**
 * Interfaz de servicio para gestión de venues (recintos de conciertos)
 * Maneja búsqueda de venues y servicios cercanos con paginación
 */
public interface VenueService {
    
    // ====== MÉTODOS DE BÚSQUEDA DE VENUES ======
    
    /**
     * Buscar venues en Google Maps por nombre o query con paginación
     * 
     * @param query Nombre del venue o términos de búsqueda
     * @param pageable Configuración de paginación
     * @return Página con venues encontrados
     */
    Page<NearbyPlaceDto> searchVenuesInGoogleMaps(String query, Pageable pageable);
    
    /**
     * Buscar venues por ubicación geográfica con paginación
     * 
     * @param lat Latitud
     * @param lng Longitud  
     * @param query Tipo de lugar a buscar (ej: "concert venue")
     * @param pageable Configuración de paginación
     * @return Página con venues cercanos
     */
    Page<NearbyPlaceDto> searchVenuesByLocation(Double lat, Double lng, String query, Pageable pageable);
    
    /**
     * Obtener detalles de un venue por query/nombre
     * (SIN paginación - resultado único)
     * 
     * @param query Nombre del venue o query de búsqueda
     * @return Información detallada del venue
     */
    PlaceInfoResponse getVenueDetails(String query);
    
    /**
     * Encontrar venues cercanos a una ubicación con paginación
     * 
     * @param lat Latitud de ubicación central
     * @param lng Longitud de ubicación central
     * @param radius Radio de búsqueda en metros
     * @param pageable Configuración de paginación
     * @return Página con venues cercanos
     */
    Page<NearbyPlaceDto> findVenuesNearby(Double lat, Double lng, Integer radius, Pageable pageable);
    
    // ====== MÉTODOS DE SERVICIOS CERCANOS ======
    
    /**
     * Obtener hoteles cerca de un venue con paginación
     * 
     * @param placeId Nombre del venue o Place ID
     * @param radius Radio de búsqueda en metros
     * @param pageable Configuración de paginación
     * @return Página con hoteles cercanos
     */
    Page<NearbyPlaceDto> getHotelsNearVenue(String placeId, Integer radius, Pageable pageable);
    
    /**
     * Obtener restaurantes cerca de un venue con paginación
     * 
     * @param placeId Nombre del venue o Place ID
     * @param radius Radio de búsqueda en metros
     * @param pageable Configuración de paginación
     * @return Página con restaurantes cercanos
     */
    Page<NearbyPlaceDto> getRestaurantsNearVenue(String placeId, Integer radius, Pageable pageable);
    
    /**
     * Obtener estacionamientos cerca de un venue con paginación
     * 
     * @param placeId Nombre del venue o Place ID
     * @param pageable Configuración de paginación
     * @return Página con estacionamientos cercanos
     */
    Page<NearbyPlaceDto> getParkingNearVenue(String placeId, Pageable pageable);
    
    /**
     * Obtener transporte público cerca de un venue con paginación
     * 
     * @param placeId Nombre del venue o Place ID
     * @param pageable Configuración de paginación
     * @return Página con opciones de transporte cercanas
     */
    Page<NearbyPlaceDto> getTransportNearVenue(String placeId, Pageable pageable);
}