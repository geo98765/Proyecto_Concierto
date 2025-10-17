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
import com.example.rockStadium.service.HotelService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;  // ← ESTE ES EL IMPORTANTE
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
@Tag(name = "Hoteles", description = "Búsqueda de hoteles cercanos a venues de conciertos")
public class HotelController {
    
    private final HotelService hotelService;
    
    @Operation(
        summary = "Obtener hoteles cercanos a un venue",
        description = "Lista hoteles cerca de un venue específico ordenados por distancia.\n\n" +
                "**Funcionalidad 28:** Incluye nombre, ubicación, calificación, distancia y opiniones.\n\n" +
                "**Máximo:** 20 resultados.\n" +
                "**Radio por defecto:** 5,000 metros"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Hoteles encontrados exitosamente",
            content = @Content(schema = @Schema(implementation = NearbySearchResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Venue no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error al buscar hoteles")
    })
    @GetMapping("/near-venue/{venueId}")
    public ResponseEntity<NearbySearchResponse> getHotelsNearVenue(
            @Parameter(description = "ID del venue en la base de datos", example = "1", required = true)
            @PathVariable Integer venueId,
            @Parameter(description = "Distancia máxima en metros", example = "5000")
            @RequestParam(required = false) Integer maxDistance) {
        NearbySearchResponse response = hotelService.getHotelsNearVenue(venueId, maxDistance);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Obtener detalles completos de un hotel",
        description = "Funcionalidad 29: Retorna información detallada del hotel incluyendo opiniones de usuarios.\n\n" +
                "**Incluye:** nombre, dirección, calificación, reseñas, amenidades, horarios, contacto."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Detalles del hotel obtenidos",
            content = @Content(schema = @Schema(implementation = PlaceInfoResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Hotel no encontrado")
    })
    @GetMapping("/details")
    public ResponseEntity<PlaceInfoResponse> getHotelDetails(
            @Parameter(description = "Nombre del hotel", example = "Hotel Aeropuerto Express", required = true)
            @RequestParam String name,
            @Parameter(description = "Ubicación (coordenadas)", example = "19.4326,-99.1332", required = true)
            @RequestParam String location) {
        PlaceInfoResponse response = hotelService.getHotelDetails(name, location);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Filtrar hoteles por calificación mínima",
        description = "Funcionalidad 30: Filtra hoteles con una calificación mínima especificada.\n\n" +
                "**Rango válido:** 1.0 a 5.0 estrellas"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hoteles filtrados exitosamente"),
        @ApiResponse(responseCode = "400", description = "Calificación inválida (debe estar entre 1-5)")
    })
    @GetMapping("/filter-by-rating/{venueId}")
    public ResponseEntity<NearbySearchResponse> filterHotelsByRating(
            @Parameter(description = "ID del venue", example = "1", required = true)
            @PathVariable Integer venueId,
            @Parameter(description = "Calificación mínima (1.0 - 5.0)", example = "4.0", required = true)
            @RequestParam BigDecimal minRating) {
        NearbySearchResponse response = hotelService.filterHotelsByRating(venueId, minRating);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Filtrar hoteles por distancia máxima",
        description = "Funcionalidad 31: Retorna hoteles dentro de una distancia máxima del venue.\n\n" +
                "Útil para encontrar hoteles caminables al concierto."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hoteles filtrados por distancia"),
        @ApiResponse(responseCode = "400", description = "Distancia inválida")
    })
    @GetMapping("/filter-by-distance/{venueId}")
    public ResponseEntity<NearbySearchResponse> filterHotelsByDistance(
            @Parameter(description = "ID del venue", example = "1", required = true)
            @PathVariable Integer venueId,
            @Parameter(description = "Distancia máxima en metros", example = "3000", required = true)
            @RequestParam Integer maxDistance) {
        NearbySearchResponse response = hotelService.filterHotelsByDistance(venueId, maxDistance);
        return ResponseEntity.ok(response);
    }

    
}