// package com.example.rockStadium.service;

// import com.example.rockStadium.dto.*;
// import com.example.rockStadium.model.Artist;
// import com.example.rockStadium.model.Genre;
// import com.example.rockStadium.model.User;
// import com.example.rockStadium.model.UserPreference;
// import com.example.rockStadium.repository.*;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.math.BigDecimal;
// import java.util.List;
// import java.util.stream.Collectors;

// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class UserPreferenceServiceImpl implements UserPreferenceService {
    
//     private final UserPreferenceRepository preferenceRepository;
//     private final UserRepository userRepository;
//     private final ArtistRepository artistRepository;
//     private final GenreRepository genreRepository;
//     private final SpotifyService spotifyService;
    
//     private static final int MAX_FAVORITE_ARTISTS = 50;
//     private static final int MAX_FAVORITE_GENRES = 10;
    
//     @Override
//     @Transactional
//     public UserPreferenceResponse create(UserPreferenceRequest req) {
//         throw new UnsupportedOperationException("Use getPreferences or update instead");
//     }
    
//     @Transactional(readOnly = true)
//     public UserPreferenceResponse getPreferences(Integer userId) {
//         log.info("Obteniendo preferencias del usuario: {}", userId);
        
//         UserPreference preference = preferenceRepository.findByUser_UserId(userId)
//                 .orElseGet(() -> createDefaultPreferences(userId));
        
//         return convertToDto(preference);
//     }
    
//     @Override
//     @Transactional
//     public UserPreferenceResponse update(Integer userId, UserPreferenceRequest req) {
//         log.info("Actualizando preferencias del usuario: {}", userId);
        
//         UserPreference preference = preferenceRepository.findByUser_UserId(userId)
//                 .orElseGet(() -> createDefaultPreferences(userId));
        
//         if (req.getSearchRadiusKm() != null) {
//             validateSearchRadius(req.getSearchRadiusKm());
//             preference.setSearchRadiusKm(req.getSearchRadiusKm());
//         }
        
//         if (req.getEmailNotifications() != null) {
//             preference.setEmailNotifications(req.getEmailNotifications());
//         }
        
//         if (req.getPushNotifications() != null) {
//             preference.setPushNotifications(req.getPushNotifications());
//         }
        
//         preference = preferenceRepository.save(preference);
//         return convertToDto(preference);
//     }
    
//     @Override
//     @Transactional
//     public UserPreferenceResponse addFavoriteArtist(Integer userId, String spotifyId) {
//         log.info("Agregando artista favorito para usuario {}: {}", userId, spotifyId);
        
//         UserPreference preference = getOrCreatePreference(userId);
        
//         if (preference.getFavoriteArtists().size() >= MAX_FAVORITE_ARTISTS) {
//             throw new RuntimeException("Has alcanzado el límite de " + MAX_FAVORITE_ARTISTS + " artistas favoritos");
//         }
        
//         Artist artist = spotifyService.saveOrUpdateArtistFromSpotify(spotifyId);
        
//         boolean alreadyExists = preference.getFavoriteArtists().stream()
//                 .anyMatch(a -> a.getArtistId().equals(artist.getArtistId()));
        
//         if (alreadyExists) {
//             throw new RuntimeException("El artista ya está en tus favoritos");
//         }
        
//         preference.getFavoriteArtists().add(artist);
//         preference = preferenceRepository.save(preference);
        
//         log.info("Artista agregado exitosamente. Total de favoritos: {}", 
//                 preference.getFavoriteArtists().size());
        
//         return convertToDto(preference);
//     }
    
//     @Override
//     @Transactional
//     public UserPreferenceResponse removeFavoriteArtist(Integer userId, Integer artistId) {
//         log.info("Eliminando artista favorito {} del usuario {}", artistId, userId);
        
//         UserPreference preference = getOrCreatePreference(userId);
        
//         boolean removed = preference.getFavoriteArtists()
//                 .removeIf(a -> a.getArtistId().equals(artistId));
        
//         if (!removed) {
//             throw new RuntimeException("El artista no está en tus favoritos");
//         }
        
//         preference = preferenceRepository.save(preference);
        
//         log.info("Artista eliminado exitosamente. Total de favoritos: {}", 
//                 preference.getFavoriteArtists().size());
        
//         return convertToDto(preference);
//     }
    
//     @Override
//     @Transactional(readOnly = true)
//     public List<ArtistDto> getFavoriteArtists(Integer userId) {
//         log.info("Obteniendo artistas favoritos del usuario: {}", userId);
        
//         UserPreference preference = preferenceRepository.findByUser_UserId(userId)
//                 .orElseThrow(() -> new RuntimeException("Preferencias no encontradas"));
        
//         return preference.getFavoriteArtists().stream()
//                 .map(this::convertArtistToDto)
//                 .collect(Collectors.toList());
//     }
    
//     @Override
//     @Transactional
//     public UserPreferenceResponse addFavoriteGenre(Integer userId, Integer genreId) {
//         log.info("Agregando género favorito {} para usuario {}", genreId, userId);
        
//         UserPreference preference = getOrCreatePreference(userId);
        
//         if (preference.getFavoriteGenres().size() >= MAX_FAVORITE_GENRES) {
//             throw new RuntimeException("Has alcanzado el límite de " + MAX_FAVORITE_GENRES + " géneros favoritos");
//         }
        
//         Genre genre = genreRepository.findById(genreId)
//                 .orElseThrow(() -> new RuntimeException("Género no encontrado con ID: " + genreId));
        
//         boolean alreadyExists = preference.getFavoriteGenres().stream()
//                 .anyMatch(g -> g.getGenreId().equals(genreId));
        
//         if (alreadyExists) {
//             throw new RuntimeException("El género ya está en tus favoritos");
//         }
        
//         preference.getFavoriteGenres().add(genre);
//         preference = preferenceRepository.save(preference);
        
//         log.info("Género agregado exitosamente. Total de favoritos: {}", 
//                 preference.getFavoriteGenres().size());
        
//         return convertToDto(preference);
//     }
    
//     @Override
//     @Transactional
//     public UserPreferenceResponse removeFavoriteGenre(Integer userId, Integer genreId) {
//         log.info("Eliminando género favorito {} del usuario {}", genreId, userId);
        
//         UserPreference preference = getOrCreatePreference(userId);
        
//         boolean removed = preference.getFavoriteGenres()
//                 .removeIf(g -> g.getGenreId().equals(genreId));
        
//         if (!removed) {
//             throw new RuntimeException("El género no está en tus favoritos");
//         }
        
//         preference = preferenceRepository.save(preference);
        
//         log.info("Género eliminado exitosamente. Total de favoritos: {}", 
//                 preference.getFavoriteGenres().size());
        
//         return convertToDto(preference);
//     }
    
//     @Override
//     @Transactional(readOnly = true)
//     public List<GenreDto> getFavoriteGenres(Integer userId) {
//         log.info("Obteniendo géneros favoritos del usuario: {}", userId);
        
//         UserPreference preference = preferenceRepository.findByUser_UserId(userId)
//                 .orElseThrow(() -> new RuntimeException("Preferencias no encontradas"));
        
//         return preference.getFavoriteGenres().stream()
//                 .map(this::convertGenreToDto)
//                 .collect(Collectors.toList());
//     }
    
//     @Override
//     @Transactional(readOnly = true)
//     public List<GenreDto> getAllGenres() {
//         log.info("Obteniendo todos los géneros disponibles");
        
//         return genreRepository.findAll().stream()
//                 .map(this::convertGenreToDto)
//                 .collect(Collectors.toList());
//     }
    
//     // ===== MÉTODOS AUXILIARES =====
    
//     private UserPreference getOrCreatePreference(Integer userId) {
//         return preferenceRepository.findByUser_UserId(userId)
//                 .orElseGet(() -> createDefaultPreferences(userId));
//     }
    
//     private UserPreference createDefaultPreferences(Integer userId) {
//         log.info("Creando preferencias por defecto para usuario: {}", userId);
        
//         User user = userRepository.findById(userId)
//                 .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
        
//         UserPreference preference = UserPreference.builder()
//                 .user(user)
//                 .searchRadiusKm(BigDecimal.valueOf(25.0))
//                 .emailNotifications(true)
//                 .pushNotifications(true)
//                 .build();
        
//         return preferenceRepository.save(preference);
//     }
    
//     private void validateSearchRadius(BigDecimal radius) {
//         if (radius.compareTo(BigDecimal.valueOf(5.0)) < 0 || 
//             radius.compareTo(BigDecimal.valueOf(50.0)) > 0) {
//             throw new RuntimeException("El radio de búsqueda debe estar entre 5 y 50 km");
//         }
//     }
    
//     // ===== MÉTODOS DE CONVERSIÓN =====
    
//     private UserPreferenceResponse convertToDto(UserPreference preference) {
//         return UserPreferenceResponse.builder()
//                 .preferenceId(preference.getPreferenceId())
//                 .userId(preference.getUser().getUserId())
//                 .searchRadiusKm(preference.getSearchRadiusKm())
//                 .emailNotifications(preference.getEmailNotifications())
//                 .pushNotifications(preference.getPushNotifications())
//                 .favoriteArtists(preference.getFavoriteArtists().stream()
//                         .map(this::convertArtistToDto)
//                         .collect(Collectors.toList()))
//                 .favoriteGenres(preference.getFavoriteGenres().stream()
//                         .map(this::convertGenreToDto)
//                         .collect(Collectors.toList()))
//                 .favoriteArtistsCount(preference.getFavoriteArtists().size())
//                 .favoriteGenresCount(preference.getFavoriteGenres().size())
//                 .build();
//     }
    
//     private ArtistDto convertArtistToDto(Artist artist) {
//         return ArtistDto.builder()
//                 .artistId(artist.getArtistId())
//                 .spotifyId(artist.getSpotifyId())
//                 .name(artist.getName())
//                 .imageUrl(artist.getImageUrl())
//                 .popularity(artist.getPopularity())
//                 .followers(artist.getFollowers())
//                 .genres(artist.getGenres().stream()
//                         .map(Genre::getName)
//                         .collect(Collectors.toList()))
//                 .build();
//     }
    
    
// }