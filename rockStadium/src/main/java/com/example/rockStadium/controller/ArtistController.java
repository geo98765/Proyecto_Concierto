package com.example.rockStadium.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.rockStadium.dto.ArtistResponse;
import com.example.rockStadium.service.SpotifyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController {
    
    private final SpotifyService spotifyService;
    
    /**
     * GET /api/artists/search?name=Metallica
     * Funcionalidad 44: Buscar artista por nombre
     */
    @GetMapping("/search")
    public ResponseEntity<List<ArtistResponse>> searchArtists(@RequestParam String name) {
        List<ArtistResponse> artists = spotifyService.searchArtistsByName(name);
        return ResponseEntity.ok(artists);
    }
    
    /**
     * GET /api/artists/{spotifyId}
     * Funcionalidad 45: Obtener información de artista
     */
    @GetMapping("/{spotifyId}")
    public ResponseEntity<ArtistResponse> getArtistById(@PathVariable String spotifyId) {
        ArtistResponse artist = spotifyService.getArtistById(spotifyId);
        return ResponseEntity.ok(artist);
    }
    
    /**
     * GET /api/artists/multiple?ids=id1,id2,id3
     * Obtener múltiples artistas por sus IDs
     */
    @GetMapping("/multiple")
    public ResponseEntity<List<ArtistResponse>> getArtistsByIds(
            @RequestParam List<String> ids) {
        List<ArtistResponse> artists = spotifyService.getArtistsByIds(ids);
        return ResponseEntity.ok(artists);
    }
}