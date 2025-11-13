package com.example.rockStadium.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.rockStadium.dto.MusicGenreResponse;
import com.example.rockStadium.service.UserPreferenceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Public controller for music genres catalog
 * Controlador público para el catálogo de géneros musicales
 */
@RestController
@RequestMapping("/api/v1/genres")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Music Genres", description = "Public endpoints for browsing music genres catalog")
public class MusicGenreController {
    
    private final UserPreferenceService preferenceService;
    
    /**
     * Endpoint público para obtener todos los géneros musicales disponibles
     * No requiere autenticación porque es información de catálogo
     */
    @Operation(
        summary = "Get all available music genres",
        description = """
                Returns the complete catalog of available music genres.
                
                **Public endpoint** - No authentication required.
                
                This endpoint shows all music genres available in the system
                that users can add to their favorites.
                """,
        security = {} // Este endpoint NO requiere autenticación
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Genres retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<MusicGenreResponse>> getAllGenres(
            @Parameter(description = "Page number (starts at 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Number of genres per page", example = "20")
            @RequestParam(defaultValue = "20") int pageSize) {
        
        log.info("📚 Getting all genres catalog (page: {}, size: {})", page, pageSize);
        
        // Obtener todos los géneros del servicio
        List<MusicGenreResponse> allGenres = preferenceService.getAllGenres();
        
        // Lógica de paginación manual
        int start = page * pageSize;
        int end = Math.min((start + pageSize), allGenres.size());
        
        // Manejar caso donde start está más allá del tamaño de la lista
        if (start >= allGenres.size()) {
            return ResponseEntity.ok(List.of());
        }
        
        // Devolver solo la sublista sin metadata
        List<MusicGenreResponse> pageContent = allGenres.subList(start, end);
        
        log.info("✅ Returning {} genres (page {} of {})", 
                pageContent.size(), page, (allGenres.size() / pageSize) + 1);
        
        return ResponseEntity.ok(pageContent);
    }
}