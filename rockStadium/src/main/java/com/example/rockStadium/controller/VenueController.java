package com.example.rockStadium.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.rockStadium.dto.NearbySearchResponse;
import com.example.rockStadium.dto.PlaceInfoResponse;
import com.example.rockStadium.service.VenueService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
@Tag(name = "Venues (Recintos)", description = "Búsqueda de recintos de conciertos y servicios cercanos")
public class VenueController {
    
    private final VenueService venueService;
    
    // ===== BÚSQUEDA DE VENUES =====
    
    @Operation(
        summary = "Buscar venues en Google Maps",
        description = "Busca recintos de conciertos directamente en Google Maps usando una query de texto.\n\n" +
                "**Ejemplos de búsquedas:**\n" +
                "- 'Foro Sol Ciudad de México'\n" +
                "- 'Madison Square Garden'\n" +
                "- 'Estadio Azteca'"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Venues encontrados",
            content = @Content(schema = @Schema(implementation = NearbySearchResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Query inválido"),
        @ApiResponse(responseCode = "500", description = "Error al consultar Google Maps")
    })
    @GetMapping("/search-maps")
    public ResponseEntity<NearbySearchResponse> searchVenuesInGoogleMaps(
            @Parameter(
                description = "Nombre del venue o query de búsqueda",
                example = "Foro Sol Ciudad Mexico",
                required = true
            )
            @RequestParam String query) {
        NearbySearchResponse response = venueService.searchVenuesInGoogleMaps(query);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Buscar venues por ubicación",
        description = "Busca venues cerca de coordenadas específicas.\n\n" +
                "Útil para encontrar recintos cerca de la ubicación del usuario."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Venues encontrados"),
        @ApiResponse(responseCode = "400", description = "Coordenadas inválidas")
    })
    @GetMapping("/search-maps-by-location")
    public ResponseEntity<NearbySearchResponse> searchVenuesByLocation(
            @Parameter(description = "Latitud", example = "19.4326", required = true)
            @RequestParam Double lat,
            @Parameter(description = "Longitud", example = "-99.1332", required = true)
            @RequestParam Double lng,
            @Parameter(description = "Tipo de lugar a buscar", example = "concert venue")
            @RequestParam(defaultValue = "concert venue") String query) {
        NearbySearchResponse response = venueService.searchVenuesByLocation(lat, lng, query);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Obtener detalles completos de un venue",
        description = "Retorna información completa de un venue buscando por nombre.\n\n" +
                "**ACTUALIZADO:** Ahora busca por nombre en lugar de Place ID para obtener mejores resultados.\n\n" +
                "**Incluye:** dirección, coordenadas, reseñas, calificación, teléfono, sitio web, tipo de lugar.\n\n" +
                "**Ejemplo de uso:**\n" +
                "1. Primero busca venues con `/search-maps?query=Foro Sol`\n" +
                "2. Copia el nombre exacto del resultado\n" +
                "3. Usa ese nombre en este endpoint para obtener detalles completos"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Detalles del venue obtenidos",
            content = @Content(schema = @Schema(implementation = PlaceInfoResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Venue no encontrado")
    })
    @GetMapping("/details")
    public ResponseEntity<PlaceInfoResponse> getVenueDetails(
            @Parameter(
                description = "Nombre completo del venue o query de búsqueda",
                example = "Lefrak Concert Hall New York",
                required = true
            )
            @RequestParam String query) {
        PlaceInfoResponse response = venueService.getVenueDetails(query);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Buscar venues cercanos",
        description = "Encuentra venues dentro de un radio específico desde coordenadas dadas.\n\n" +
                "**Radio por defecto:** 10,000 metros (10 km)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Venues encontrados")
    })
    @GetMapping("/nearby")
    public ResponseEntity<NearbySearchResponse> findVenuesNearby(
            @Parameter(description = "Latitud", example = "19.4326", required = true)
            @RequestParam Double lat,
            @Parameter(description = "Longitud", example = "-99.1332", required = true)
            @RequestParam Double lng,
            @Parameter(description = "Radio de búsqueda en metros", example = "10000")
            @RequestParam(defaultValue = "10000") Integer radius) {
        NearbySearchResponse response = venueService.findVenuesNearby(lat, lng, radius);
        return ResponseEntity.ok(response);
    }
    
    // ===== SERVICIOS CERCANOS AL VENUE =====
    
    @Operation(
        summary = "Obtener hoteles cercanos al venue",
        description = "Lista hoteles cerca de un venue específico, ordenados por distancia.\n\n" +
                "**Nota:** Usa el nombre del venue o Place ID como parámetro.\n\n" +
                "**Radio por defecto:** 5,000 metros (5 km)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Hoteles encontrados",
            content = @Content(schema = @Schema(implementation = NearbySearchResponse.class))
        )
    })
    @GetMapping("/hotels")
    public ResponseEntity<NearbySearchResponse> getHotelsNearVenue(
            @Parameter(
                description = "Nombre o Place ID del venue",
                example = "Foro Sol",
                required = true
            )
            @RequestParam String placeId,
            @Parameter(description = "Radio de búsqueda en metros", example = "5000")
            @RequestParam(defaultValue = "5000") Integer radius) {
        NearbySearchResponse response = venueService.getHotelsNearVenue(placeId, radius);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Obtener restaurantes cercanos",
        description = "Lista restaurantes cerca del venue.\n\n" +
                "**Radio por defecto:** 2,000 metros (2 km)"
    )
    @GetMapping("/restaurants")
    public ResponseEntity<NearbySearchResponse> getRestaurantsNearVenue(
            @Parameter(description = "Nombre o Place ID del venue", required = true)
            @RequestParam String placeId,
            @Parameter(description = "Radio de búsqueda en metros", example = "2000")
            @RequestParam(defaultValue = "2000") Integer radius) {
        NearbySearchResponse response = venueService.getRestaurantsNearVenue(placeId, radius);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Obtener estacionamientos cercanos",
        description = "Lista estacionamientos cerca del venue con información de capacidad y precios."
    )
    @GetMapping("/parking")
    public ResponseEntity<NearbySearchResponse> getParkingNearVenue(
            @Parameter(description = "Nombre o Place ID del venue", required = true)
            @RequestParam String placeId) {
        NearbySearchResponse response = venueService.getParkingNearVenue(placeId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Obtener transporte público cercano",
        description = "Lista opciones de transporte público (metro, autobús, tren) cerca del venue."
    )
    @GetMapping("/transport")
    public ResponseEntity<NearbySearchResponse> getTransportNearVenue(
            @Parameter(description = "Nombre o Place ID del venue", required = true)
            @RequestParam String placeId) {
        NearbySearchResponse response = venueService.getTransportNearVenue(placeId);
        return ResponseEntity.ok(response);
    }
}