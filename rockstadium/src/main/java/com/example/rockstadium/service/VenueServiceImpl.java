package com.example.rockstadium.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.rockstadium.dto.NearbyPlaceDto;
import com.example.rockstadium.dto.NearbySearchResponse;
import com.example.rockstadium.dto.PlaceInfoResponse;
import com.example.rockstadium.mapper.VenueMapper;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación del servicio para gestión de venues (recintos)
 * Maneja búsqueda de venues y servicios cercanos con paginación
 * 
 * EXCEPCIONES USADAS (manejadas por RestExceptionHandler):
 * - EntityNotFoundException (jakarta.persistence) → 404 NOT_FOUND
 * - IllegalArgumentException → 400 BAD_REQUEST
 * - RuntimeException → 500 INTERNAL_SERVER_ERROR
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VenueServiceImpl implements VenueService {
    
    private final SerpApiService serpApiService;
    private final VenueMapper venueMapper;
    
    private static final int DEFAULT_SEARCH_RADIUS = 10000; // 10km en metros
    
    // ====== BÚSQUEDA DE VENUES ======
    
    /**
     * Buscar venues en Google Maps por nombre o query con paginación
     * Realiza búsqueda directa usando el nombre del venue
     * 
     * @param query Nombre o términos de búsqueda del venue
     * @param pageable Configuración de paginación
     * @return Página con venues encontrados
     * @throws RuntimeException si hay error en la búsqueda
     */
    @Override
    @Transactional(readOnly = true)
    public Page<NearbyPlaceDto> searchVenuesInGoogleMaps(String query, Pageable pageable) {
        log.info("Buscando venues en Google Maps: {} (page: {}, size: {})", 
                query, pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            // Obtener respuesta de SerpApi (sin paginación nativa)
            NearbySearchResponse serpResponse = serpApiService.searchVenuesByQuery(query);
            
            // Extraer lista completa de resultados
            List<NearbyPlaceDto> allVenues = extractAllVenues(serpResponse);
            
            log.info("✅ Búsqueda completada. Total resultados: {}", allVenues.size());
            
            // Aplicar paginación manual
            return paginateList(allVenues, pageable);
            
        } catch (Exception e) {
            log.error("❌ Error buscando venues en Google Maps: {}", e.getMessage(), e);
            throw new RuntimeException("Error al buscar venues en Google Maps: " + e.getMessage(), e);
        }
    }
    
    /**
     * Buscar venues por ubicación geográfica con paginación
     * Encuentra venues cerca de coordenadas específicas
     * 
     * @param lat Latitud de la ubicación
     * @param lng Longitud de la ubicación
     * @param query Tipo de lugar a buscar (ej: "concert venue")
     * @param pageable Configuración de paginación
     * @return Página con venues cercanos encontrados
     * @throws RuntimeException si hay error en la búsqueda
     */
    @Override
    @Transactional(readOnly = true)
    public Page<NearbyPlaceDto> searchVenuesByLocation(Double lat, Double lng, String query, Pageable pageable) {
        log.info("Buscando venues por ubicación: {},{} con query: {} (page: {}, size: {})", 
                lat, lng, query, pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            NearbySearchResponse serpResponse = serpApiService.searchNearbyPlaces(
                BigDecimal.valueOf(lat),
                BigDecimal.valueOf(lng),
                query,
                DEFAULT_SEARCH_RADIUS
            );
            
            List<NearbyPlaceDto> allVenues = extractAllVenues(serpResponse);
            log.info("✅ Búsqueda por ubicación completada. Total resultados: {}", allVenues.size());
            
            return paginateList(allVenues, pageable);
            
        } catch (Exception e) {
            log.error("❌ Error buscando venues por ubicación: {}", e.getMessage(), e);
            throw new RuntimeException("Error al buscar venues por ubicación: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtener detalles completos de un venue por nombre
     * Busca el venue y retorna información detallada (sin paginación - resultado único)
     * 
     * @param query Nombre o términos de búsqueda del venue
     * @return Información detallada del venue encontrado
     * @throws EntityNotFoundException (jakarta.persistence) si no se encuentra el venue → 404
     * @throws RuntimeException si hay error en la búsqueda → 500
     */
    @Override
    @Transactional(readOnly = true)
    public PlaceInfoResponse getVenueDetails(String query) {
        log.info("Obteniendo detalles del venue: {}", query);
        
        try {
            // Buscar el venue por nombre/query usando SerpApi
            NearbySearchResponse searchResponse = serpApiService.searchVenuesByQuery(query);
            
            // Extraer el primer venue encontrado usando el mapper
            NearbyPlaceDto venue = venueMapper.extractFirstVenue(searchResponse);
            
            if (venue == null) {
                log.warn("⚠️  No se encontró información para: {}", query);
                throw new EntityNotFoundException("No se encontró el venue: " + query);
            }
            
            log.info("✅ Venue encontrado: {}", venue.getTitle());
            
            // Convertir a respuesta detallada usando el mapper
            return venueMapper.toPlaceInfoResponseFromNearby(venue);
            
        } catch (EntityNotFoundException e) {
            // Re-lanzar EntityNotFoundException (manejada por RestExceptionHandler → 404)
            throw e;
        } catch (Exception e) {
            log.error("❌ Error obteniendo detalles del venue: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener detalles del venue: " + e.getMessage(), e);
        }
    }
    
    /**
     * Encontrar venues cercanos a una ubicación con paginación
     * Busca venues dentro de un radio específico
     * 
     * @param lat Latitud de la ubicación central
     * @param lng Longitud de la ubicación central
     * @param radius Radio de búsqueda en metros
     * @param pageable Configuración de paginación
     * @return Página con venues cercanos
     * @throws RuntimeException si hay error en la búsqueda
     */
    @Override
    @Transactional(readOnly = true)
    public Page<NearbyPlaceDto> findVenuesNearby(Double lat, Double lng, Integer radius, Pageable pageable) {
        log.info("Buscando venues cercanos a: {},{} con radio: {}m (page: {}, size: {})", 
                lat, lng, radius, pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            NearbySearchResponse serpResponse = serpApiService.searchNearbyPlaces(
                BigDecimal.valueOf(lat),
                BigDecimal.valueOf(lng),
                "concert venue",
                radius
            );
            
            List<NearbyPlaceDto> allVenues = extractAllVenues(serpResponse);
            log.info("✅ Búsqueda de venues cercanos completada. Total resultados: {}", allVenues.size());
            
            return paginateList(allVenues, pageable);
            
        } catch (Exception e) {
            log.error("❌ Error buscando venues cercanos: {}", e.getMessage(), e);
            throw new RuntimeException("Error al buscar venues cercanos: " + e.getMessage(), e);
        }
    }
    
    // ====== SERVICIOS CERCANOS AL VENUE ======
    
    /**
     * Obtener hoteles cerca de un venue con paginación
     * Busca hoteles dentro de un radio específico del venue
     * 
     * @param placeId Nombre o Place ID del venue
     * @param radius Radio de búsqueda en metros
     * @param pageable Configuración de paginación
     * @return Página con hoteles cercanos
     * @throws EntityNotFoundException (jakarta.persistence) si el venue no existe → 404
     * @throws IllegalArgumentException si las coordenadas son inválidas → 400
     * @throws RuntimeException si hay error en la búsqueda → 500
     */
    @Override
    @Transactional(readOnly = true)
    public Page<NearbyPlaceDto> getHotelsNearVenue(String placeId, Integer radius, Pageable pageable) {
        log.info("Obteniendo hoteles cerca del venue: {} con radio: {}m (page: {}, size: {})", 
                placeId, radius, pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            // Obtener coordenadas del venue
            PlaceInfoResponse venueDetails = getVenueDetails(placeId);
            PlaceInfoResponse.PlaceDetail venue = getFirstPlaceDetail(venueDetails);
            validateGpsCoordinates(venue);
            
            // Buscar hoteles cercanos
            NearbySearchResponse serpResponse = serpApiService.searchNearbyHotels(
                venue.getGpsCoordinates().getLatitude(),
                venue.getGpsCoordinates().getLongitude(),
                radius
            );
            
            List<NearbyPlaceDto> allHotels = extractAllVenues(serpResponse);
            log.info("✅ Búsqueda de hoteles completada. Total resultados: {}", allHotels.size());
            
            return paginateList(allHotels, pageable);
            
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ Error obteniendo hoteles cerca del venue: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener hoteles: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtener restaurantes cerca de un venue con paginación
     * Busca restaurantes dentro de un radio específico del venue
     * 
     * @param placeId Nombre o Place ID del venue
     * @param radius Radio de búsqueda en metros
     * @param pageable Configuración de paginación
     * @return Página con restaurantes cercanos
     * @throws EntityNotFoundException (jakarta.persistence) si el venue no existe → 404
     * @throws IllegalArgumentException si las coordenadas son inválidas → 400
     * @throws RuntimeException si hay error en la búsqueda → 500
     */
    @Override
    @Transactional(readOnly = true)
    public Page<NearbyPlaceDto> getRestaurantsNearVenue(String placeId, Integer radius, Pageable pageable) {
        log.info("Obteniendo restaurantes cerca del venue: {} con radio: {}m (page: {}, size: {})", 
                placeId, radius, pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            // Obtener coordenadas del venue
            PlaceInfoResponse venueDetails = getVenueDetails(placeId);
            PlaceInfoResponse.PlaceDetail venue = getFirstPlaceDetail(venueDetails);
            validateGpsCoordinates(venue);
            
            // Buscar restaurantes cercanos
            NearbySearchResponse serpResponse = serpApiService.searchNearbyRestaurants(
                venue.getGpsCoordinates().getLatitude(),
                venue.getGpsCoordinates().getLongitude(),
                radius
            );
            
            List<NearbyPlaceDto> allRestaurants = extractAllVenues(serpResponse);
            log.info("✅ Búsqueda de restaurantes completada. Total resultados: {}", allRestaurants.size());
            
            return paginateList(allRestaurants, pageable);
            
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ Error obteniendo restaurantes cerca del venue: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener restaurantes: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtener estacionamientos cerca de un venue con paginación
     * Busca estacionamientos disponibles cerca del venue
     * 
     * @param placeId Nombre o Place ID del venue
     * @param pageable Configuración de paginación
     * @return Página con estacionamientos cercanos
     * @throws EntityNotFoundException (jakarta.persistence) si el venue no existe → 404
     * @throws IllegalArgumentException si las coordenadas son inválidas → 400
     * @throws RuntimeException si hay error en la búsqueda → 500
     */
    @Override
    @Transactional(readOnly = true)
    public Page<NearbyPlaceDto> getParkingNearVenue(String placeId, Pageable pageable) {
        log.info("Obteniendo estacionamientos cerca del venue: {} (page: {}, size: {})", 
                placeId, pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            // Obtener coordenadas del venue
            PlaceInfoResponse venueDetails = getVenueDetails(placeId);
            PlaceInfoResponse.PlaceDetail venue = getFirstPlaceDetail(venueDetails);
            validateGpsCoordinates(venue);
            
            // Buscar estacionamientos cercanos
            NearbySearchResponse serpResponse = serpApiService.searchNearbyParkings(
                venue.getGpsCoordinates().getLatitude(),
                venue.getGpsCoordinates().getLongitude(),
                2000
            );
            
            List<NearbyPlaceDto> allParkings = extractAllVenues(serpResponse);
            log.info("✅ Búsqueda de estacionamientos completada. Total resultados: {}", allParkings.size());
            
            return paginateList(allParkings, pageable);
            
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ Error obteniendo estacionamientos cerca del venue: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener estacionamientos: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtener transporte público cerca de un venue con paginación
     * Busca opciones de transporte público (metro, autobús, tren) cerca del venue
     * 
     * @param placeId Nombre o Place ID del venue
     * @param pageable Configuración de paginación
     * @return Página con opciones de transporte cercanas
     * @throws EntityNotFoundException (jakarta.persistence) si el venue no existe → 404
     * @throws IllegalArgumentException si las coordenadas son inválidas → 400
     * @throws RuntimeException si hay error en la búsqueda → 500
     */
    @Override
    @Transactional(readOnly = true)
    public Page<NearbyPlaceDto> getTransportNearVenue(String placeId, Pageable pageable) {
        log.info("Obteniendo transporte público cerca del venue: {} (page: {}, size: {})", 
                placeId, pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            // Obtener coordenadas del venue
            PlaceInfoResponse venueDetails = getVenueDetails(placeId);
            PlaceInfoResponse.PlaceDetail venue = getFirstPlaceDetail(venueDetails);
            validateGpsCoordinates(venue);
            
            // Buscar transporte público cercano
            NearbySearchResponse serpResponse = serpApiService.searchNearbyTransport(
                venue.getGpsCoordinates().getLatitude(),
                venue.getGpsCoordinates().getLongitude()
            );
            
            List<NearbyPlaceDto> allTransport = extractAllVenues(serpResponse);
            log.info("✅ Búsqueda de transporte público completada. Total resultados: {}", allTransport.size());
            
            return paginateList(allTransport, pageable);
            
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ Error obteniendo transporte cerca del venue: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener transporte: " + e.getMessage(), e);
        }
    }
    
    // ====== MÉTODOS AUXILIARES PRIVADOS ======
    
    /**
     * Extrae todos los venues de una respuesta de SerpApi
     * Maneja tanto place_results (resultado único) como local_results (lista)
     * 
     * @param response Respuesta de SerpApi
     * @return Lista de venues encontrados
     */
    private List<NearbyPlaceDto> extractAllVenues(NearbySearchResponse response) {
        if (response == null) {
            return Collections.emptyList();
        }
        
        // Caso 1: Resultado único directo
        if (response.getPlaceResults() != null) {
            return List.of(response.getPlaceResults());
        }
        
        // Caso 2: Lista de resultados
        if (response.getLocalResults() != null) {
            return response.getLocalResults();
        }
        
        return Collections.emptyList();
    }
    
    /**
     * Aplica paginación manual a una lista
     * Similar al patrón usado en UserPreferenceController.getAllGenres()
     * 
     * @param allItems Lista completa de items
     * @param pageable Configuración de paginación
     * @return Página con items paginados
     */
    private <T> Page<T> paginateList(List<T> allItems, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allItems.size());
        
        // Manejar caso donde start está más allá del tamaño de la lista
        if (start >= allItems.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, allItems.size());
        }
        
        List<T> pageContent = allItems.subList(start, end);
        return new PageImpl<>(pageContent, pageable, allItems.size());
    }
    
    /**
     * Obtiene el primer PlaceDetail de una respuesta o lanza excepción
     * 
     * @param venueDetails Respuesta con detalles del venue
     * @return Primer PlaceDetail encontrado
     * @throws IllegalArgumentException si no hay resultados
     */
    private PlaceInfoResponse.PlaceDetail getFirstPlaceDetail(PlaceInfoResponse venueDetails) {
        if (venueDetails.getLocalResults() == null || venueDetails.getLocalResults().isEmpty()) {
            throw new IllegalArgumentException("No se pudieron obtener las coordenadas del venue");
        }
        return venueDetails.getLocalResults().get(0);
    }
    
    /**
     * Valida que el venue tenga coordenadas GPS válidas
     * 
     * @param venue Detalles del venue a validar
     * @throws IllegalArgumentException si las coordenadas son nulas
     */
    private void validateGpsCoordinates(PlaceInfoResponse.PlaceDetail venue) {
        if (venue.getGpsCoordinates() == null) {
            throw new IllegalArgumentException("El venue no tiene coordenadas GPS");
        }
    }
}