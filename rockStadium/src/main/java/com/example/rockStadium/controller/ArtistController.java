package com.example.rockStadium.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.rockStadium.dto.ArtistCompleteInfoResponse;
import com.example.rockStadium.dto.ArtistResponse;
import com.example.rockStadium.service.ArtistEventService;
import com.example.rockStadium.service.SpotifyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Artistas", description = "Endpoints para gestión y búsqueda de artistas musicales")
public class ArtistController {
    
    private final SpotifyService spotifyService;
    private final ArtistEventService artistEventService;
    
    @Operation(
        summary = "Buscar artistas por nombre",
        description = "Busca artistas en Spotify por nombre. Retorna hasta 10 resultados con información detallada."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Artistas encontrados exitosamente",
            content = @Content(schema = @Schema(implementation = ArtistResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Parámetro de búsqueda inválido"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/search")
    public ResponseEntity<List<ArtistResponse>> searchArtists(
            @Parameter(description = "Nombre del artista a buscar", example = "Metallica", required = true)
            @RequestParam String name) {
        log.info("Buscando artistas con nombre: {}", name);
        List<ArtistResponse> artists = spotifyService.searchArtistsByName(name);
        return ResponseEntity.ok(artists);
    }
    
    @Operation(
        summary = "Obtener información de artista por ID",
        description = "Retorna información detallada de un artista usando su Spotify ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Información del artista obtenida exitosamente"
        ),
        @ApiResponse(responseCode = "404", description = "Artista no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{spotifyId}")
    public ResponseEntity<ArtistResponse> getArtistById(
            @Parameter(description = "Spotify ID del artista", example = "2ye2Wgw4gimLv2eAKyk1NB", required = true)
            @PathVariable String spotifyId) {
        log.info("Obteniendo información del artista: {}", spotifyId);
        ArtistResponse artist = spotifyService.getArtistById(spotifyId);
        return ResponseEntity.ok(artist);
    }
    
    @Operation(
        summary = "Obtener información completa del artista con eventos",
        description = "Retorna información detallada del artista incluyendo:\n" +
                "- Datos del artista desde Spotify\n" +
                "- Eventos próximos desde Ticketmaster\n" +
                "- Información de venues (recintos)\n" +
                "- Clima del lugar\n" +
                "- Hoteles cercanos (top 5)\n" +
                "- Opciones de transporte público"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Información completa obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = ArtistCompleteInfoResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Artista no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error al procesar la solicitud")
    })
    @GetMapping("/{spotifyId}/complete-info")
    public ResponseEntity<ArtistCompleteInfoResponse> getArtistCompleteInfo(
            @Parameter(
                description = "Spotify ID del artista", 
                example = "4q3ewBCX7sLwd24euuV69X",
                required = true
            )
            @PathVariable String spotifyId) {
        log.info("🎸 Obteniendo información completa del artista: {}", spotifyId);
        ArtistCompleteInfoResponse response = artistEventService.getArtistCompleteInfo(spotifyId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Obtener múltiples artistas por IDs",
        description = "Retorna información de múltiples artistas proporcionando una lista de Spotify IDs"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Artistas obtenidos exitosamente"),
        @ApiResponse(responseCode = "400", description = "Lista de IDs inválida")
    })
    @GetMapping("/multiple")
    public ResponseEntity<List<ArtistResponse>> getArtistsByIds(
            @Parameter(
                description = "Lista de Spotify IDs separados por coma",
                example = "2ye2Wgw4gimLv2eAKyk1NB,4q3ewBCX7sLwd24euuV69X",
                required = true
            )
            @RequestParam List<String> ids) {
        log.info("Obteniendo {} artistas", ids.size());
        List<ArtistResponse> artists = spotifyService.getArtistsByIds(ids);
        return ResponseEntity.ok(artists);
    }
}