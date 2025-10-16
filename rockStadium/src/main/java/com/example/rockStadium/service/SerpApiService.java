package com.example.rockStadium.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.example.rockStadium.config.SerpApiConfig;
import com.example.rockStadium.dto.NearbySearchResponse;
import com.example.rockStadium.dto.PlaceInfoResponse;
import com.example.rockStadium.dto.WeatherResponse;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
@RequiredArgsConstructor
@Slf4j
public class SerpApiService {
    
    private final SerpApiConfig config;
    private final Gson gson = new Gson();
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    
    /**
     * Busca hoteles cercanos a una ubicación
     */
    public NearbySearchResponse searchNearbyHotels(BigDecimal latitude, BigDecimal longitude, Integer radius) {
        try {
            // Incluir coordenadas en la query para mejor precisión
            String query = String.format("hotels near %s,%s", latitude.toString(), longitude.toString());
            String url = String.format(
                "%s?engine=google_maps&type=search&q=%s&api_key=%s",
                config.getBaseUrl(),
                query.replace(" ", "+"),
                config.getApiKey()
            );
            
            log.info("URL de búsqueda de hoteles: {}", url.replace(config.getApiKey(), "***"));
            
            return executeRequest(url, NearbySearchResponse.class);
        } catch (Exception e) {
            log.error("Error buscando hoteles cercanos: {}", e.getMessage());
            throw new RuntimeException("Error al buscar hoteles", e);
        }
    }
    
    /**
     * Busca restaurantes cercanos a una ubicación
     */
    public NearbySearchResponse searchNearbyRestaurants(BigDecimal latitude, BigDecimal longitude, Integer radius) {
        try {
            String query = String.format("restaurants near %s,%s", latitude.toString(), longitude.toString());
            String url = String.format(
                "%s?engine=google_maps&type=search&q=%s&api_key=%s",
                config.getBaseUrl(),
                query.replace(" ", "+"),
                config.getApiKey()
            );
            
            log.info("Buscando restaurantes en: {},{}", latitude, longitude);
            return executeRequest(url, NearbySearchResponse.class);
        } catch (Exception e) {
            log.error("Error buscando restaurantes: {}", e.getMessage());
            throw new RuntimeException("Error al buscar restaurantes", e);
        }
    }
    
    /**
     * Busca estacionamientos cercanos a una ubicación
     */
    public NearbySearchResponse searchNearbyParkings(BigDecimal latitude, BigDecimal longitude, Integer radius) {
        try {
            String query = String.format("parking near %s,%s", latitude.toString(), longitude.toString());
            String url = String.format(
                "%s?engine=google_maps&type=search&q=%s&api_key=%s",
                config.getBaseUrl(),
                query.replace(" ", "+"),
                config.getApiKey()
            );
            
            log.info("Buscando estacionamientos en: {},{}", latitude, longitude);
            return executeRequest(url, NearbySearchResponse.class);
        } catch (Exception e) {
            log.error("Error buscando estacionamientos: {}", e.getMessage());
            throw new RuntimeException("Error al buscar estacionamientos", e);
        }
    }
    
    /**
     * Busca transporte público cercano
     */
    public NearbySearchResponse searchNearbyTransport(BigDecimal latitude, BigDecimal longitude) {
        try {
            String query = String.format("public transport near %s,%s", latitude.toString(), longitude.toString());
            String url = String.format(
                "%s?engine=google_maps&type=search&q=%s&api_key=%s",
                config.getBaseUrl(),
                query.replace(" ", "+"),
                config.getApiKey()
            );
            
            log.info("Buscando transporte público en: {},{}", latitude, longitude);
            return executeRequest(url, NearbySearchResponse.class);
        } catch (Exception e) {
            log.error("Error buscando transporte: {}", e.getMessage());
            throw new RuntimeException("Error al buscar transporte", e);
        }
    }
    
    /**
     * Obtiene detalles de un lugar específico
     */
    public PlaceInfoResponse getPlaceDetails(String placeName, String location) {
        try {
            String url = String.format(
                "%s?engine=google_maps&type=search&q=%s&ll=%s&api_key=%s",
                config.getBaseUrl(),
                placeName.replace(" ", "+"),
                location,
                config.getApiKey()
            );
            
            log.info("Obteniendo detalles de: {}", placeName);
            return executeRequest(url, PlaceInfoResponse.class);
        } catch (Exception e) {
            log.error("Error obteniendo detalles del lugar: {}", e.getMessage());
            throw new RuntimeException("Error al obtener detalles del lugar", e);
        }
    }
    
    /**
     * Busca información de un recinto/venue
     */
    public PlaceInfoResponse searchVenueInfo(String venueName, String city) {
        try {
            String query = String.format("%s %s", venueName, city);
            String url = String.format(
                "%s?engine=google_maps&type=search&q=%s&api_key=%s",
                config.getBaseUrl(),
                query.replace(" ", "+"),
                config.getApiKey()
            );
            
            log.info("Buscando información del venue: {}", query);
            return executeRequest(url, PlaceInfoResponse.class);
        } catch (Exception e) {
            log.error("Error buscando información del recinto: {}", e.getMessage());
            throw new RuntimeException("Error al buscar información del recinto", e);
        }
    }
   /**
 * Obtiene el clima para una ubicación usando Google Weather
 */
public WeatherResponse getWeatherByLocation(String location) {
    try {
        String url = String.format(
            "%s?engine=google&q=weather+%s&api_key=%s",
            config.getBaseUrl(),
            location.replace(" ", "+"),
            config.getApiKey()
        );
        
        log.info("Obteniendo clima para: {}", location);
        return executeRequest(url, WeatherResponse.class);
    } catch (Exception e) {
        log.error("Error obteniendo clima: {}", e.getMessage());
        throw new RuntimeException("Error al obtener clima", e);
    }
}
    /**
     * Ejecuta una petición HTTP y parsea la respuesta
     */
    private <T> T executeRequest(String url, Class<T> responseType) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Accept", "application/json")
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            
            if (!response.isSuccessful()) {
                log.error("Error en la petición: {}. Response: {}", response.code(), responseBody);
                throw new IOException("Error en la petición: " + response.code());
            }
            
            // LOG TEMPORAL: Ver qué devuelve realmente SerpApi
            log.info("=== RESPUESTA CRUDA DE SERPAPI ===");
            log.info(responseBody);
            log.info("=== FIN RESPUESTA ===");
            
            return gson.fromJson(responseBody, responseType);
        }
    }
    
    /**
     * Busca venues por query general
     */
    public NearbySearchResponse searchVenuesByQuery(String query) {
        try {
            String url = String.format(
                "%s?engine=google_maps&type=search&q=%s&api_key=%s",
                config.getBaseUrl(),
                query.replace(" ", "+"),
                config.getApiKey()
            );
            
            log.info("Buscando venues con query: {}", query);
            return executeRequest(url, NearbySearchResponse.class);
        } catch (Exception e) {
            log.error("Error buscando venues: {}", e.getMessage());
            throw new RuntimeException("Error al buscar venues", e);
        }
    }
    
    /**
 * Obtiene detalles de un lugar por Place ID
 * CORREGIDO: Usa el engine correcto para detalles
 */
public PlaceInfoResponse getPlaceDetailsByPlaceId(String placeId) {
    try {
        // IMPORTANTE: Para obtener detalles completos, usa el engine "google_maps"
        // con el parámetro "data_id" o "place_id"
        String url = String.format(
            "%s?engine=google_maps&type=place&data_id=%s&api_key=%s",
            config.getBaseUrl(),
            placeId,
            config.getApiKey()
        );
        
        log.info("Obteniendo detalles del lugar con Place ID: {}", placeId);
        log.info("URL: {}", url.replace(config.getApiKey(), "***"));
        
        PlaceInfoResponse response = executeRequest(url, PlaceInfoResponse.class);
        
        // Log de la respuesta para debugging
        if (response == null || response.getLocalResults() == null || response.getLocalResults().isEmpty()) {
            log.warn("⚠️  No se obtuvieron detalles del lugar. Respuesta vacía.");
        } else {
            log.info("✅ Detalles obtenidos: {}", response.getLocalResults().get(0).getTitle());
        }
        
        return response;
        
    } catch (Exception e) {
        log.error("Error obteniendo detalles del lugar: {}", e.getMessage(), e);
        throw new RuntimeException("Error al obtener detalles del lugar", e);
    }
}
    /**
     * Busca lugares por tipo (genérico)
     */
    public NearbySearchResponse searchNearbyPlaces(
            BigDecimal latitude, 
            BigDecimal longitude, 
            String placeType, 
            Integer radius) {
        try {
            String query = String.format("%s near %s,%s", placeType, latitude.toString(), longitude.toString());
            String url = String.format(
                "%s?engine=google_maps&type=search&q=%s&api_key=%s",
                config.getBaseUrl(),
                query.replace(" ", "+"),
                config.getApiKey()
            );
            
            log.info("Buscando lugares: {} en {},{}", placeType, latitude, longitude);
            return executeRequest(url, NearbySearchResponse.class);
        } catch (Exception e) {
            log.error("Error buscando lugares cercanos: {}", e.getMessage());
            throw new RuntimeException("Error al buscar lugares cercanos", e);
        }
    }
}