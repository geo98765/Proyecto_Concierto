package com.example.rockStadium.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.rockStadium.dto.NearbyPlaceDto;
import com.example.rockStadium.dto.NearbySearchResponse;
import com.example.rockStadium.dto.PlaceInfoResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VenueServiceImpl implements VenueService {
    
    private final SerpApiService serpApiService;
    
    // ====== BÚSQUEDA DE VENUES ======
    
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
    public PlaceInfoResponse getVenueDetails(String query) {
        log.info("Obteniendo detalles del venue: {}", query);
        
        try {
            // Buscar el venue por nombre/query
            NearbySearchResponse searchResponse = serpApiService.searchVenuesByQuery(query);
            
            // Validar que hay resultados
            if (searchResponse == null || searchResponse.getLocalResults() == null 
                    || searchResponse.getLocalResults().isEmpty()) {
                log.warn("⚠️  No se encontró información para: {}", query);
                throw new RuntimeException("No se encontró el venue: " + query);
            }
            
            // Tomar el primer resultado (el más relevante)
            NearbyPlaceDto venue = searchResponse.getLocalResults().get(0);
            log.info("✅ Venue encontrado: {}", venue.getTitle());
            
            // Convertir NearbyPlaceDto a PlaceInfoResponse.PlaceDetail
            PlaceInfoResponse.PlaceDetail detail = PlaceInfoResponse.PlaceDetail.builder()
                    .title(venue.getTitle())
                    .placeId(venue.getPlaceId())
                    .address(venue.getAddress())
                    .rating(venue.getRating())
                    .reviews(venue.getReviews())
                    .phone(venue.getPhone())
                    .phoneNumber(venue.getPhone())
                    .website(venue.getWebsite())
                    .description(venue.getDescription())
                    .types(venue.getTypes())
                    .typeId(venue.getTypeId())
                    .price(venue.getPrice())
                    .priceLevel(venue.getPrice())
                    .gpsCoordinates(venue.getGpsCoordinates() != null 
                        ? PlaceInfoResponse.GpsCoordinates.builder()
                            .latitude(venue.getGpsCoordinates().getLatitude())
                            .longitude(venue.getGpsCoordinates().getLongitude())
                            .build()
                        : null)
                    .build();
            
            // Construir respuesta final
            PlaceInfoResponse response = PlaceInfoResponse.builder()
                    .localResults(List.of(detail))
                    .searchMetadata(PlaceInfoResponse.SearchMetadata.builder()
                            .status("Success")
                            .build())
                    .build();
            
            log.info("✅ Detalles completos del venue obtenidos: {}", venue.getTitle());
            return response;
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo detalles del venue: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener detalles del venue: " + e.getMessage(), e);
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
    
    // ====== SERVICIOS CERCANOS AL VENUE ======
    
    @Override
    public NearbySearchResponse getHotelsNearVenue(String placeId, Integer radius) {
        log.info("Obteniendo hoteles cerca del venue: {}", placeId);
        try {
            // Primero obtenemos los detalles del venue para obtener sus coordenadas
            PlaceInfoResponse venueDetails = getVenueDetailsByPlaceId(placeId);
            
            if (venueDetails.getLocalResults() == null || venueDetails.getLocalResults().isEmpty()) {
                throw new RuntimeException("No se pudieron obtener las coordenadas del venue");
            }
            
            PlaceInfoResponse.PlaceDetail venue = venueDetails.getLocalResults().get(0);
            
            if (venue.getGpsCoordinates() == null) {
                throw new RuntimeException("El venue no tiene coordenadas GPS");
            }
            
            return serpApiService.searchNearbyHotels(
                venue.getGpsCoordinates().getLatitude(),
                venue.getGpsCoordinates().getLongitude(),
                radius
            );
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
            
            if (venueDetails.getLocalResults() == null || venueDetails.getLocalResults().isEmpty()) {
                throw new RuntimeException("No se pudieron obtener las coordenadas del venue");
            }
            
            PlaceInfoResponse.PlaceDetail venue = venueDetails.getLocalResults().get(0);
            
            if (venue.getGpsCoordinates() == null) {
                throw new RuntimeException("El venue no tiene coordenadas GPS");
            }
            
            return serpApiService.searchNearbyRestaurants(
                venue.getGpsCoordinates().getLatitude(),
                venue.getGpsCoordinates().getLongitude(),
                radius
            );
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
            
            if (venueDetails.getLocalResults() == null || venueDetails.getLocalResults().isEmpty()) {
                throw new RuntimeException("No se pudieron obtener las coordenadas del venue");
            }
            
            PlaceInfoResponse.PlaceDetail venue = venueDetails.getLocalResults().get(0);
            
            if (venue.getGpsCoordinates() == null) {
                throw new RuntimeException("El venue no tiene coordenadas GPS");
            }
            
            return serpApiService.searchNearbyParkings(
                venue.getGpsCoordinates().getLatitude(),
                venue.getGpsCoordinates().getLongitude(),
                2000
            );
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
            
            if (venueDetails.getLocalResults() == null || venueDetails.getLocalResults().isEmpty()) {
                throw new RuntimeException("No se pudieron obtener las coordenadas del venue");
            }
            
            PlaceInfoResponse.PlaceDetail venue = venueDetails.getLocalResults().get(0);
            
            if (venue.getGpsCoordinates() == null) {
                throw new RuntimeException("El venue no tiene coordenadas GPS");
            }
            
            return serpApiService.searchNearbyTransport(
                venue.getGpsCoordinates().getLatitude(),
                venue.getGpsCoordinates().getLongitude()
            );
        } catch (Exception e) {
            log.error("Error obteniendo transporte cerca del venue: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener transporte", e);
        }
    }
    
    // ====== MÉTODO AUXILIAR PRIVADO ======
    
    /**
     * Método auxiliar para obtener detalles del venue por Place ID
     * Usado internamente para obtener coordenadas
     */
    private PlaceInfoResponse getVenueDetailsByPlaceId(String placeId) {
        // Buscar el venue por su Place ID en Google Maps
        // Como no podemos buscar directamente por Place ID en SerpApi,
        // asumimos que el placeId es en realidad el nombre del venue
        return getVenueDetails(placeId);
    }
}