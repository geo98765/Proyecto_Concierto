// package com.example.rockStadium.service;

// import com.example.rockStadium.dto.ArtistDto;
// import com.example.rockStadium.dto.ConcertDto;
// import com.example.rockStadium.model.Artist;
// import com.example.rockStadium.model.Concert;
// import com.example.rockStadium.repository.ArtistRepository;
// import com.example.rockStadium.repository.ConcertRepository;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.stream.Collectors;

// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class ArtistService {
    
//     private final ArtistRepository artistRepository;
//     private final ConcertRepository concertRepository;
//     private final SpotifyService spotifyService;
    
//     /**
//      * Funcionalidad 44: Buscar artista por nombre
//      * TODO viene directamente de Spotify (no usa BD)
//      */
//     public List<ArtistDto> searchArtist(String query) {
//         log.info("Buscando artista en Spotify: {}", query);
        
//         // Buscar directamente en Spotify (sin guardar en BD)
//         return spotifyService.searchArtist(query);
//     }
    
//     /**
//      * Funcionalidad 44: Buscar artista por Spotify ID
//      * TODO viene directamente de Spotify
//      */
//     public ArtistDto getArtistBySpotifyId(String spotifyId) {
//         log.info("Obteniendo artista por Spotify ID: {}", spotifyId);
        
//         // Obtener directamente de Spotify (sin guardar en BD)
//         return spotifyService.getArtistById(spotifyId);
//     }
    
//     /**
//      * Funcionalidad 45: Obtener información detallada del artista
//      * Si el artista está en BD (porque tiene conciertos), lo combina con datos de Spotify
//      * Si no está en BD, lo obtiene solo de Spotify
//      */
//     public ArtistDto getArtistInfo(String spotifyId) {
//         log.info("Obteniendo información del artista: {}", spotifyId);
        
//         // SIEMPRE obtener de Spotify para tener datos actualizados
//         return spotifyService.getArtistById(spotifyId);
//     }
    
//     /**
//      * Funcionalidad 46: Obtener conciertos del artista
//      * Ordenados por fecha
//      */
//     @Transactional(readOnly = true)
//     public List<ConcertDto> getArtistConcerts(String spotifyId) {
//         log.info("Obteniendo conciertos del artista: {}", spotifyId);
        
//         // Buscar artista en BD (solo si tiene conciertos registrados)
//         Artist artist = artistRepository.findBySpotifyId(spotifyId)
//                 .orElse(null);
        
//         if (artist == null) {
//             log.info("Artista no tiene conciertos registrados en BD");
//             return List.of(); // Retornar lista vacía
//         }
        
//         List<Concert> concerts = concertRepository.findByArtist_ArtistId(artist.getArtistId());
        
//         return concerts.stream()
//                 .map(this::convertConcertToDto)
//                 .sorted((c1, c2) -> c1.getEventDate().compareTo(c2.getEventDate()))
//                 .collect(Collectors.toList());
//     }
    
//     /**
//      * Obtener próximos conciertos del artista
//      */
//     @Transactional(readOnly = true)
//     public List<ConcertDto> getUpcomingArtistConcerts(String spotifyId) {
//         log.info("Obteniendo próximos conciertos del artista: {}", spotifyId);
        
//         Artist artist = artistRepository.findBySpotifyId(spotifyId)
//                 .orElse(null);
        
//         if (artist == null) {
//             return List.of();
//         }
        
//         List<Concert> concerts = concertRepository.findByArtist_ArtistId(artist.getArtistId());
//         LocalDateTime now = LocalDateTime.now();
        
//         return concerts.stream()
//                 .filter(c -> c.getEventDate().isAfter(now))
//                 .map(this::convertConcertToDto)
//                 .sorted((c1, c2) -> c1.getEventDate().compareTo(c2.getEventDate()))
//                 .collect(Collectors.toList());
//     }
    
//     /**
//      * Obtener artistas por género (de Spotify)
//      */
//     public List<ArtistDto> getArtistsByGenre(String genreName) {
//         log.info("Buscando artistas del género en Spotify: {}", genreName);
        
//         // Buscar en Spotify usando el género como query
//         return spotifyService.searchArtist(genreName);
//     }
    
//     /**
//      * Obtener artistas relacionados
//      */
//     public List<ArtistDto> getRelatedArtists(String spotifyId) {
//         log.info("Obteniendo artistas relacionados desde Spotify: {}", spotifyId);
        
//         return spotifyService.getRelatedArtists(spotifyId);
//     }
    
//     // ===== MÉTODOS DE CONVERSIÓN =====
    
//     private ConcertDto convertConcertToDto(Concert concert) {
//     return ConcertDto.builder()
//             .concertId(concert.getConcertId())
//             .name(concert.getName())
//             .eventDate(concert.getEventDate())
//             .priceMin(concert.getPriceMin())
//             .priceMax(concert.getPriceMax())
//             .status(concert.getStatus().name())
//             .description(concert.getDescription())
//             .ticketUrl(concert.getTicketUrl())
//             .imageUrl(concert.getImageUrl())
//             // Información del artista
//             .artistId(concert.getArtist().getArtistId())
//             .artistName(concert.getArtist().getName())
//             .artistImageUrl(concert.getArtist().getImageUrl())
//             // Información del venue
//             .venueId(concert.getVenue().getVenueId())
//             .venueName(concert.getVenue().getName())
//             .venueCity(concert.getVenue().getCity())
//             .venueLatitude(concert.getVenue().getLatitude())
//             .venueLongitude(concert.getVenue().getLongitude())
//             // Géneros
//             .genres(concert.getGenres().stream()
//                     .map(genre -> genre.getName())
//                     .collect(Collectors.toList()))
//             .build();
// }
// }
// }