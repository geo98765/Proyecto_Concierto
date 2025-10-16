package com.example.rockStadium.service;

import com.example.rockStadium.dto.*;
import com.example.rockStadium.model.*;
import com.example.rockStadium.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPreferenceServiceImpl implements UserPreferenceService {
    
    private final UserPreferenceRepository userPreferenceRepository;
    private final ProfileRepository profileRepository;
    private final ArtistRepository artistRepository;
    private final FavoriteArtistRepository favoriteArtistRepository;
    private final FavoriteGenreRepository favoriteGenreRepository;
    private final MusicGenreRepository musicGenreRepository;
    private final SpotifyService spotifyService;
    
    private static final int MAX_FAVORITE_ARTISTS = 50;
    private static final int MAX_FAVORITE_GENRES = 10;
    private static final BigDecimal MIN_SEARCH_RADIUS = BigDecimal.valueOf(5.0);
    private static final BigDecimal MAX_SEARCH_RADIUS = BigDecimal.valueOf(50.0);
    
    // ===== PREFERENCIAS DE BÚSQUEDA =====
    
    @Override
    @Transactional
    public UserPreferenceResponse createOrUpdatePreferences(Integer userId, UserPreferenceRequest request) {
        log.info("Configurando preferencias para usuario: {}", userId);
        
        Profile profile = getProfileByUserId(userId);
        UserPreference preference = getOrCreateUserPreference(profile);
        
        // Validar y actualizar radio de búsqueda
        if (request.getSearchRadiusKm() != null) {
            validateSearchRadius(request.getSearchRadiusKm());
            preference.setSearchRadius(request.getSearchRadiusKm());
        }
        
        // Actualizar notificaciones
        if (request.getEmailNotifications() != null) {
            preference.setEmailNotifications(request.getEmailNotifications());
        }
        
        preference = userPreferenceRepository.save(preference);
        log.info("✅ Preferencias actualizadas para usuario {}", userId);
        
        return buildPreferenceResponse(profile, preference);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserPreferenceResponse getPreferences(Integer userId) {
        log.info("Obteniendo preferencias del usuario: {}", userId);
        
        Profile profile = getProfileByUserId(userId);
        UserPreference preference = getOrCreateUserPreference(profile);
        
        return buildPreferenceResponse(profile, preference);
    }
    
    // ===== ARTISTAS FAVORITOS =====
    
    @Override
    @Transactional
    public UserPreferenceResponse addFavoriteArtist(Integer userId, String spotifyId) {
        log.info("➕ Agregando artista favorito {} para usuario {}", spotifyId, userId);
        
        Profile profile = getProfileByUserId(userId);
        
        // Verificar límite
        long currentCount = favoriteArtistRepository.countByProfileProfileId(profile.getProfileId());
        if (currentCount >= MAX_FAVORITE_ARTISTS) {
            throw new RuntimeException(String.format("Has alcanzado el límite de %d artistas favoritos", MAX_FAVORITE_ARTISTS));
        }
        
        // Obtener o crear artista desde Spotify
        Artist artist = getOrCreateArtistFromSpotify(spotifyId);
        
        // Verificar si ya es favorito
        if (favoriteArtistRepository.existsByProfileProfileIdAndArtistArtistId(
                profile.getProfileId(), artist.getArtistId())) {
            throw new RuntimeException("Este artista ya está en tus favoritos");
        }
        
        // Crear relación
        FavoriteArtist favoriteArtist = FavoriteArtist.builder()
                .profile(profile)
                .artist(artist)
                .build();
        
        favoriteArtistRepository.save(favoriteArtist);
        log.info("✅ Artista agregado a favoritos. Total: {}", currentCount + 1);
        
        UserPreference preference = getOrCreateUserPreference(profile);
        return buildPreferenceResponse(profile, preference);
    }
    
    @Override
    @Transactional
    public UserPreferenceResponse removeFavoriteArtist(Integer userId, Integer artistId) {
        log.info("➖ Eliminando artista favorito {} del usuario {}", artistId, userId);
        
        Profile profile = getProfileByUserId(userId);
        
        // Verificar que existe la relación
        if (!favoriteArtistRepository.existsByProfileProfileIdAndArtistArtistId(
                profile.getProfileId(), artistId)) {
            throw new RuntimeException("Este artista no está en tus favoritos");
        }
        
        favoriteArtistRepository.deleteByProfileProfileIdAndArtistArtistId(
                profile.getProfileId(), artistId);
        
        log.info("✅ Artista eliminado de favoritos");
        
        UserPreference preference = getOrCreateUserPreference(profile);
        return buildPreferenceResponse(profile, preference);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ArtistResponse> getFavoriteArtists(Integer userId) {
        log.info("Obteniendo artistas favoritos del usuario: {}", userId);
        
        Profile profile = getProfileByUserId(userId);
        List<FavoriteArtist> favorites = favoriteArtistRepository.findByProfileProfileId(profile.getProfileId());
        
        return favorites.stream()
                .map(fa -> convertArtistToResponse(fa.getArtist()))
                .collect(Collectors.toList());
    }
    
    // ===== GÉNEROS FAVORITOS =====
    
    @Override
    @Transactional
    public UserPreferenceResponse addFavoriteGenre(Integer userId, Integer genreId) {
        log.info("➕ Agregando género favorito {} para usuario {}", genreId, userId);
        
        Profile profile = getProfileByUserId(userId);
        
        // Verificar límite
        long currentCount = favoriteGenreRepository.countByProfileProfileId(profile.getProfileId());
        if (currentCount >= MAX_FAVORITE_GENRES) {
            throw new RuntimeException(String.format("Has alcanzado el límite de %d géneros favoritos", MAX_FAVORITE_GENRES));
        }
        
        // Verificar que el género existe
        MusicGenre genre = musicGenreRepository.findById(genreId)
                .orElseThrow(() -> new RuntimeException("Género no encontrado con ID: " + genreId));
        
        // Verificar si ya es favorito
        if (favoriteGenreRepository.existsByProfileProfileIdAndMusicGenreMusicGenreId(
                profile.getProfileId(), genreId)) {
            throw new RuntimeException("Este género ya está en tus favoritos");
        }
        
        // Crear relación
        FavoriteGenre favoriteGenre = FavoriteGenre.builder()
                .profile(profile)
                .musicGenre(genre)
                .build();
        
        favoriteGenreRepository.save(favoriteGenre);
        log.info("✅ Género agregado a favoritos. Total: {}", currentCount + 1);
        
        UserPreference preference = getOrCreateUserPreference(profile);
        return buildPreferenceResponse(profile, preference);
    }
    
    @Override
    @Transactional
    public UserPreferenceResponse removeFavoriteGenre(Integer userId, Integer genreId) {
        log.info("➖ Eliminando género favorito {} del usuario {}", genreId, userId);
        
        Profile profile = getProfileByUserId(userId);
        
        // Verificar que existe la relación
        if (!favoriteGenreRepository.existsByProfileProfileIdAndMusicGenreMusicGenreId(
                profile.getProfileId(), genreId)) {
            throw new RuntimeException("Este género no está en tus favoritos");
        }
        
        favoriteGenreRepository.deleteByProfileProfileIdAndMusicGenreMusicGenreId(
                profile.getProfileId(), genreId);
        
        log.info("✅ Género eliminado de favoritos");
        
        UserPreference preference = getOrCreateUserPreference(profile);
        return buildPreferenceResponse(profile, preference);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MusicGenreResponse> getFavoriteGenres(Integer userId) {
        log.info("Obteniendo géneros favoritos del usuario: {}", userId);
        
        Profile profile = getProfileByUserId(userId);
        List<FavoriteGenre> favorites = favoriteGenreRepository.findByProfileProfileId(profile.getProfileId());
        
        return favorites.stream()
                .map(fg -> convertGenreToResponse(fg.getMusicGenre()))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MusicGenreResponse> getAllGenres() {
        log.info("Obteniendo todos los géneros disponibles");
        
        return musicGenreRepository.findAll().stream()
                .map(this::convertGenreToResponse)
                .collect(Collectors.toList());
    }
    
    // ===== MÉTODOS AUXILIARES =====
    
    private Profile getProfileByUserId(Integer userId) {
        return profileRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado para usuario: " + userId));
    }
    
    private UserPreference getOrCreateUserPreference(Profile profile) {
        return profile.getUserPreference() != null 
                ? profile.getUserPreference()
                : createDefaultPreference(profile);
    }
    
    private UserPreference createDefaultPreference(Profile profile) {
        log.info("Creando preferencias por defecto para perfil: {}", profile.getProfileId());
        
        UserPreference preference = UserPreference.builder()
                .profile(profile)
                .searchRadius(BigDecimal.valueOf(25.0))
                .emailNotifications(true)
                .build();
        
        return userPreferenceRepository.save(preference);
    }
    
    private void validateSearchRadius(BigDecimal radius) {
        if (radius.compareTo(MIN_SEARCH_RADIUS) < 0) {
            throw new RuntimeException("El radio de búsqueda mínimo es " + MIN_SEARCH_RADIUS + " km");
        }
        if (radius.compareTo(MAX_SEARCH_RADIUS) > 0) {
            throw new RuntimeException("El radio de búsqueda máximo es " + MAX_SEARCH_RADIUS + " km");
        }
    }
    
    private Artist getOrCreateArtistFromSpotify(String spotifyId) {
        // Buscar si ya existe
        return artistRepository.findBySpotifyId(spotifyId)
                .orElseGet(() -> createArtistFromSpotify(spotifyId));
    }
    
    private Artist createArtistFromSpotify(String spotifyId) {
        log.info("Creando nuevo artista desde Spotify: {}", spotifyId);
        
        ArtistResponse spotifyArtist = spotifyService.getArtistById(spotifyId);
        
        Artist artist = Artist.builder()
                .spotifyId(spotifyArtist.getSpotifyId())
                .name(spotifyArtist.getName())
                .build();
        
        return artistRepository.save(artist);
    }
    
    private UserPreferenceResponse buildPreferenceResponse(Profile profile, UserPreference preference) {
        List<FavoriteArtist> favoriteArtists = favoriteArtistRepository.findByProfileProfileId(profile.getProfileId());
        List<FavoriteGenre> favoriteGenres = favoriteGenreRepository.findByProfileProfileId(profile.getProfileId());
        
        return UserPreferenceResponse.builder()
                .userPreferenceId(preference.getUserPreferenceId())
                .profileId(profile.getProfileId())
                .searchRadius(preference.getSearchRadius())
                .emailNotifications(preference.getEmailNotifications())
                .favoriteArtists(favoriteArtists.stream()
                        .map(fa -> convertArtistToResponse(fa.getArtist()))
                        .collect(Collectors.toList()))
                .favoriteGenres(favoriteGenres.stream()
                        .map(fg -> convertGenreToResponse(fg.getMusicGenre()))
                        .collect(Collectors.toList()))
                .favoriteArtistsCount(favoriteArtists.size())
                .favoriteGenresCount(favoriteGenres.size())
                .maxFavoriteArtists(MAX_FAVORITE_ARTISTS)
                .maxFavoriteGenres(MAX_FAVORITE_GENRES)
                .build();
    }
    
    private ArtistResponse convertArtistToResponse(Artist artist) {
        // Si el artista tiene Spotify ID, obtener info actualizada
        if (artist.getSpotifyId() != null) {
            try {
                return spotifyService.getArtistById(artist.getSpotifyId());
            } catch (Exception e) {
                log.warn("No se pudo obtener info de Spotify para artista: {}", artist.getSpotifyId());
            }
        }
        
        // Fallback: usar datos básicos de la BD
        return ArtistResponse.builder()
                .spotifyId(artist.getSpotifyId())
                .name(artist.getName())
                .build();
    }
    
    private MusicGenreResponse convertGenreToResponse(MusicGenre genre) {
        return MusicGenreResponse.builder()
                .musicGenreId(genre.getMusicGenreId())
                .name(genre.getName())
                .description(genre.getDescription())
                .build();
    }
}