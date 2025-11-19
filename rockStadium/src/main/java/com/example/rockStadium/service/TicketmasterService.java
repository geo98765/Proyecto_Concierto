package com.example.rockstadium.service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.example.rockstadium.config.TicketmasterConfig;
import com.example.rockstadium.dto.TicketmasterEventResponse;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Servicio para interactuar con Ticketmaster Discovery API
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TicketmasterService {
    
    private final TicketmasterConfig config;
    private final Gson gson = new Gson();
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    
    /**
     * Buscar eventos por nombre de artista
     */
    public TicketmasterEventResponse searchEventsByArtist(String artistName) {
        try {
            String url = String.format(
                "%s?keyword=%s&classificationName=music&apikey=%s&size=20&sort=date,asc",
                config.getEventsUrl(),
                artistName.replace(" ", "%20"),
                config.getApiKey()
            );
            
            log.info("🎫 Buscando eventos en Ticketmaster para: {}", artistName);
            log.info("URL: {}", url.replace(config.getApiKey(), "***"));
            
            return executeRequest(url);
            
        } catch (Exception e) {
            log.error("❌ Error buscando eventos en Ticketmaster: {}", e.getMessage(), e);
            throw new RuntimeException("Error al buscar eventos: " + e.getMessage(), e);
        }
    }
    
    /**
     * Buscar eventos por nombre de artista en un país específico
     */
    public TicketmasterEventResponse searchEventsByArtistAndCountry(String artistName, String countryCode) {
        try {
            String url = String.format(
                "%s?keyword=%s&classificationName=music&countryCode=%s&apikey=%s&size=20&sort=date,asc",
                config.getEventsUrl(),
                artistName.replace(" ", "%20"),
                countryCode,
                config.getApiKey()
            );
            
            log.info("🎫 Buscando eventos en Ticketmaster para: {} en {}", artistName, countryCode);
            
            return executeRequest(url);
            
        } catch (Exception e) {
            log.error("❌ Error buscando eventos: {}", e.getMessage());
            throw new RuntimeException("Error al buscar eventos: " + e.getMessage(), e);
        }
    }
    
    /**
     * Buscar eventos por ID de venue
     */
    public TicketmasterEventResponse searchEventsByVenue(String venueId) {
        try {
            String url = String.format(
                "%s?venueId=%s&apikey=%s&size=20&sort=date,asc",
                config.getEventsUrl(),
                venueId,
                config.getApiKey()
            );
            
            log.info("🎫 Buscando eventos en venue: {}", venueId);
            
            return executeRequest(url);
            
        } catch (Exception e) {
            log.error("❌ Error buscando eventos por venue: {}", e.getMessage());
            throw new RuntimeException("Error al buscar eventos: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtener evento por ID
     */
    public TicketmasterEventResponse.Event getEventById(String eventId) {
        try {
            String url = String.format(
                "%s/%s.json?apikey=%s",
                config.getBaseUrl() + "/events",
                eventId,
                config.getApiKey()
            );
            
            log.info("🎫 Obteniendo detalles del evento: {}", eventId);
            
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            
            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";
                
                if (!response.isSuccessful()) {
                    log.error("Error en la petición: {}. Response: {}", response.code(), responseBody);
                    throw new IOException("Error en la petición: " + response.code());
                }
                
                return gson.fromJson(responseBody, TicketmasterEventResponse.Event.class);
            }
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo evento: {}", e.getMessage());
            throw new RuntimeException("Error al obtener evento: " + e.getMessage(), e);
        }
    }
    
    /**
     * Ejecuta una petición HTTP y parsea la respuesta
     */
    private TicketmasterEventResponse executeRequest(String url) throws IOException {
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
            
            // Log de respuesta para debug
            log.debug("=== RESPUESTA DE TICKETMASTER ===");
            log.debug(responseBody);
            log.debug("=== FIN RESPUESTA ===");
            
            TicketmasterEventResponse eventResponse = gson.fromJson(responseBody, TicketmasterEventResponse.class);
            
            if (eventResponse.getEmbedded() != null && eventResponse.getEmbedded().getEvents() != null) {
                log.info("✅ Encontrados {} eventos", eventResponse.getEmbedded().getEvents().size());
            } else {
                log.warn("⚠️  No se encontraron eventos");
            }
            
            return eventResponse;
        }
    }
}