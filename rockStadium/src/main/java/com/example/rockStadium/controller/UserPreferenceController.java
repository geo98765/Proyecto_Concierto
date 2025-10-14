// package com.example.rockStadium.controller;

// import com.example.rockStadium.dto.*;
// import com.example.rockStadium.service.UserPreferenceServiceImpl;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/api/users/{userId}/preferences")
// @RequiredArgsConstructor
// public class UserPreferenceController {
    
//     private final UserPreferenceServiceImpl preferenceService;
    
//     /**
//      * GET /api/users/{userId}/preferences
//      * Funcionalidad 13: Obtener preferencias del usuario
//      */
//     @GetMapping
//     public ResponseEntity<UserPreferenceResponse> getPreferences(@PathVariable Integer userId) {
//         UserPreferenceResponse response = preferenceService.getPreferences(userId);
//         return ResponseEntity.ok(response);
//     }
    
//     /**
//      * PUT /api/users/{userId}/preferences
//      * Funcionalidades 12 y 14: Configurar/Actualizar preferencias de búsqueda
//      */
//     @PutMapping
//     public ResponseEntity<UserPreferenceResponse> updatePreferences(
//             @PathVariable Integer userId,
//             @Valid @RequestBody UserPreferenceRequest request) {
//         UserPreferenceResponse response = preferenceService.update(userId, request);
//         return ResponseEntity.ok(response);
//     }
    
//     // ===== ARTISTAS FAVORITOS =====
    
//     /**
//      * GET /api/users/{userId}/preferences/artists
//      * Funcionalidad 8: Obtener artistas favoritos
//      */
//     @GetMapping("/artists")
//     public ResponseEntity<List<ArtistDto>> getFavoriteArtists(@PathVariable Integer userId) {
//         List<ArtistDto> artists = preferenceService.getFavoriteArtists(userId);
//         return ResponseEntity.ok(artists);
//     }
    
//     /**
//      * POST /api/users/{userId}/preferences/artists
//      * Funcionalidad 6: Agregar artista favorito
//      * Body: { "spotifyId": "3RGLhK1IP9jnYFH4BRFJBS" }
//      */
//     @PostMapping("/artists")
//     public ResponseEntity<UserPreferenceResponse> addFavoriteArtist(
//             @PathVariable Integer userId,
//             @RequestBody AddFavoriteArtistRequest request) {
//         UserPreferenceResponse response = preferenceService.addFavoriteArtist(userId, request.getSpotifyId());
//         return ResponseEntity.ok(response);
//     }
    
//     /**
//      * DELETE /api/users/{userId}/preferences/artists/{artistId}
//      * Funcionalidad 7: Eliminar artista favorito
//      */
//     @DeleteMapping("/artists/{artistId}")
//     public ResponseEntity<UserPreferenceResponse> removeFavoriteArtist(
//             @PathVariable Integer userId,
//             @PathVariable Integer artistId) {
//         UserPreferenceResponse response = preferenceService.removeFavoriteArtist(userId, artistId);
//         return ResponseEntity.ok(response);
//     }
    
//     // ===== GÉNEROS FAVORITOS =====
    
//     /**
//      * GET /api/users/{userId}/preferences/genres
//      * Funcionalidad 11: Obtener géneros favoritos
//      */
//     @GetMapping("/genres")
//     public ResponseEntity<List<GenreDto>> getFavoriteGenres(@PathVariable Integer userId) {
//         List<GenreDto> genres = preferenceService.getFavoriteGenres(userId);
//         return ResponseEntity.ok(genres);
//     }
    
//     /**
//      * POST /api/users/{userId}/preferences/genres
//      * Funcionalidad 9: Agregar género favorito
//      * Body: { "genreId": 1 }
//      */
//     @PostMapping("/genres")
//     public ResponseEntity<UserPreferenceResponse> addFavoriteGenre(
//             @PathVariable Integer userId,
//             @RequestBody AddFavoriteGenreRequest request) {
//         UserPreferenceResponse response = preferenceService.addFavoriteGenre(userId, request.getGenreId());
//         return ResponseEntity.ok(response);
//     }
    
//     /**
//      * DELETE /api/users/{userId}/preferences/genres/{genreId}
//      * Funcionalidad 10: Eliminar género favorito
//      */
//     @DeleteMapping("/genres/{genreId}")
//     public ResponseEntity<UserPreferenceResponse> removeFavoriteGenre(
//             @PathVariable Integer userId,
//             @PathVariable Integer genreId) {
//         UserPreferenceResponse response = preferenceService.removeFavoriteGenre(userId, genreId);
//         return ResponseEntity.ok(response);
//     }
    
//     /**
//      * GET /api/users/{userId}/preferences/genres/available
//      * Obtener todos los géneros disponibles
//      */
//     @GetMapping("/genres/available")
//     public ResponseEntity<List<GenreDto>> getAllGenres(@PathVariable Integer userId) {
//         List<GenreDto> genres = preferenceService.getAllGenres();
//         return ResponseEntity.ok(genres);
//     }
// }