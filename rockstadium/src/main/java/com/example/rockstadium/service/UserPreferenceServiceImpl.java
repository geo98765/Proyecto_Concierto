package com.example.rockstadium.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.rockstadium.dto.AddFavoriteGenreRequest;
import com.example.rockstadium.dto.ArtistResponse;
import com.example.rockstadium.dto.DeleteFavoriteGenreRequest;
import com.example.rockstadium.dto.MusicGenreResponse;
import com.example.rockstadium.dto.SuccessResponse;
import com.example.rockstadium.dto.UserPreferenceBasicResponse;
import com.example.rockstadium.dto.UserPreferenceRequest;
import com.example.rockstadium.dto.UserPreferenceResponse;
import com.example.rockstadium.mapper.UserPreferenceMapper;
import com.example.rockstadium.model.Artist;
import com.example.rockstadium.model.FavoriteArtist;
import com.example.rockstadium.model.FavoriteGenre;
import com.example.rockstadium.model.MusicGenre;
import com.example.rockstadium.model.Profile;
import com.example.rockstadium.model.User;
import com.example.rockstadium.model.UserPreference;
import com.example.rockstadium.repository.ArtistRepository;
import com.example.rockstadium.repository.FavoriteArtistRepository;
import com.example.rockstadium.repository.FavoriteGenreRepository;
import com.example.rockstadium.repository.MusicGenreRepository;
import com.example.rockstadium.repository.ProfileRepository;
import com.example.rockstadium.repository.UserPreferenceRepository;
import com.example.rockstadium.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    private final UserPreferenceMapper mapper;
    private final UserRepository userRepository; 

    private static final int MAX_FAVORITE_ARTISTS = 40;
    private static final int MAX_FAVORITE_GENRES = 30;
    private static final BigDecimal DEFAULT_SEARCH_RADIUS = BigDecimal.valueOf(25.0);
    
    // ===== SEARCH PREFERENCES =====
    
    @Override
    @Transactional
    public UserPreferenceBasicResponse createOrUpdatePreferences(Integer userId, UserPreferenceRequest request) {
        log.info("Configuring preferences for user: {}", userId);
        
        // CORRECCIÓN: Primero buscar el profile para asegurar que existe
        Profile profile = getProfileByUserId(userId);
        
        // Luego validar los permisos de acceso
        validateUserOwnershipWithProfile(userId, profile);
        
        UserPreference preference = getOrCreateUserPreference(profile);
        
        // Actualizar el radio de búsqueda si está presente en la petición
        if (request.getSearchRadiusKm() != null) {
            preference.setSearchRadius(request.getSearchRadiusKm());
        }
        
        // Actualizar las notificaciones si están presentes en la petición
        if (request.getEmailNotifications() != null) {
            preference.setEmailNotifications(request.getEmailNotifications());
        }
        
        preference = userPreferenceRepository.save(preference);
        log.info("✅ Preferences updated for user {}", userId);
        
        // Devolver solo los campos básicos usando el nuevo DTO
        return mapper.toBasicResponse(preference);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserPreferenceResponse getPreferences(Integer userId, boolean includeFullLists) {
        log.info("Getting preferences for user: {} (includeLists: {})", userId, includeFullLists);
        
        // CORRECCIÓN: Primero buscar el profile para asegurar que existe
        Profile profile = getProfileByUserId(userId);
        
        // Luego validar los permisos de acceso
        validateUserOwnershipWithProfile(userId, profile);
        
        UserPreference preference = userPreferenceRepository
                .findByProfileProfileId(profile.getProfileId())
                .orElseGet(() -> createDefaultPreferenceTransactional(profile));
        
        if (includeFullLists) {
            // Retornar respuesta completa con todos los artistas y géneros
            return buildPreferenceResponse(profile, preference);
        } else {
            // Retornar resumen sin listas (solo conteos)
            return buildPreferenceSummary(profile, preference);
        }
    }
    
    // ===== FAVORITE ARTISTS =====
    
    @Override
    @Transactional
    public ArtistResponse addFavoriteArtist(Integer userId, String spotifyId) {
        log.info("➕ Adding favorite artist {} for user {}", spotifyId, userId);
        
        // CORRECCIÓN: Primero buscar el profile para asegurar que existe
        Profile profile = getProfileByUserId(userId);
        
        // Luego validar los permisos de acceso
        validateUserOwnershipWithProfile(userId, profile);
        
        // Verificar límite
        long currentCount = favoriteArtistRepository.countByProfileProfileId(profile.getProfileId());
        if (currentCount >= MAX_FAVORITE_ARTISTS) {
            throw new IllegalStateException(
                String.format("You have reached the limit of %d favorite artists", MAX_FAVORITE_ARTISTS)
            );
        }
        
        // Obtener o crear artista desde Spotify
        Artist artist = getOrCreateArtistFromSpotify(spotifyId);
        
        // Verificar si ya es favorito
        if (favoriteArtistRepository.existsByProfileProfileIdAndArtistArtistId(
                profile.getProfileId(), artist.getArtistId())) {
            throw new IllegalStateException("This artist is already in your favorites");
        }
        
        // Crear relación
        FavoriteArtist favoriteArtist = FavoriteArtist.builder()
                .profile(profile)
                .artist(artist)
                .build();
        
        favoriteArtistRepository.save(favoriteArtist);
        log.info("✅ Artist added to favorites. Total: {}", currentCount + 1);
        
        // Retornar el artista enriquecido con datos de Spotify
        return enrichArtistWithSpotifyData(artist);
    }
    
    @Override
    @Transactional
    public SuccessResponse removeFavoriteArtist(Integer userId, Integer artistId) {
        log.info("➖ Removing favorite artist {} from user {}", artistId, userId);
        
        // CORRECCIÓN: Primero buscar el profile para asegurar que existe
        Profile profile = getProfileByUserId(userId);
        
        // Luego validar los permisos de acceso
        validateUserOwnershipWithProfile(userId, profile);
        
        // Obtener nombre del artista antes de eliminar
        FavoriteArtist favoriteArtist = favoriteArtistRepository
                .findByProfileProfileIdAndArtistArtistId(profile.getProfileId(), artistId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Favorite artist not found with artistId: '%s'", artistId)
                ));
        
        String artistName = favoriteArtist.getArtist().getName();
        
        favoriteArtistRepository.deleteByProfileProfileIdAndArtistArtistId(
                profile.getProfileId(), artistId);
        
        log.info("✅ Artist '{}' removed from favorites", artistName);
        
        return SuccessResponse.of("Artist removed successfully", artistName);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ArtistResponse> getFavoriteArtists(Integer userId, Pageable pageable) {
        log.info("Getting favorite artists for user: {} (page: {}, size: {})", 
                userId, pageable.getPageNumber(), pageable.getPageSize());
        
        // CORRECCIÓN: Primero buscar el profile para asegurar que existe
        Profile profile = getProfileByUserId(userId);
        
        // Luego validar los permisos de acceso
        validateUserOwnershipWithProfile(userId, profile);
        
        Page<FavoriteArtist> favoritesPage = favoriteArtistRepository
                .findByProfileProfileId(profile.getProfileId(), pageable);
        
        // Enriquecer cada artista con datos de Spotify (lógica de negocio en el servicio)
        return favoritesPage.map(fa -> enrichArtistWithSpotifyData(fa.getArtist()));
    }
    
    // ===== FAVORITE GENRES =====
    
    @Override
    @Transactional
    public MusicGenreResponse addFavoriteGenre(Integer userId, AddFavoriteGenreRequest request) {
        log.info("➕ Adding favorite genre for user {}: {}", userId, request);
        
        // CORRECCIÓN: Primero buscar el profile para asegurar que existe
        Profile profile = getProfileByUserId(userId);
        
        // Luego validar los permisos de acceso
        validateUserOwnershipWithProfile(userId, profile);
        
        // Validar request
        if (!request.isValid()) {
            throw new IllegalArgumentException("Either genreId or genreName must be provided");
        }
        
        // Verificar límite
        long currentCount = favoriteGenreRepository.countByProfileProfileId(profile.getProfileId());
        if (currentCount >= MAX_FAVORITE_GENRES) {
            throw new IllegalStateException(
                String.format("You have reached the limit of %d favorite genres", MAX_FAVORITE_GENRES)
            );
        }
        
        // Buscar género usando helper method
        MusicGenre genre = findGenreByIdOrName(request);
        
        // Verificar si ya es favorito
        if (favoriteGenreRepository.existsByProfileProfileIdAndMusicGenreMusicGenreId(
                profile.getProfileId(), genre.getMusicGenreId())) {
            throw new IllegalStateException("This genre is already in your favorites");
        }
        
        // Crear relación
        FavoriteGenre favoriteGenre = FavoriteGenre.builder()
                .profile(profile)
                .musicGenre(genre)
                .build();
        
        favoriteGenreRepository.save(favoriteGenre);
        log.info("✅ Genre '{}' added to favorites. Total: {}", genre.getName(), currentCount + 1);
        
        return mapper.toGenreResponse(genre);
    }
    
    @Override
    @Transactional
    public SuccessResponse removeFavoriteGenre(Integer userId, DeleteFavoriteGenreRequest request) {
        log.info("➖ Removing favorite genre from user {}: {}", userId, request);
        
        // CORRECCIÓN: Primero buscar el profile para asegurar que existe
        Profile profile = getProfileByUserId(userId);
        
        // Luego validar los permisos de acceso
        validateUserOwnershipWithProfile(userId, profile);
        
        // Validar request
        if (!request.isValid()) {
            throw new IllegalArgumentException("Either genreId or genreName must be provided");
        }
        
        // Buscar género usando helper method
        MusicGenre genre = findGenreByIdOrName(request);
        
        // Verificar que el género está en favoritos
        FavoriteGenre favoriteGenre = favoriteGenreRepository
                .findByProfileProfileIdAndMusicGenreMusicGenreId(
                        profile.getProfileId(), genre.getMusicGenreId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Favorite genre not found with %s: '%s'",
                                request.getGenreId() != null ? "genreId" : "genreName",
                                request.getGenreId() != null ? request.getGenreId() : request.getGenreName())
                ));
        
        String genreName = favoriteGenre.getMusicGenre().getName();
        
        favoriteGenreRepository.deleteByProfileProfileIdAndMusicGenreMusicGenreId(
                profile.getProfileId(), genre.getMusicGenreId());
        
        log.info("✅ Genre '{}' removed from favorites", genreName);
        
        return SuccessResponse.of("Genre removed successfully", genreName);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<MusicGenreResponse> getFavoriteGenres(Integer userId, Pageable pageable) {
        log.info("Getting favorite genres for user: {} (page: {}, size: {})", 
                userId, pageable.getPageNumber(), pageable.getPageSize());
        
        // CORRECCIÓN: Primero buscar el profile para asegurar que existe
        Profile profile = getProfileByUserId(userId);
        
        // Luego validar los permisos de acceso
        validateUserOwnershipWithProfile(userId, profile);
        
        Page<FavoriteGenre> favoritesPage = favoriteGenreRepository
                .findByProfileProfileId(profile.getProfileId(), pageable);
        
        return favoritesPage.map(fg -> mapper.toGenreResponse(fg.getMusicGenre()));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MusicGenreResponse> getAllGenres() {
        log.info("Getting all available genres");
        
        return musicGenreRepository.findAll().stream()
                .map(mapper::toGenreResponse)
                .collect(Collectors.toList());
    }
    
    // ===== HELPER METHODS =====
    
    /**
     * Valida que el usuario autenticado sea el dueño del recurso o sea ADMIN
     * DEPRECATED: Usar validateUserOwnershipWithProfile cuando ya se tiene el Profile cargado
     * 
     * @param userId ID del usuario propietario del recurso
     * @throws IllegalStateException si el usuario no tiene permiso
     */
    private void validateUserOwnership(Integer userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }
        
        // Obtener el email del usuario autenticado
        String authenticatedEmail = authentication.getName();
        
        // Verificar si el usuario autenticado es ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        // Si es ADMIN, tiene permiso total
        if (isAdmin) {
            log.debug("Admin user accessing preferences for user ID: {}", userId);
            return;
        }
        
        // Si no es ADMIN, verificar que sea el propietario
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("User not found with id: '%s'", userId)
                ));
        
        if (!targetUser.getEmail().equals(authenticatedEmail)) {
            log.warn("Unauthorized access attempt by user: {} for user ID: {}", 
                    authenticatedEmail, userId);
            throw new IllegalStateException("You don't have permission to access this resource");
        }
    }
    
    /**
     * Valida que el usuario autenticado sea el dueño del recurso o sea ADMIN
     * Versión optimizada que recibe el Profile ya cargado para evitar consultas duplicadas
     * 
     * @param userId ID del usuario propietario del recurso
     * @param profile Profile que ya fue cargado de la base de datos
     * @throws IllegalStateException si el usuario no tiene permiso
     */
    private void validateUserOwnershipWithProfile(Integer userId, Profile profile) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }
        
        // Obtener el email del usuario autenticado
        String authenticatedEmail = authentication.getName();
        
        // Verificar si el usuario autenticado es ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        // Si es ADMIN, tiene permiso total
        if (isAdmin) {
            log.debug("✅ Admin user accessing preferences for user ID: {}", userId);
            return;
        }
        
        // Si no es ADMIN, verificar que sea el propietario
        // El profile ya tiene el user cargado con la relación
        String targetEmail = profile.getUser().getEmail();
        
        if (!targetEmail.equals(authenticatedEmail)) {
            log.warn("❌ Unauthorized access attempt by user: {} for user ID: {}", 
                    authenticatedEmail, userId);
            throw new IllegalStateException("You don't have permission to access this resource");
        }
        
        log.debug("✅ User validated: {} accessing own preferences (ID: {})", 
                authenticatedEmail, userId);
    }
   
    /**
     * Get profile by user ID or throw exception
     * Obtiene el perfil por ID de usuario o lanza excepción
     */
    private Profile getProfileByUserId(Integer userId) {
        return profileRepository.findByUserUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Profile not found with userId: '%s'", userId)
                ));
    }
    
    /**
     * Get existing preference or create new one (for transactional methods)
     * Obtiene la preferencia existente o crea una nueva (para métodos transaccionales)
     */
    private UserPreference getOrCreateUserPreference(Profile profile) {
        return userPreferenceRepository.findByProfileProfileId(profile.getProfileId())
                .orElseGet(() -> createDefaultPreference(profile));
    }
    
    /**
     * Create default preference and save to database
     * Crea preferencia por defecto y guarda en la base de datos
     */
    private UserPreference createDefaultPreference(Profile profile) {
        log.info("Creating default preferences for profile: {}", profile.getProfileId());
        
        UserPreference preference = UserPreference.builder()
                .profile(profile)
                .searchRadius(DEFAULT_SEARCH_RADIUS)
                .emailNotifications(true)
                .build();
        
        return userPreferenceRepository.save(preference);
    }
    
    /**
     * Create default preference in separate transaction (for read-only methods)
     * Crea preferencia por defecto en transacción separada (para métodos de solo lectura)
     */
    @Transactional
    protected UserPreference createDefaultPreferenceTransactional(Profile profile) {
        return createDefaultPreference(profile);
    }
    
    /**
     * Build complete preference response with all related data
     * Construye la respuesta completa de preferencias con todos los datos relacionados
     */
    private UserPreferenceResponse buildPreferenceResponse(Profile profile, UserPreference preference) {
        List<FavoriteArtist> favoriteArtists = 
            favoriteArtistRepository.findByProfileProfileId(profile.getProfileId());
        List<FavoriteGenre> favoriteGenres = 
            favoriteGenreRepository.findByProfileProfileId(profile.getProfileId());
        
        // Enriquecer artistas con datos de Spotify (lógica de negocio en el servicio)
        List<ArtistResponse> artistResponses = favoriteArtists.stream()
                .map(fa -> enrichArtistWithSpotifyData(fa.getArtist()))
                .collect(Collectors.toList());
        
        // Mapeo simple de géneros
        List<MusicGenreResponse> genreResponses = favoriteGenres.stream()
                .map(fg -> mapper.toGenreResponse(fg.getMusicGenre()))
                .collect(Collectors.toList());
        
        return mapper.toResponse(preference, artistResponses, genreResponses);
    }
    
    /**
     * Build preference summary without full lists (only counts)
     * Construye el resumen de preferencias sin listas completas (solo conteos)
     */
    private UserPreferenceResponse buildPreferenceSummary(Profile profile, UserPreference preference) {
        long artistsCount = favoriteArtistRepository.countByProfileProfileId(profile.getProfileId());
        long genresCount = favoriteGenreRepository.countByProfileProfileId(profile.getProfileId());
        
        return UserPreferenceResponse.builder()
                .userPreferenceId(preference.getUserPreferenceId())
                .profileId(profile.getProfileId())
                .searchRadius(preference.getSearchRadius())
                .emailNotifications(preference.getEmailNotifications())
                .favoriteArtists(Collections.emptyList())
                .favoriteGenres(Collections.emptyList())
                .favoriteArtistsCount((int) artistsCount)
                .favoriteGenresCount((int) genresCount)
                .maxFavoriteArtists(MAX_FAVORITE_ARTISTS)
                .maxFavoriteGenres(MAX_FAVORITE_GENRES)
                .build();
    }
    
    /**
     * Get or create artist from Spotify
     * Obtiene o crea un artista desde Spotify
     */
    private Artist getOrCreateArtistFromSpotify(String spotifyId) {
        return artistRepository.findBySpotifyId(spotifyId)
                .orElseGet(() -> createArtistFromSpotify(spotifyId));
    }
    
    /**
     * Create artist from Spotify data
     * Crea un artista desde datos de Spotify
     */
    private Artist createArtistFromSpotify(String spotifyId) {
        log.info("Creating new artist from Spotify: {}", spotifyId);
        
        try {
            ArtistResponse spotifyArtist = spotifyService.getArtistById(spotifyId);
            
            Artist artist = Artist.builder()
                    .spotifyId(spotifyArtist.getSpotifyId())
                    .name(spotifyArtist.getName())
                    .build();
            
            return artistRepository.save(artist);
        } catch (Exception e) {
            log.error("Failed to create artist from Spotify: {}", spotifyId, e);
            throw new IllegalStateException(
                "Unable to fetch artist information from Spotify. Please try again later."
            );
        }
    }
    
    /**
     * Find genre by ID or name
     * Busca género por ID o nombre
     * 
     * @param request Request with either genreId or genreName
     * @return The found genre
     * @throws EntityNotFoundException if genre not found
     */
    private MusicGenre findGenreByIdOrName(AddFavoriteGenreRequest request) {
        // Primero buscar por ID si está proporcionado
        if (request.getGenreId() != null) {
            return musicGenreRepository.findById(request.getGenreId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Genre not found with id: '%s'", request.getGenreId())
                    ));
        }
        
        // Luego buscar por nombre si está proporcionado
        if (request.getGenreName() != null && !request.getGenreName().trim().isEmpty()) {
            String genreName = request.getGenreName().trim();
            return musicGenreRepository.findByNameIgnoreCase(genreName)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Genre not found with name: '%s'", genreName)
                    ));
        }
        
        // Esto no debería suceder debido a la validación, pero por si acaso
        throw new IllegalArgumentException("Either genreId or genreName must be provided");
    }
    
    /**
     * Find genre by ID or name (for delete operations)
     * Busca género por ID o nombre (para operaciones de eliminación)
     * 
     * @param request Request with either genreId or genreName
     * @return The found genre
     * @throws EntityNotFoundException if genre not found
     */
    private MusicGenre findGenreByIdOrName(DeleteFavoriteGenreRequest request) {
        // Primero buscar por ID si está proporcionado
        if (request.getGenreId() != null) {
            return musicGenreRepository.findById(request.getGenreId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Genre not found with id: '%s'", request.getGenreId())
                    ));
        }
        
        // Luego buscar por nombre si está proporcionado
        if (request.getGenreName() != null && !request.getGenreName().trim().isEmpty()) {
            String genreName = request.getGenreName().trim();
            return musicGenreRepository.findByNameIgnoreCase(genreName)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Genre not found with name: '%s'", genreName)
                    ));
        }
        
        // Esto no debería suceder debido a la validación, pero por si acaso
        throw new IllegalArgumentException("Either genreId or genreName must be provided");
    }
    
    /**
     * Enrich artist with Spotify data if available
     * Enriquece el artista con datos de Spotify si están disponibles
     * 
     * This is business logic and belongs in the Service layer, not in the Mapper
     * 
     * @param artist The artist entity from database
     * @return ArtistResponse enriched with Spotify data, or basic data if Spotify fails
     */
    private ArtistResponse enrichArtistWithSpotifyData(Artist artist) {
        // Intentar obtener datos frescos de Spotify
        if (artist.getSpotifyId() != null) {
            try {
                return spotifyService.getArtistById(artist.getSpotifyId());
            } catch (Exception e) {
                log.warn("Failed to fetch Spotify info for artist: {} - Error: {}", 
                    artist.getSpotifyId(), e.getMessage());
            }
        }
        
        // Fallback: retornar datos básicos de la BD usando el mapper
        return mapper.toArtistResponse(artist);
    }
}