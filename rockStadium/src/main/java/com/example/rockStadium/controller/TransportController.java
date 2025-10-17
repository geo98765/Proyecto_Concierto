package com.example.rockStadium.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.rockStadium.dto.NearbySearchResponse;
import com.example.rockStadium.dto.PlaceInfoResponse;
import com.example.rockStadium.service.TransportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/transport")
@RequiredArgsConstructor
@Tag(name = "Transporte Público", description = "Opciones de transporte público cerca de venues")
public class TransportController {
    
    private final TransportService transportService;
    
    @Operation(
        summary = "Obtener opciones de transporte disponibles",
        description = "Funcionalidad 32: Lista todas las opciones de transporte público cerca del venue.\n\n" +
                "**Incluye:**\n" +
                "- Estaciones de metro\n" +
                "- Paradas de autobús\n" +
                "- Estaciones de tren\n" +
                "- Información de distancia y ubicación"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Opciones de transporte encontradas",
            content = @Content(schema = @Schema(implementation = NearbySearchResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Venue no encontrado")
    })
    @GetMapping("/options/{venueId}")
    public ResponseEntity<NearbySearchResponse> getTransportOptions(
            @Parameter(description = "ID del venue", example = "1", required = true)
            @PathVariable Integer venueId) {
        NearbySearchResponse response = transportService.getTransportOptions(venueId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Obtener estaciones de metro cercanas",
        description = "Busca estaciones de metro en un radio de 1 km del venue."
    )
    @GetMapping("/metro/{venueId}")
    public ResponseEntity<NearbySearchResponse> getNearbyMetroStations(
            @Parameter(description = "ID del venue", example = "1", required = true)
            @PathVariable Integer venueId) {
        NearbySearchResponse response = transportService.getNearbyMetroStations(venueId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Obtener paradas de autobús cercanas",
        description = "Busca paradas de autobús en un radio de 800 metros del venue."
    )
    @GetMapping("/bus/{venueId}")
    public ResponseEntity<NearbySearchResponse> getNearbyBusStops(
            @Parameter(description = "ID del venue", example = "1", required = true)
            @PathVariable Integer venueId) {
        NearbySearchResponse response = transportService.getNearbyBusStops(venueId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Obtener estaciones de tren cercanas",
        description = "Busca estaciones de tren en un radio de 2 km del venue."
    )
    @GetMapping("/train/{venueId}")
    public ResponseEntity<NearbySearchResponse> getNearbyTrainStations(
            @Parameter(description = "ID del venue", example = "1", required = true)
            @PathVariable Integer venueId) {
        NearbySearchResponse response = transportService.getNearbyTrainStations(venueId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Obtener detalles específicos de transporte",
        description = "Funcionalidad 33: Retorna información detallada de una estación o parada específica.\n\n" +
                "**Incluye:** horarios, líneas, opiniones de usuarios, accesibilidad."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Detalles del transporte obtenidos",
            content = @Content(schema = @Schema(implementation = PlaceInfoResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Estación no encontrada")
    })
    @GetMapping("/details")
    public ResponseEntity<PlaceInfoResponse> getTransportDetails(
            @Parameter(description = "Nombre de la estación/parada", example = "Metro Ciudad Deportiva", required = true)
            @RequestParam String name,
            @Parameter(description = "Ubicación (coordenadas)", example = "19.4326,-99.1332", required = true)
            @RequestParam String location) {
        PlaceInfoResponse response = transportService.getTransportDetails(name, location);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Obtener rutas de transporte al venue",
        description = "Funcionalidad 34: Calcula rutas desde la ubicación del usuario hasta el venue.\n\n" +
                "**Incluye:**\n" +
                "- Punto de partida y destino\n" +
                "- Duración estimada\n" +
                "- Costo aproximado\n\n" +
                "**Nota:** Para rutas completas detalladas, considera integrar Google Directions API."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ruta calculada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Coordenadas inválidas"),
        @ApiResponse(responseCode = "404", description = "Venue no encontrado")
    })
    @GetMapping("/route/{venueId}")
    public ResponseEntity<String> getTransportRoute(
            @Parameter(description = "ID del venue destino", example = "1", required = true)
            @PathVariable Integer venueId,
            @Parameter(description = "Latitud de origen", example = "19.4326", required = true)
            @RequestParam BigDecimal startLat,
            @Parameter(description = "Longitud de origen", example = "-99.1332", required = true)
            @RequestParam BigDecimal startLng) {
        String route = transportService.getTransportRoute(startLat, startLng, venueId);
        return ResponseEntity.ok(route);
    }
    
    @Operation(
        summary = "Calcular ruta personalizada",
        description = "Funcionalidad 35: Genera una ruta entre dos coordenadas específicas.\n\n" +
                "Útil cuando el usuario quiere una ruta desde una ubicación diferente a su ubicación actual."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ruta personalizada calculada"),
        @ApiResponse(responseCode = "400", description = "Coordenadas inválidas")
    })
    @GetMapping("/custom-route")
    public ResponseEntity<String> calculateCustomRoute(
            @Parameter(description = "Latitud de origen", example = "19.4326", required = true)
            @RequestParam BigDecimal startLat,
            @Parameter(description = "Longitud de origen", example = "-99.1332", required = true)
            @RequestParam BigDecimal startLng,
            @Parameter(description = "Latitud de destino", example = "19.4200", required = true)
            @RequestParam BigDecimal endLat,
            @Parameter(description = "Longitud de destino", example = "-99.1500", required = true)
            @RequestParam BigDecimal endLng) {
        String route = transportService.calculateCustomRoute(startLat, startLng, endLat, endLng);
        return ResponseEntity.ok(route);
    }
}