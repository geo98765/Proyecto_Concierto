package com.example.rockStadium.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.rockStadium.dto.AddFavoriteGenreRequest;
import com.example.rockStadium.dto.ArtistResponse;
import com.example.rockStadium.dto.DeleteFavoriteGenreRequest;
import com.example.rockStadium.dto.MusicGenreResponse;
import com.example.rockStadium.dto.SuccessResponse;
import com.example.rockStadium.dto.UserPreferenceRequest;
import com.example.rockStadium.dto.UserPreferenceResponse;
import com.example.rockStadium.exception.BusinessRuleException;
import com.example.rockStadium.exception.ResourceNotFoundException;
import com.example.rockStadium.mapper.UserPreferenceMapper;
import com.example.rockStadium.model.Artist;
import com.example.rockStadium.model.FavoriteArtist;
import com.example.rockStadium.model.FavoriteGenre;
import com.example.rockStadium.model.MusicGenre;
import com.example.rockStadium.model.Profile;
import com.example.rockStadium.model.UserPreference;
import com.example.rockStadium.repository.ArtistRepository;
import com.example.rockStadium.repository.FavoriteArtistRepository;
import com.example.rockStadium.repository.FavoriteGenreRepository;
import com.example.rockStadium.repository.MusicGenreRepository;
import com.example.rockStadium.repository.ProfileRepository;
import com.example.rockStadium.repository.UserPreferenceRepository;

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
    
    private static final int MAX_FAVORITE_ARTISTS = 50;
    private static final int MAX_FAVORITE_GENRES = 10;
    private static final BigDecimal DEFAULT_SEARCH_RADIUS = BigDecimal.valueOf(25.0);
    
    // ===== SEARCH PREFERENCES =====
    
    @Override
    @Transactional
    public UserPreferenceResponse createOrUpdatePreferences(Integer userId, UserPreferenceRequest request) {
        log.info("Configuring preferences for user: {}", userId);
        
        Profile profile = getProfileByUserId(userId);
        UserPreference preference = getOrCreateUserPreference(profile);
        
        // Update search radius
        if (request.getSearchRadiusKm() != null) {
            preference.setSearchRadius(request.getSearchRadiusKm());
        }
        
        // Update notifications
        if (request.getEmailNotifications() != null) {
            preference.setEmailNotifications(request.getEmailNotifications());
        }
        
        preference = userPreferenceRepository.save(preference);
        log.info("✅ Preferences updated for user {}", userId);
        
        return buildPreferenceResponse(profile, preference);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserPreferenceResponse getPreferences(Integer userId, boolean includeFullLists) {
        log.info("Getting preferences for user: {} (includeLists: {})", userId, includeFullLists);
        
        Profile profile = getProfileByUserId(userId);
        UserPreference preference = userPreferenceRepository
                .findByProfileProfileId(profile.getProfileId())
                .orElseGet(() -> createDefaultPreferenceTransactional(profile));
        
        if (includeFullLists) {
            // Return full response with all artists and genres
            return buildPreferenceResponse(profile, preference);
        } else {
            // Return summary without lists (only counts)
            return buildPreferenceSummary(profile, preference);
        }
    }
    
    // ===== FAVORITE ARTISTS =====
    
    @Override
    @Transactional
    public ArtistResponse addFavoriteArtist(Integer userId, String spotifyId) {
        log.info("➕ Adding favorite artist {} for user {}", spotifyId, userId);
        
        Profile profile = getProfileByUserId(userId);
        
        // Check limit
        long currentCount = favoriteArtistRepository.countByProfileProfileId(profile.getProfileId());
        if (currentCount >= MAX_FAVORITE_ARTISTS) {
            throw new BusinessRuleException(
                String.format("You have reached the limit of %d favorite artists", MAX_FAVORITE_ARTISTS)
            );
        }
        
        // Get or create artist from Spotify
        Artist artist = getOrCreateArtistFromSpotify(spotifyId);
        
        // Check if already favorite
        if (favoriteArtistRepository.existsByProfileProfileIdAndArtistArtistId(
                profile.getProfileId(), artist.getArtistId())) {
            throw new BusinessRuleException("This artist is already in your favorites");
        }
        
        // Create relationship
        FavoriteArtist favoriteArtist = FavoriteArtist.builder()
                .profile(profile)
                .artist(artist)
                .build();
        
        favoriteArtistRepository.save(favoriteArtist);
        log.info("✅ Artist added to favorites. Total: {}", currentCount + 1);
        
        // Return only the added artist
        return mapper.toArtistResponse(artist);
    }
    
    @Override
    @Transactional
    public SuccessResponse removeFavoriteArtist(Integer userId, Integer artistId) {
        log.info("➖ Removing favorite artist {} from user {}", artistId, userId);
        
        Profile profile = getProfileByUserId(userId);
        
        // Get artist name before deleting
        FavoriteArtist favoriteArtist = favoriteArtistRepository
                .findByProfileProfileIdAndArtistArtistId(profile.getProfileId(), artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite artist", "artistId", artistId));
        
        String artistName = favoriteArtist.getArtist().getName();
        
        favoriteArtistRepository.deleteByProfileProfileIdAndArtistArtistId(
                profile.getProfileId(), artistId);
        
        log.info("✅ Artist '{}' removed from favorites", artistName);
        
        return SuccessResponse.of("Artist removed successfully", artistName);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ArtistResponse> getFavoriteArtists(Integer userId) {
        log.info("Getting favorite artists for user: {}", userId);
        
        Profile profile = getProfileByUserId(userId);
        List<FavoriteArtist> favorites = favoriteArtistRepository.findByProfileProfileId(profile.getProfileId());
        
        return favorites.stream()
                .map(fa -> mapper.toArtistResponse(fa.getArtist()))
                .collect(Collectors.toList());
    }
    
    // ===== FAVORITE GENRES =====
    
    @Override
    @Transactional
    public MusicGenreResponse addFavoriteGenre(Integer userId, AddFavoriteGenreRequest request) {
        log.info("➕ Adding favorite genre for user {}: {}", userId, request);
        
        // Validate request
        if (!request.isValid()) {
            throw new BusinessRuleException("Either genreId or genreName must be provided");
        }
        
        Profile profile = getProfileByUserId(userId);
        
        // Check limit
        long currentCount = favoriteGenreRepository.countByProfileProfileId(profile.getProfileId());
        if (currentCount >= MAX_FAVORITE_GENRES) {
            throw new BusinessRuleException(
                String.format("You have reached the limit of %d favorite genres", MAX_FAVORITE_GENRES)
            );
        }
        
        // Find genre by ID or name
        MusicGenre genre = findGenreByIdOrName(request);
        
        // Check if already favorite
        if (favoriteGenreRepository.existsByProfileProfileIdAndMusicGenreMusicGenreId(
                profile.getProfileId(), genre.getMusicGenreId())) {
            throw new BusinessRuleException("This genre is already in your favorites");
        }
        
        // Create relationship
        FavoriteGenre favoriteGenre = FavoriteGenre.builder()
                .profile(profile)
                .musicGenre(genre)
                .build();
        
        favoriteGenreRepository.save(favoriteGenre);
        log.info("✅ Genre '{}' added to favorites. Total: {}", genre.getName(), currentCount + 1);
        
        // Return only the added genre
        return mapper.toGenreResponse(genre);
    }
    
    @Override
    @Transactional
    public SuccessResponse removeFavoriteGenre(Integer userId, DeleteFavoriteGenreRequest request) {
        log.info("➖ Removing favorite genre for user {}: {}", userId, request);
        
        // Validate request
        if (!request.isValid()) {
            throw new BusinessRuleException("Either genreId or genreName must be provided");
        }
        
        Profile profile = getProfileByUserId(userId);
        
        // Find genre by ID or name
        MusicGenre genre = findGenreByIdOrName(request);
        
        // Get favorite relationship
        FavoriteGenre favoriteGenre = favoriteGenreRepository
                .findByProfileProfileIdAndMusicGenreMusicGenreId(
                        profile.getProfileId(), genre.getMusicGenreId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Favorite genre", 
                        request.getGenreId() != null ? "genreId" : "genreName",
                        request.getGenreId() != null ? request.getGenreId() : request.getGenreName()
                ));
        
        String genreName = favoriteGenre.getMusicGenre().getName();
        
        favoriteGenreRepository.deleteByProfileProfileIdAndMusicGenreMusicGenreId(
                profile.getProfileId(), genre.getMusicGenreId());
        
        log.info("✅ Genre '{}' removed from favorites", genreName);
        
        return SuccessResponse.of("Genre removed successfully", genreName);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MusicGenreResponse> getFavoriteGenres(Integer userId) {
        log.info("Getting favorite genres for user: {}", userId);
        
        Profile profile = getProfileByUserId(userId);
        List<FavoriteGenre> favorites = favoriteGenreRepository.findByProfileProfileId(profile.getProfileId());
        
        return favorites.stream()
                .map(fg -> mapper.toGenreResponse(fg.getMusicGenre()))
                .collect(Collectors.toList());
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
     * Get profile by user ID or throw exception
     */
    private Profile getProfileByUserId(Integer userId) {
        return profileRepository.findByUserUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "userId", userId));
    }
    
    /**
     * Get existing preference or create new one (for transactional methods)
     */
    private UserPreference getOrCreateUserPreference(Profile profile) {
        return userPreferenceRepository.findByProfileProfileId(profile.getProfileId())
                .orElseGet(() -> createDefaultPreference(profile));
    }
    
    /**
     * Create default preference and save to database
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
     */
    @Transactional
    protected UserPreference createDefaultPreferenceTransactional(Profile profile) {
        return createDefaultPreference(profile);
    }
    
    /**
     * Get or create artist from Spotify
     */
    private Artist getOrCreateArtistFromSpotify(String spotifyId) {
        return artistRepository.findBySpotifyId(spotifyId)
                .orElseGet(() -> createArtistFromSpotify(spotifyId));
    }
    
    /**
     * Create artist from Spotify data
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
            throw new BusinessRuleException(
                "Unable to fetch artist information from Spotify. Please try again later.", e
            );
        }
    }
    
    /**
     * Find genre by ID or name
     * Tries ID first, then name if ID is not provided
     */
    private MusicGenre findGenreByIdOrName(AddFavoriteGenreRequest request) {
        // Try by ID first if provided
        if (request.getGenreId() != null) {
            return musicGenreRepository.findById(request.getGenreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Genre", "id", request.getGenreId()));
        }
        
        // Try by name if provided
        if (request.getGenreName() != null && !request.getGenreName().trim().isEmpty()) {
            String genreName = request.getGenreName().trim();
            return musicGenreRepository.findByNameIgnoreCase(genreName)
                    .orElseThrow(() -> new ResourceNotFoundException("Genre", "name", genreName));
        }
        
        // This shouldn't happen due to validation, but just in case
        throw new BusinessRuleException("Either genreId or genreName must be provided");
    }
    
    /**
     * Find genre by ID or name (for DELETE operations)
     */
    private MusicGenre findGenreByIdOrName(DeleteFavoriteGenreRequest request) {
        // Try by ID first if provided
        if (request.getGenreId() != null) {
            return musicGenreRepository.findById(request.getGenreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Genre", "id", request.getGenreId()));
        }
        
        // Try by name if provided
        if (request.getGenreName() != null && !request.getGenreName().trim().isEmpty()) {
            String genreName = request.getGenreName().trim();
            return musicGenreRepository.findByNameIgnoreCase(genreName)
                    .orElseThrow(() -> new ResourceNotFoundException("Genre", "name", genreName));
        }
        
        // This shouldn't happen due to validation, but just in case
        throw new BusinessRuleException("Either genreId or genreName must be provided");
    }
    
    /**
     * Build complete preference response with all related data
     */
    private UserPreferenceResponse buildPreferenceResponse(Profile profile, UserPreference preference) {
        List<FavoriteArtist> favoriteArtists = 
            favoriteArtistRepository.findByProfileProfileId(profile.getProfileId());
        List<FavoriteGenre> favoriteGenres = 
            favoriteGenreRepository.findByProfileProfileId(profile.getProfileId());
        
        List<ArtistResponse> artistResponses = favoriteArtists.stream()
                .map(fa -> mapper.toArtistResponse(fa.getArtist()))
                .collect(Collectors.toList());
        
        List<MusicGenreResponse> genreResponses = favoriteGenres.stream()
                .map(fg -> mapper.toGenreResponse(fg.getMusicGenre()))
                .collect(Collectors.toList());
        
        return mapper.toResponse(preference, artistResponses, genreResponses);
    }
    
    /**
     * Build preference summary without full lists (only counts)
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
}