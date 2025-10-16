package com.example.rockStadium.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rockStadium.dto.AddFavoriteArtistRequest;
import com.example.rockStadium.dto.AddFavoriteGenreRequest;
import com.example.rockStadium.dto.ArtistResponse;
import com.example.rockStadium.dto.MusicGenreResponse;
import com.example.rockStadium.dto.UserPreferenceRequest;
import com.example.rockStadium.dto.UserPreferenceResponse;
import com.example.rockStadium.service.UserPreferenceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users/{userId}/preferences")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Preferencias de Usuario", description = "Endpoints para gestión de preferencias, artistas y géneros favoritos")
public class UserPreferenceController {
    
    private final UserPreferenceService preferenceService;
    
    // ===== PREFERENCIAS DE BÚSQUEDA =====
    
    @Operation(
        summary = "Obtener preferencias del usuario",
        description = "Retorna las preferencias de búsqueda del usuario, incluyendo artistas y géneros favoritos"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Preferencias obtenidas exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping
    public ResponseEntity<UserPreferenceResponse> getPreferences(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Integer userId) {
        log.info("📋 Obteniendo preferencias del usuario: {}", userId);
        UserPreferenceResponse response = preferenceService.getPreferences(userId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Configurar o actualizar preferencias",
        description = "Configura el radio de búsqueda y preferencias de notificaciones del usuario"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Preferencias actualizadas exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping
    public ResponseEntity<UserPreferenceResponse> updatePreferences(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Integer userId,
            @Valid @RequestBody UserPreferenceRequest request) {
        log.info("⚙️ Actualizando preferencias del usuario: {}", userId);
        UserPreferenceResponse response = preferenceService.createOrUpdatePreferences(userId, request);
        return ResponseEntity.ok(response);
    }
    
    // ===== ARTISTAS FAVORITOS =====
    
    @Operation(
        summary = "Obtener artistas favoritos",
        description = "Retorna la lista de artistas favoritos del usuario"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Artistas obtenidos exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/artists")
    public ResponseEntity<List<ArtistResponse>> getFavoriteArtists(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Integer userId) {
        log.info("🎸 Obteniendo artistas favoritos del usuario: {}", userId);
        List<ArtistResponse> artists = preferenceService.getFavoriteArtists(userId);
        return ResponseEntity.ok(artists);
    }
    
    @Operation(
        summary = "Agregar artista favorito",
        description = "Agrega un artista a los favoritos del usuario. Máximo 50 artistas."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Artista agregado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Límite alcanzado o artista ya existe"),
        @ApiResponse(responseCode = "404", description = "Usuario o artista no encontrado")
    })
    @PostMapping("/artists")
    public ResponseEntity<UserPreferenceResponse> addFavoriteArtist(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Integer userId,
            @Valid @RequestBody AddFavoriteArtistRequest request) {
        log.info("➕ Agregando artista favorito para usuario: {}", userId);
        UserPreferenceResponse response = preferenceService.addFavoriteArtist(userId, request.getSpotifyId());
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Eliminar artista favorito",
        description = "Elimina un artista de los favoritos del usuario"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Artista eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario o artista no encontrado")
    })
    @DeleteMapping("/artists/{artistId}")
    public ResponseEntity<UserPreferenceResponse> removeFavoriteArtist(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Integer userId,
            @Parameter(description = "ID del artista en la BD", example = "5", required = true)
            @PathVariable Integer artistId) {
        log.info("➖ Eliminando artista favorito del usuario: {}", userId);
        UserPreferenceResponse response = preferenceService.removeFavoriteArtist(userId, artistId);
        return ResponseEntity.ok(response);
    }
    
    // ===== GÉNEROS FAVORITOS =====
    
    @Operation(
        summary = "Obtener géneros favoritos",
        description = "Retorna la lista de géneros musicales favoritos del usuario"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Géneros obtenidos exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/genres")
    public ResponseEntity<List<MusicGenreResponse>> getFavoriteGenres(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Integer userId) {
        log.info("🎵 Obteniendo géneros favoritos del usuario: {}", userId);
        List<MusicGenreResponse> genres = preferenceService.getFavoriteGenres(userId);
        return ResponseEntity.ok(genres);
    }
    
    @Operation(
        summary = "Agregar género favorito",
        description = "Agrega un género musical a los favoritos del usuario. Máximo 10 géneros."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Género agregado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Límite alcanzado o género ya existe"),
        @ApiResponse(responseCode = "404", description = "Usuario o género no encontrado")
    })
    @PostMapping("/genres")
    public ResponseEntity<UserPreferenceResponse> addFavoriteGenre(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Integer userId,
            @Valid @RequestBody AddFavoriteGenreRequest request) {
        log.info("➕ Agregando género favorito para usuario: {}", userId);
        UserPreferenceResponse response = preferenceService.addFavoriteGenre(userId, request.getGenreId());
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Eliminar género favorito",
        description = "Elimina un género musical de los favoritos del usuario"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Género eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario o género no encontrado")
    })
    @DeleteMapping("/genres/{genreId}")
    public ResponseEntity<UserPreferenceResponse> removeFavoriteGenre(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Integer userId,
            @Parameter(description = "ID del género musical", example = "3", required = true)
            @PathVariable Integer genreId) {
        log.info("➖ Eliminando género favorito del usuario: {}", userId);
        UserPreferenceResponse response = preferenceService.removeFavoriteGenre(userId, genreId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Obtener todos los géneros disponibles",
        description = "Retorna el catálogo completo de géneros musicales disponibles"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Géneros obtenidos exitosamente")
    })
    @GetMapping("/genres/available")
    public ResponseEntity<List<MusicGenreResponse>> getAllGenres(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Integer userId) {
        log.info("📚 Obteniendo catálogo de géneros");
        List<MusicGenreResponse> genres = preferenceService.getAllGenres();
        return ResponseEntity.ok(genres);
    }
}