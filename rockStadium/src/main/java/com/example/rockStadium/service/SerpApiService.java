package com.example.rockStadium.service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.rockStadium.config.SerpApiConfig;
import com.example.rockStadium.dto.NearbySearchResponse;
import com.example.rockStadium.dto.PlaceInfoResponse;
import com.example.rockStadium.dto.WeatherResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

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
    private final OkHttpClient httpClient = new OkHttpClient();
    
    // Gson con deserializador personalizado para manejar types flexible
    private final Gson gson = new GsonBuilder()
            .setLenient()
            .registerTypeAdapter(new TypeToken<List<String>>(){}.getType(), 
                (JsonDeserializer<List<String>>) (json, typeOfT, context) -> {
                    List<String> list = new ArrayList<>();
                    if (json.isJsonArray()) {
                        // Si es un array, procesar cada elemento
                        json.getAsJsonArray().forEach(e -> list.add(e.getAsString()));
                    } else if (json.isJsonPrimitive()) {
                        // Si es un string, agregarlo como único elemento
                        list.add(json.getAsString());
                    }
                    return list;
                })
            .create();
    
    /**
     * Ejecuta una petición HTTP y parsea la respuesta (versión genérica con Type)
     */
    private <T> T executeRequest(String url, Type responseType) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body() != null ? 
                    response.body().string() : "";
            
            if (!response.isSuccessful()) {
                log.error("❌ Error en la petición: {}. Response: {}", response.code(), responseBody);
                throw new IOException("Error en la petición: " + response.code());
            }
            
            // LOG TEMPORAL: Ver qué devuelve realmente SerpApi
            log.info("=== RESPUESTA CRUDA DE SERPAPI ===");
            log.info(responseBody);
            log.info("=== FIN RESPUESTA ===");
            
            // Intentar parsear la respuesta
            try {
                T result = gson.fromJson(responseBody, responseType);
                
                if (result == null) {
                    log.error("❌ La respuesta de SerpAPI fue null después de parsear");
                    log.error("Response body era: {}", responseBody);
                    throw new IOException("Respuesta null de SerpAPI");
                }
                
                return result;
            } catch (JsonSyntaxException e) {
                log.error("❌ Error parseando JSON de SerpAPI: {}", e.getMessage());
                log.error("JSON problemático: {}", responseBody);
                throw new IOException("Error parseando respuesta de SerpAPI: " + e.getMessage(), e);
            }
        }
    }
    
    /**
     * Ejecuta una petición HTTP y parsea la respuesta (versión con Class)
     * Sobrecarga para facilitar el uso con clases específicas
     */
    private <T> T executeRequest(String url, Class<T> responseClass) throws IOException {
        return executeRequest(url, (Type) responseClass);
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
            
            log.info("🔍 Buscando venues con query: {}", query);
            log.info("🌐 URL (sin API key): {}", url.replace(config.getApiKey(), "***"));
            
            return executeRequest(url, NearbySearchResponse.class);
        } catch (Exception e) {
            log.error("❌ Error buscando venues: {}", e.getMessage(), e);
            throw new RuntimeException("Error al buscar venues: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene detalles de un lugar por Place ID
     */
    public PlaceInfoResponse getPlaceDetailsByPlaceId(String placeId) {
        try {
            String url = String.format(
                "%s?engine=google_maps&type=place&data_id=%s&api_key=%s",
                config.getBaseUrl(),
                placeId,
                config.getApiKey()
            );
            
            log.info("🔍 Obteniendo detalles del lugar con Place ID: {}", placeId);
            log.info("🌐 URL: {}", url.replace(config.getApiKey(), "***"));
            
            PlaceInfoResponse response = executeRequest(url, PlaceInfoResponse.class);
            
            // Validación de respuesta
            if (response == null || response.getLocalResults() == null || response.getLocalResults().isEmpty()) {
                log.warn("⚠️  No se obtuvieron detalles del lugar. Respuesta vacía.");
            } else {
                log.info("✅ Detalles obtenidos: {}", response.getLocalResults().get(0).getTitle());
            }
            
            return response;
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo detalles del lugar: {}", e.getMessage(), e);
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
            
            log.info("🔍 Buscando lugares: {} en {},{}", placeType, latitude, longitude);
            return executeRequest(url, NearbySearchResponse.class);
        } catch (Exception e) {
            log.error("❌ Error buscando lugares cercanos: {}", e.getMessage());
            throw new RuntimeException("Error al buscar lugares cercanos", e);
        }
    }
    
    /**
     * Busca hoteles cercanos
     */
    public NearbySearchResponse searchNearbyHotels(BigDecimal latitude, BigDecimal longitude, Integer radius) {
        try {
            String query = String.format("hotels near %s,%s", latitude.toString(), longitude.toString());
            String url = String.format(
                "%s?engine=google_maps&type=search&q=%s&api_key=%s",
                config.getBaseUrl(),
                query.replace(" ", "+"),
                config.getApiKey()
            );
            
            log.info("🏨 Buscando hoteles en: {},{}", latitude, longitude);
            return executeRequest(url, NearbySearchResponse.class);
        } catch (Exception e) {
            log.error("❌ Error buscando hoteles: {}", e.getMessage());
            throw new RuntimeException("Error al buscar hoteles", e);
        }
    }
    
    /**
     * Busca restaurantes cercanos
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
            
            log.info("🍽️  Buscando restaurantes en: {},{}", latitude, longitude);
            return executeRequest(url, NearbySearchResponse.class);
        } catch (Exception e) {
            log.error("❌ Error buscando restaurantes: {}", e.getMessage());
            throw new RuntimeException("Error al buscar restaurantes", e);
        }
    }
    
    /**
     * Busca estacionamientos cercanos
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
            
            log.info("🅿️  Buscando estacionamientos en: {},{}", latitude, longitude);
            return executeRequest(url, NearbySearchResponse.class);
        } catch (Exception e) {
            log.error("❌ Error buscando estacionamientos: {}", e.getMessage());
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
            
            log.info("🚇 Buscando transporte público en: {},{}", latitude, longitude);
            return executeRequest(url, NearbySearchResponse.class);
        } catch (Exception e) {
            log.error("❌ Error buscando transporte: {}", e.getMessage());
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
            
            log.info("🔍 Obteniendo detalles de: {}", placeName);
            return executeRequest(url, PlaceInfoResponse.class);
        } catch (Exception e) {
            log.error("❌ Error obteniendo detalles del lugar: {}", e.getMessage());
            throw new RuntimeException("Error al obtener detalles del lugar", e);
        }
    }
    
    /**
     * Obtiene el clima para una ubicación usando Google Search
     */
    public WeatherResponse getWeatherByLocation(String location) {
        try {
            String url = String.format(
                "%s?engine=google&q=weather+%s&api_key=%s",
                config.getBaseUrl(),
                location.replace(" ", "+"),
                config.getApiKey()
            );
            
            log.info("🌤️  Obteniendo clima para: {}", location);
            log.info("🌐 URL (sin API key): {}", url.replace(config.getApiKey(), "***"));
            
            WeatherResponse response = executeRequest(url, WeatherResponse.class);
            
            // Validar respuesta
            if (response != null && response.getAnswerBox() != null) {
                log.info("✅ Clima obtenido: {} - {}", 
                    response.getAnswerBox().getLocation(), 
                    response.getAnswerBox().getWeather());
            } else {
                log.warn("⚠️  Respuesta de clima vacía para: {}", location);
            }
            
            return response;
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo clima para {}: {}", location, e.getMessage());
            // No lanzar excepción, solo retornar null para que el flujo continúe
            return null;
        }
    }
    
    /**
     * Busca información de un recinto/venue
     */
    public PlaceInfoResponse searchVenueInfo(String venueName, String location) {
        try {
            String query = venueName + " " + location;
            String url = String.format(
                "%s?engine=google_maps&type=search&q=%s&api_key=%s",
                config.getBaseUrl(),
                query.replace(" ", "+"),
                config.getApiKey()
            );
            
            log.info("🎪 Buscando información del venue: {}", venueName);
            return executeRequest(url, PlaceInfoResponse.class);
        } catch (Exception e) {
            log.error("❌ Error buscando información del venue: {}", e.getMessage());
            throw new RuntimeException("Error al buscar información del venue", e);
        }
    }
}