package com.example.rockstadium.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.example.rockstadium.dto.*;

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
                        ? "No se encontraron eventos próximos para este artista" 
                        : String.format("Se encontraron %d eventos próximos", events.size()))
                    .build();
                    
        } catch (Exception e) {
            log.error("❌ Error obteniendo información del artista: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener información completa del artista", e);
        }
    }
    
    /**
     * Procesa los eventos de Ticketmaster y enriquece con información adicional
     */
    private List<ArtistEventInfo> processTicketmasterEvents(
            TicketmasterEventResponse ticketmasterResponse, 
            String artistName) {
        
        if (ticketmasterResponse == null || ticketmasterResponse.getEmbedded() == null 
                || ticketmasterResponse.getEmbedded().getEvents() == null) {
            log.info("⚠️  No se encontraron eventos en Ticketmaster para: {}", artistName);
            return new ArrayList<>();
        }
        
        List<TicketmasterEventResponse.Event> events = ticketmasterResponse.getEmbedded().getEvents();
        log.info("📋 Procesando {} eventos encontrados...", events.size());
        
        return events.stream()
                .map(this::enrichEventWithAdditionalInfo)
                .filter(event -> event != null) // Filtrar eventos que fallaron
                .collect(Collectors.toList());
    }
    
    /**
     * Enriquece un evento con información adicional (clima, hoteles, transporte)
     */
    private ArtistEventInfo enrichEventWithAdditionalInfo(TicketmasterEventResponse.Event event) {
        try {
            log.info("🎵 Procesando evento: {}", event.getName());
            
            // Obtener venue del evento
            TicketmasterEventResponse.Venue ticketmasterVenue = extractVenue(event);
            
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
            
            // Clima (con manejo de errores mejorado)
            String locationQuery = buildLocationQuery(ticketmasterVenue);
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
     * Extrae el venue del evento de Ticketmaster
     */
    private TicketmasterEventResponse.Venue extractVenue(TicketmasterEventResponse.Event event) {
        if (event.getEmbedded() == null || event.getEmbedded().getVenues() == null 
                || event.getEmbedded().getVenues().isEmpty()) {
            return null;
        }
        return event.getEmbedded().getVenues().get(0);
    }
    
    /**
     * Construye información del venue desde Ticketmaster
     */
    private ArtistEventInfo.VenueInfo buildVenueInfoFromTicketmaster(TicketmasterEventResponse.Venue venue) {
        String fullAddress = buildFullAddress(venue);
        
        return ArtistEventInfo.VenueInfo.builder()
                .name(venue.getName())
                .address(fullAddress)
                .latitude(venue.getLocation() != null && venue.getLocation().getLatitude() != null 
                        ? new BigDecimal(venue.getLocation().getLatitude()) : null)
                .longitude(venue.getLocation() != null && venue.getLocation().getLongitude() != null 
                        ? new BigDecimal(venue.getLocation().getLongitude()) : null)
                .placeId(null) // Ticketmaster no provee Place ID
                .rating(null)
                .reviews(null)
                .phone(null)
                .website(venue.getUrl())
                .parkingInfo(venue.getParkingDetail())
                .accessibilityInfo(venue.getAccessibleSeatingDetail())
                .timezone(venue.getTimezone())
                .build();
    }
    
    /**
     * Construye la dirección completa del venue
     */
    private String buildFullAddress(TicketmasterEventResponse.Venue venue) {
        StringBuilder address = new StringBuilder();
        
        if (venue.getAddress() != null && venue.getAddress().getLine1() != null) {
            address.append(venue.getAddress().getLine1());
        }
        
        if (venue.getCity() != null && venue.getCity().getName() != null) {
            if (address.length() > 0) address.append(", ");
            address.append(venue.getCity().getName());
        }
        
        if (venue.getState() != null && venue.getState().getName() != null) {
            if (address.length() > 0) address.append(", ");
            address.append(venue.getState().getName());
        }
        
        if (venue.getCountry() != null && venue.getCountry().getName() != null) {
            if (address.length() > 0) address.append(", ");
            address.append(venue.getCountry().getName());
        }
        
        return address.toString();
    }
    
    /**
     * Construye el query de ubicación para la búsqueda de clima
     */
    private String buildLocationQuery(TicketmasterEventResponse.Venue venue) {
        StringBuilder location = new StringBuilder();
        
        if (venue.getCity() != null && venue.getCity().getName() != null) {
            location.append(venue.getCity().getName());
        }
        
        if (venue.getState() != null && venue.getState().getStateCode() != null) {
            if (location.length() > 0) location.append(" ");
            location.append(venue.getState().getStateCode());
        }
        
        if (venue.getCountry() != null && venue.getCountry().getCountryCode() != null) {
            if (location.length() > 0) location.append(" ");
            location.append(venue.getCountry().getCountryCode());
        }
        
        return location.toString();
    }
    
    /**
     * Construye la fecha del evento
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
     * CORREGIDO: Ahora maneja correctamente los errores
     */
    private WeatherResponse getWeatherForLocation(String location) {
        try {
            if (location == null || location.trim().isEmpty()) {
                log.warn("⚠️  Ubicación vacía, no se puede obtener clima");
                return null;
            }
            
            log.info("🌤️  Buscando clima para: {}", location);
            return serpApiService.getWeatherByLocation(location);
            
        } catch (Exception e) {
            log.warn("⚠️  No se pudo obtener clima para {}: {}", location, e.getMessage());
            return null; // Retornar null en lugar de lanzar excepción
        }
    }
    
    /**
     * Obtiene hoteles cercanos (top 5)
     */
    private List<NearbyPlaceDto> getNearbyHotels(BigDecimal lat, BigDecimal lng) {
        try {
            log.info("🏨 Buscando hoteles cercanos...");
            NearbySearchResponse hotels = serpApiService.searchNearbyHotels(lat, lng, 5000);
            
            if (hotels != null && hotels.getLocalResults() != null) {
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
            log.info("🚇 Buscando transporte cercano...");
            NearbySearchResponse transport = serpApiService.searchNearbyTransport(lat, lng);
            
            if (transport != null && transport.getLocalResults() != null) {
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