package com.example.rockStadium.service;

import com.example.rockStadium.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que orquesta la obtención de información completa del artista
 * combinando Spotify API + Ticketmaster API + SerpApi (Google Maps)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ArtistEventService {
    
    private final SpotifyService spotifyService;
    private final TicketmasterService ticketmasterService;
    private final SerpApiService serpApiService;
    
    /**
     * Funcionalidad 45 + 46: Obtener información completa del artista
     * Incluye: datos del artista, eventos REALES, venues, clima, hoteles, transporte
     */
    public ArtistCompleteInfoResponse getArtistCompleteInfo(String spotifyId) {
        log.info("===== OBTENIENDO INFORMACIÓN COMPLETA DEL ARTISTA: {} =====", spotifyId);
        
        try {
            // 1. Obtener información del artista desde Spotify
            ArtistResponse artist = spotifyService.getArtistById(spotifyId);
            log.info("✅ Artista obtenido de Spotify: {}", artist.getName());
            
            // 2. Buscar eventos REALES del artista en Ticketmaster
            log.info("🎫 Buscando eventos en Ticketmaster...");
            TicketmasterEventResponse ticketmasterEvents = ticketmasterService.searchEventsByArtist(artist.getName());
            
            // 3. Procesar eventos encontrados
            List<ArtistEventInfo> events = processTicketmasterEvents(ticketmasterEvents, artist.getName());
            
            // 4. Construir respuesta
            return ArtistCompleteInfoResponse.builder()
                    .artist(artist)
                    .upcomingEvents(events)
                    .totalEventsFound(events.size())
                    .searchedQuery("Ticketmaster: " + artist.getName())
                    .message(events.isEmpty() 
                        ? "No se encontraron eventos próximos para este artista en Ticketmaster" 
                        : String.format("✅ Encontrados %d eventos confirmados con información completa", events.size()))
                    .build();
                    
        } catch (Exception e) {
            log.error("❌ Error obteniendo información completa del artista: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener información del artista: " + e.getMessage(), e);
        }
    }
    
    /**
     * Procesa los eventos de Ticketmaster y enriquece con clima, hoteles y transporte
     */
    private List<ArtistEventInfo> processTicketmasterEvents(TicketmasterEventResponse response, String artistName) {
        if (response.getEmbedded() == null || response.getEmbedded().getEvents() == null) {
            log.warn("⚠️  No se encontraron eventos en Ticketmaster");
            return new ArrayList<>();
        }
        
        List<TicketmasterEventResponse.Event> events = response.getEmbedded().getEvents();
        log.info("📍 Procesando {} eventos de Ticketmaster", events.size());
        
        // Limitar a los primeros 5 eventos para no saturar las APIs
        return events.stream()
                .limit(5)
                .map(event -> enrichTicketmasterEvent(event, artistName))
                .filter(event -> event != null) // Filtrar eventos nulos (errores)
                .collect(Collectors.toList());
    }
    
    /**
     * Enriquece un evento de Ticketmaster con información adicional (clima, hoteles, transporte)
     */
    private ArtistEventInfo enrichTicketmasterEvent(TicketmasterEventResponse.Event event, String artistName) {
        try {
            log.info("🎵 Procesando evento: {}", event.getName());
            
            // Extraer información del venue
            TicketmasterEventResponse.Venue ticketmasterVenue = null;
            if (event.getEmbedded() != null && event.getEmbedded().getVenues() != null 
                    && !event.getEmbedded().getVenues().isEmpty()) {
                ticketmasterVenue = event.getEmbedded().getVenues().get(0);
            }
            
            if (ticketmasterVenue == null) {
                log.warn("⚠️  Evento sin venue, saltando: {}", event.getName());
                return null;
            }
            
            // Construir información del venue
            ArtistEventInfo.VenueInfo venueInfo = buildVenueInfoFromTicketmaster(ticketmasterVenue);
            
            // Verificar que tengamos coordenadas
            if (ticketmasterVenue.getLocation() == null || 
                    ticketmasterVenue.getLocation().getLatitude() == null) {
                log.warn("⚠️  Venue sin coordenadas GPS, saltando: {}", ticketmasterVenue.getName());
                return null;
            }
            
            BigDecimal lat = new BigDecimal(ticketmasterVenue.getLocation().getLatitude());
            BigDecimal lng = new BigDecimal(ticketmasterVenue.getLocation().getLongitude());
            
            // Obtener información adicional del lugar
            log.info("🌤️  Obteniendo clima, hoteles y transporte...");
            
            // Clima
            String locationQuery = String.format("%s, %s", 
                ticketmasterVenue.getCity() != null ? ticketmasterVenue.getCity().getName() : "",
                ticketmasterVenue.getCountry() != null ? ticketmasterVenue.getCountry().getName() : ""
            );
            WeatherResponse weather = getWeatherForLocation(locationQuery);
            
            // Hoteles cercanos (top 5)
            List<NearbyPlaceDto> hotels = getNearbyHotels(lat, lng);
            
            // Transporte cercano
            List<NearbyPlaceDto> transport = getNearbyTransport(lat, lng);
            
            // Construir fechas y precios
            String eventDate = buildEventDate(event);
            String ticketPrice = buildTicketPrice(event);
            
            // Construir el evento completo
            return ArtistEventInfo.builder()
                    .eventName(event.getName())
                    .eventDate(eventDate)
                    .eventType(event.getType() != null ? event.getType() : "Concert")
                    .ticketUrl(event.getUrl())
                    .ticketPrice(ticketPrice)
                    .status(event.getDates() != null && event.getDates().getStatus() != null 
                            ? event.getDates().getStatus().getCode() : "unknown")
                    .venue(venueInfo)
                    .weather(weather)
                    .nearbyHotels(hotels)
                    .nearbyTransport(transport)
                    .build();
                    
        } catch (Exception e) {
            log.error("❌ Error procesando evento {}: {}", event.getName(), e.getMessage());
            return null;
        }
    }
    
    /**
     * Construye información del venue desde Ticketmaster
     */
    private ArtistEventInfo.VenueInfo buildVenueInfoFromTicketmaster(TicketmasterEventResponse.Venue venue) {
        String fullAddress = String.format("%s, %s, %s",
            venue.getAddress() != null ? venue.getAddress().getLine1() : "",
            venue.getCity() != null ? venue.getCity().getName() : "",
            venue.getCountry() != null ? venue.getCountry().getName() : ""
        );
        
        return ArtistEventInfo.VenueInfo.builder()
                .name(venue.getName())
                .address(fullAddress)
                .latitude(venue.getLocation() != null ? new BigDecimal(venue.getLocation().getLatitude()) : null)
                .longitude(venue.getLocation() != null ? new BigDecimal(venue.getLocation().getLongitude()) : null)
                .placeId(venue.getId())
                .parkingInfo(venue.getParkingDetail())
                .accessibilityInfo(venue.getAccessibleSeatingDetail())
                .timezone(venue.getTimezone())
                .build();
    }
    
    /**
     * Construye la fecha del evento en formato legible
     */
    private String buildEventDate(TicketmasterEventResponse.Event event) {
        if (event.getDates() == null || event.getDates().getStart() == null) {
            return "Fecha por confirmar";
        }
        
        TicketmasterEventResponse.Start start = event.getDates().getStart();
        String date = start.getLocalDate() != null ? start.getLocalDate() : "TBA";
        String time = start.getLocalTime() != null ? start.getLocalTime() : "";
        
        return date + (time.isEmpty() ? "" : " " + time);
    }
    
    /**
     * Construye el rango de precios del evento
     */
    private String buildTicketPrice(TicketmasterEventResponse.Event event) {
        if (event.getPriceRanges() == null || event.getPriceRanges().isEmpty()) {
            return "Precio por confirmar";
        }
        
        TicketmasterEventResponse.PriceRange priceRange = event.getPriceRanges().get(0);
        return String.format("$%.2f - $%.2f %s", 
            priceRange.getMin(), 
            priceRange.getMax(), 
            priceRange.getCurrency()
        );
    }
    
    /**
     * Obtiene el clima para una ubicación
     */
    private WeatherResponse getWeatherForLocation(String location) {
        try {
            if (location != null && !location.isEmpty()) {
                return serpApiService.getWeatherByLocation(location);
            }
        } catch (Exception e) {
            log.warn("⚠️  No se pudo obtener clima: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * Obtiene hoteles cercanos (top 5)
     */
    private List<NearbyPlaceDto> getNearbyHotels(BigDecimal lat, BigDecimal lng) {
        try {
            NearbySearchResponse hotels = serpApiService.searchNearbyHotels(lat, lng, 5000);
            if (hotels.getLocalResults() != null) {
                return hotels.getLocalResults().stream()
                        .limit(5)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.warn("⚠️  Error obteniendo hoteles: {}", e.getMessage());
        }
        return new ArrayList<>();
    }
    
    /**
     * Obtiene opciones de transporte cercano
     */
    private List<NearbyPlaceDto> getNearbyTransport(BigDecimal lat, BigDecimal lng) {
        try {
            NearbySearchResponse transport = serpApiService.searchNearbyTransport(lat, lng);
            if (transport.getLocalResults() != null) {
                return transport.getLocalResults().stream()
                        .limit(5)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.warn("⚠️  Error obteniendo transporte: {}", e.getMessage());
        }
        return new ArrayList<>();
    }
}