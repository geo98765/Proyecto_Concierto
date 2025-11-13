// package com.example.rockStadium.service;

// import java.math.BigDecimal;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Optional;

// import static org.assertj.core.api.Assertions.assertThat;
// import static org.assertj.core.api.Assertions.assertThatThrownBy;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyInt;
// import static org.mockito.ArgumentMatchers.anyString;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import static org.mockito.Mockito.never;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
// import org.mockito.junit.jupiter.MockitoExtension;

// import com.example.rockStadium.dto.ArtistResponse;
// import com.example.rockStadium.dto.MusicGenreResponse;
// import com.example.rockStadium.dto.UserPreferenceRequest;
// import com.example.rockStadium.dto.UserPreferenceResponse;
// import com.example.rockStadium.model.Artist;
// import com.example.rockStadium.model.FavoriteArtist;
// import com.example.rockStadium.model.FavoriteGenre;
// import com.example.rockStadium.model.MusicGenre;
// import com.example.rockStadium.model.Profile;
// import com.example.rockStadium.model.User;
// import com.example.rockStadium.model.UserPreference;
// import com.example.rockStadium.repository.ArtistRepository;
// import com.example.rockStadium.repository.FavoriteArtistRepository;
// import com.example.rockStadium.repository.FavoriteGenreRepository;
// import com.example.rockStadium.repository.MusicGenreRepository;
// import com.example.rockStadium.repository.ProfileRepository;
// import com.example.rockStadium.repository.UserPreferenceRepository;

// /**
//  * Pruebas unitarias para UserPreferenceServiceImpl
//  * Valida la lógica de negocio del servicio de preferencias
//  */
// @ExtendWith(MockitoExtension.class)
// class UserPreferenceServiceTest {

//     @Mock
//     private UserPreferenceRepository userPreferenceRepository;

//     @Mock
//     private ProfileRepository profileRepository;

//     @Mock
//     private ArtistRepository artistRepository;

//     @Mock
//     private FavoriteArtistRepository favoriteArtistRepository;

//     @Mock
//     private FavoriteGenreRepository favoriteGenreRepository;

//     @Mock
//     private MusicGenreRepository musicGenreRepository;

//     @Mock
//     private SpotifyService spotifyService;

//     @InjectMocks
//     private UserPreferenceServiceImpl preferenceService;

//     private User testUser;
//     private Profile testProfile;
//     private UserPreference testPreference;

//     @BeforeEach
//     void setUp() {
//         testUser = User.builder()
//                 .userId(1)
//                 .email("test@example.com")
//                 .build();

//         testProfile = Profile.builder()
//                 .profileId(1)
//                 .name("Test Profile")
//                 .user(testUser)
//                 .build();

//         testPreference = UserPreference.builder()
//                 .userPreferenceId(1)
//                 .profile(testProfile)
//                 .searchRadius(BigDecimal.valueOf(10.0))
//                 .emailNotifications(true)
//                 .build();

//         testProfile.setUserPreference(testPreference);
//     }

//     // ===== TESTS DE PREFERENCIAS DE BÚSQUEDA =====

//     @Test
//     void testGetPreferences_ExistingPreference() {
//         when(profileRepository.findByUserUserId(1)).thenReturn(Optional.of(testProfile));

//         UserPreferenceResponse response = preferenceService.getPreferences(1);

//         assertThat(response).isNotNull();
//         assertThat(response.getUserPreferenceId()).isEqualTo(1);
//         assertThat(response.getSearchRadius()).isEqualByComparingTo(BigDecimal.valueOf(10.0));
//         assertThat(response.getEmailNotifications()).isTrue();
//         verify(profileRepository, times(1)).findByUserUserId(1);
//     }

//     @Test
//     void testGetPreferences_ProfileNotFound() {
//         when(profileRepository.findByUserUserId(999)).thenReturn(Optional.empty());

//         assertThatThrownBy(() -> preferenceService.getPreferences(999))
//                 .isInstanceOf(RuntimeException.class);

//         verify(profileRepository, times(1)).findByUserUserId(999);
//     }

//     @Test
//     void testCreateOrUpdatePreferences_NewPreference() {
//         Profile profileWithoutPreference = Profile.builder()
//                 .profileId(1)
//                 .name("New Profile")
//                 .user(testUser)
//                 .build();

//         UserPreferenceRequest request = UserPreferenceRequest.builder()
//                 .searchRadiusKm(BigDecimal.valueOf(15.0))
//                 .emailNotifications(false)
//                 .build();

//         when(profileRepository.findByUserUserId(1)).thenReturn(Optional.of(profileWithoutPreference));
//         when(userPreferenceRepository.save(any(UserPreference.class))).thenAnswer(invocation -> {
//             UserPreference pref = invocation.getArgument(0);
//             pref.setUserPreferenceId(1);
//             return pref;
//         });
//         when(favoriteArtistRepository.countByProfileProfileId(1)).thenReturn(0L);
//         when(favoriteGenreRepository.countByProfileProfileId(1)).thenReturn(0L);

//         UserPreferenceResponse response = preferenceService.createOrUpdatePreferences(1, request);

//         assertThat(response).isNotNull();
//         assertThat(response.getSearchRadius()).isEqualByComparingTo(BigDecimal.valueOf(15.0));
//         assertThat(response.getEmailNotifications()).isFalse();
//         verify(userPreferenceRepository, times(1)).save(any(UserPreference.class));
//     }

//     @Test
//     void testCreateOrUpdatePreferences_UpdateExisting() {
//         UserPreferenceRequest request = UserPreferenceRequest.builder()
//                 .searchRadiusKm(BigDecimal.valueOf(20.0))
//                 .emailNotifications(false)
//                 .build();

//         when(profileRepository.findByUserUserId(1)).thenReturn(Optional.of(testProfile));
//         when(userPreferenceRepository.save(any(UserPreference.class))).thenAnswer(invocation -> invocation.getArgument(0));
//         when(favoriteArtistRepository.countByProfileProfileId(1)).thenReturn(5L);
//         when(favoriteGenreRepository.countByProfileProfileId(1)).thenReturn(3L);

//         UserPreferenceResponse response = preferenceService.createOrUpdatePreferences(1, request);

//         assertThat(response).isNotNull();
//         assertThat(response.getSearchRadius()).isEqualByComparingTo(BigDecimal.valueOf(20.0));
//         assertThat(response.getEmailNotifications()).isFalse();
//         verify(userPreferenceRepository, times(1)).save(any(UserPreference.class));
//     }

//     @Test
//     void testCreateOrUpdatePreferences_InvalidRadiusTooSmall() {
//         UserPreferenceRequest request = UserPreferenceRequest.builder()
//                 .searchRadiusKm(BigDecimal.valueOf(3.0))
//                 .build();

//         when(profileRepository.findByUserUserId(1)).thenReturn(Optional.of(testProfile));

//         assertThatThrownBy(() -> preferenceService.createOrUpdatePreferences(1, request))
//                 .isInstanceOf(RuntimeException.class)
//                 .hasMessageContaining("Radio de búsqueda");

//         verify(userPreferenceRepository, never()).save(any());
//     }

//     @Test
//     void testCreateOrUpdatePreferences_InvalidRadiusTooLarge() {
//         UserPreferenceRequest request = UserPreferenceRequest.builder()
//                 .searchRadiusKm(BigDecimal.valueOf(100.0))
//                 .build();

//         when(profileRepository.findByUserUserId(1)).thenReturn(Optional.of(testProfile));

//         assertThatThrownBy(() -> preferenceService.createOrUpdatePreferences(1, request))
//                 .isInstanceOf(RuntimeException.class)
//                 .hasMessageContaining("Radio de búsqueda");

//         verify(userPreferenceRepository, never()).save(any());
//     }

//     // ===== TESTS DE ARTISTAS FAVORITOS =====

//     @Test
//     void testGetFavoriteArtists_Success() {
//         Artist artist1 = Artist.builder()
//                 .artistId(1)
//                 .name("Metallica")
//                 .spotifyId("2ye2Wgw4gimLv2eAKyk1NB")
//                 .build();

//         Artist artist2 = Artist.builder()
//                 .artistId(2)
//                 .name("Iron Maiden")
//                 .spotifyId("6mdiAmATAx73kdxrNrnlao")
//                 .build();

//         FavoriteArtist fav1 = FavoriteArtist.builder()
//                 .favoriteArtistId(1)
//                 .artist(artist1)
//                 .profile(testProfile)
//                 .build();

//         FavoriteArtist fav2 = FavoriteArtist.builder()
//                 .favoriteArtistId(2)
//                 .artist(artist2)
//                 .profile(testProfile)
//                 .build();

//         when(profileRepository.findByUserUserId(1)).thenReturn(Optional.of(testProfile));
//         when(favoriteArtistRepository.findByProfileProfileId(1)).thenReturn(Arrays.asList(fav1, fav2));
//         when(spotifyService.getArtistById("2ye2Wgw4gimLv2eAKyk1NB"))
//                 .thenReturn(ArtistResponse.builder().spotifyId("2ye2Wgw4gimLv2eAKyk1NB").name("Metallica").build());
//         when(spotifyService.getArtistById("6mdiAmATAx73kdxrNrnlao"))
//                 .thenReturn(ArtistResponse.builder().spotifyId("6mdiAmATAx73kdxrNrnlao").name("Iron Maiden").build());

//         List<ArtistResponse> artists = preferenceService.getFavoriteArtists(1);

//         assertThat(artists).hasSize(2);
//         assertThat(artists.get(0).getName()).isEqualTo("Metallica");
//         assertThat(artists.get(1).getName()).isEqualTo("Iron Maiden");
//         verify(favoriteArtistRepository, times(1)).findByProfileProfileId(1);
//     }

//     @Test
//     void testAddFavoriteArtist_NewArtist() {
//         String spotifyId = "2ye2Wgw4gimLv2eAKyk1NB";
//         Artist newArtist = Artist.builder()
//                 .artistId(1)
//                 .name("Metallica")
//                 .spotifyId(spotifyId)
//                 .build();

//         ArtistResponse artistResponse = ArtistResponse.builder()
//                 .spotifyId(spotifyId)
//                 .name("Metallica")
//                 .build();

//         when(profileRepository.findByUserUserId(1)).thenReturn(Optional.of(testProfile));
//         when(favoriteArtistRepository.countByProfileProfileId(1)).thenReturn(5L);
//         when(artistRepository.findBySpotifyId(spotifyId)).thenReturn(Optional.empty());
//         when(spotifyService.getArtistById(spotifyId)).thenReturn(artistResponse);
//         when(artistRepository.save(any(Artist.class))).thenReturn(newArtist);
//         when(favoriteArtistRepository.save(any(FavoriteArtist.class))).thenAnswer(invocation -> invocation.getArgument(0));
//         when(favoriteGenreRepository.countByProfileProfileId(1)).thenReturn(3L);

//         UserPreferenceResponse response = preferenceService.addFavoriteArtist(1, spotifyId);

//         assertThat(response).isNotNull();
//         verify(spotifyService, times(1)).getArtistById(spotifyId);
//         verify(artistRepository, times(1)).save(any(Artist.class));
//         verify(favoriteArtistRepository, times(1)).save(any(FavoriteArtist.class));
//     }

//     @Test
//     void testAddFavoriteArtist_ExistingArtist() {
//         String spotifyId = "2ye2Wgw4gimLv2eAKyk1NB";
//         Artist existingArtist = Artist.builder()
//                 .artistId(1)
//                 .name("Metallica")
//                 .spotifyId(spotifyId)
//                 .build();

//         when(profileRepository.findByUserUserId(1)).thenReturn(Optional.of(testProfile));
//         when(favoriteArtistRepository.countByProfileProfileId(1)).thenReturn(5L);
//         when(artistRepository.findBySpotifyId(spotifyId)).thenReturn(Optional.of(existingArtist));
//         when(favoriteArtistRepository.existsByProfileProfileIdAndArtistArtistId(1, 1)).thenReturn(false);
//         when(favoriteArtistRepository.save(any(FavoriteArtist.class))).thenAnswer(invocation -> invocation.getArgument(0));
//         when(favoriteGenreRepository.countByProfileProfileId(1)).thenReturn(3L);

//         UserPreferenceResponse response = preferenceService.addFavoriteArtist(1, spotifyId);

//         assertThat(response).isNotNull();
//         verify(spotifyService, never()).getArtistById(anyString());
//         verify(artistRepository, never()).save(any());
//         verify(favoriteArtistRepository, times(1)).save(any(FavoriteArtist.class));
//     }

//     @Test
//     void testAddFavoriteArtist_MaxLimitReached() {
//         String spotifyId = "2ye2Wgw4gimLv2eAKyk1NB";

//         when(profileRepository.findByUserUserId(1)).thenReturn(Optional.of(testProfile));
//         when(favoriteArtistRepository.countByProfileProfileId(1)).thenReturn(50L);

//         assertThatThrownBy(() -> preferenceService.addFavoriteArtist(1, spotifyId))
//                 .isInstanceOf(RuntimeException.class)
//                 .hasMessageContaining("límite máximo");

//         verify(favoriteArtistRepository, never()).save(any());
//     }

//     @Test
//     void testAddFavoriteArtist_AlreadyExists() {
//         String spotifyId = "2ye2Wgw4gimLv2eAKyk1NB";
//         Artist existingArtist = Artist.builder()
//                 .artistId(1)
//                 .name("Metallica")
//                 .spotifyId(spotifyId)
//                 .build();

//         when(profileRepository.findByUserUserId(1)).thenReturn(Optional.of(testProfile));
//         when(favoriteArtistRepository.countByProfileProfileId(1)).thenReturn(5L);
//         when(artistRepository.findBySpotifyId(spotifyId)).thenReturn(Optional.of(existingArtist));
//         when(favoriteArtistRepository.existsByProfileProfileIdAndArtistArtistId(1, 1)).thenReturn(true);

//         assertThatThrownBy(() -> preferenceService.addFavoriteArtist(1, spotifyId))
//                 .isInstanceOf(RuntimeException.class)
//                 .hasMessageContaining("ya está");

//         verify(favoriteArtistRepository, never()).save(any());
//     }

//     @Test
//     void testRemoveFavoriteArtist_Success() {
//         Integer artistId = 1;
//         FavoriteArtist favoriteArtist = FavoriteArtist.builder()
//                 .favoriteArtistId(1)
//                 .profile(testProfile)
//                 .build();

//         when(profileRepository.findByUserUserId(1)).thenReturn(Optional.of(testProfile));
//         when(favoriteArtistRepository.findByProfileProfileIdAndArtistArtistId(1, artistId))
//                 .thenReturn(Optional.of(favoriteArtist));
//         when(favoriteArtistRepository.countByProfileProfileId(1)).thenReturn(4L);
//         when(favoriteGenreRepository.countByProfileProfileId(1)).thenReturn(3L);

//         UserPreferenceResponse response = preferenceService.removeFavoriteArtist(1, artistId);

//         assertThat(response).isNotNull();
//         verify(favoriteArtistRepository, times(1)).delete(favoriteArtist);
//     }

//     @Test
//     void testRemoveFavoriteArtist_NotFound() {
//         Integer artistId = 999;

//         when(profileRepository.findByUserUserId(1)).thenReturn(Optional.of(testProfile));
//         when(favoriteArtistRepository.findByProfileProfileIdAndArtistArtistId(1, artistId))
//                 .thenReturn(Optional.empty());

//         assertThatThrownBy(() -> preferenceService.removeFavoriteArtist(1, artistId))
//                 .isInstanceOf(RuntimeException.class)
//                 .hasMessageContaining("no está en tus favoritos");

//         verify(favoriteArtistRepository, never()).delete(any());
//     }

//     // ===== TESTS DE GÉNEROS FAVORITOS =====

//     @Test
//     void testGetFavoriteGenres_Success() {
//         MusicGenre genre1 = MusicGenre.builder()
//                 .musicGenreId(1)
//                 .name("Rock")
//                 .build();

//         MusicGenre genre2 = MusicGenre.builder()
//                 .musicGenreId(2)
//                 .name("Metal")
//                 .build();

//         FavoriteGenre fav1 = FavoriteGenre.builder()
//                 .favoriteGenreId(1)
//                 .musicGenre(genre1)
//                 .profile(testProfile)
//                 .build();

//         FavoriteGenre fav2 = FavoriteGenre.builder()
//                 .favoriteGenreId(2)
//                 .musicGenre(genre2)
//                 .profile(testProfile)
//                 .build();

//         when(profileRepository.findByUserUserId(1)).thenReturn(Optional.of(testProfile));
//         when(favoriteGenreRepository.findByProfileProfileId(1)).thenReturn(Arrays.asList(fav1, fav2));

//         List<MusicGenreResponse> genres = preferenceService.getFavoriteGenres(1);

//         assertThat(genres).hasSize(2);
//         assertThat(genres.get(0).getName()).isEqualTo("Rock");
//         assertThat(genres.get(1).getName()).isEqualTo("Metal");
//         verify(favoriteGenreRepository, times(1)).findByProfileProfileId(1);
//     }

//     @Test
//     void testAddFavoriteGenre_Success() {
//         Integer genreId = 3;
//         MusicGenre genre = MusicGenre.builder()
//                 .musicGenreId(genreId)
//                 .name("Jazz")
//                 .build();

//         when(profileRepository.findByUserUserId(1)).thenReturn(Optional.of(testProfile));
//         when(favoriteGenreRepository.countByProfileProfileId(1)).thenReturn(5L);
//         when(musicGenreRepository.findById(genreId)).thenReturn(Optional.of(genre));
//         when(favoriteGenreRepository.existsByProfileProfileIdAndMusicGenreMusicGenreId(1, genreId)).thenReturn(false);
//         when(favoriteGenreRepository.save(any(FavoriteGenre.class))).thenAnswer(invocation -> invocation.getArgument(0));
//         when(favoriteArtistRepository.countByProfileProfileId(1)).thenReturn(3L);

//         UserPreferenceResponse response = preferenceService.addFavoriteGenre(1, genreId);

//         assertThat(response).isNotNull();
//         verify(favoriteGenreRepository, times(1)).save(any(FavoriteGenre.class));
//     }

//     @Test
//     void testAddFavoriteGenre_MaxLimitReached() {
//         Integer genreId = 3;

//         when(profileRepository.findByUserUserId(1)).thenReturn(Optional.of(testProfile));
//         when(favoriteGenreRepository.countByProfileProfileId(1)).thenReturn(10L);

//         assertThatThrownBy(() -> preferenceService.addFavoriteGenre(1, genreId))
//                 .isInstanceOf(RuntimeException.class)
//                 .hasMessageContaining("límite máximo");

//         verify(favoriteGenreRepository, never()).save(any());
//     }

//     @Test
//     void testAddFavoriteGenre_GenreNotFound() {
//         Integer genreId = 999;

//         when(profileRepository.findByUserUserId(1)).thenReturn(Optional.of(testProfile));
//         when(favoriteGenreRepository.countByProfileProfileId(1)).thenReturn(5L);
//         when(musicGenreRepository.findById(genreId)).thenReturn(Optional.empty());

//         assertThatThrownBy(() -> preferenceService.addFavoriteGenre(1, genreId))
//                 .isInstanceOf(RuntimeException.class)
//                 .hasMessageContaining("no encontrado");

//         verify(favoriteGenreRepository, never()).save(any());
//     }

//     @Test
//     void testAddFavoriteGenre_AlreadyExists() {
//         Integer genreId = 3;
//         MusicGenre genre = MusicGenre.builder()
//                 .musicGenreId(genreId)
//                 .name("Jazz")
//                 .build();

//         when(profileRepository.findByUserUserId(1)).thenReturn(Optional.of(testProfile));
//         when(favoriteGenreRepository.countByProfileProfileId(1)).thenReturn(5L);
//         when(musicGenreRepository.findById(genreId)).thenReturn(Optional.of(genre));
//         when(favoriteGenreRepository.existsByProfileProfileIdAndMusicGenreMusicGenreId(1, genreId)).thenReturn(true);

//         assertThatThrownBy(() -> preferenceService.addFavoriteGenre(1, genreId))
//                 .isInstanceOf(RuntimeException.class)
//                 .hasMessageContaining("ya está");

//         verify(favoriteGenreRepository, never()).save(any());
//     }

//     @Test
//     void testRemoveFavoriteGenre_Success() {
//         Integer genreId = 1;
//         FavoriteGenre favoriteGenre = FavoriteGenre.builder()
//                 .favoriteGenreId(1)
//                 .profile(testProfile)
//                 .build();

//         when(profileRepository.findByUserUserId(1)).thenReturn(Optional.of(testProfile));
//         when(favoriteGenreRepository.existsByProfileProfileIdAndMusicGenreMusicGenreId(1, genreId))
//                 .thenReturn(true);
//         when(favoriteGenreRepository.countByProfileProfileId(1)).thenReturn(2L);
//         when(favoriteArtistRepository.countByProfileProfileId(1)).thenReturn(5L);

//         UserPreferenceResponse response = preferenceService.removeFavoriteGenre(1, genreId);

//         assertThat(response).isNotNull();
//         verify(favoriteGenreRepository, times(1)).deleteByProfileProfileIdAndMusicGenreMusicGenreId(1, genreId);
//     }

//     @Test
//     void testRemoveFavoriteGenre_NotFound() {
//         Integer genreId = 999;

//         when(profileRepository.findByUserUserId(1)).thenReturn(Optional.of(testProfile));
//         when(favoriteGenreRepository.existsByProfileProfileIdAndMusicGenreMusicGenreId(1, genreId))
//                 .thenReturn(false);

//         assertThatThrownBy(() -> preferenceService.removeFavoriteGenre(1, genreId))
//                 .isInstanceOf(RuntimeException.class)
//                 .hasMessageContaining("no está en tus favoritos");

//         verify(favoriteGenreRepository, never()).deleteByProfileProfileIdAndMusicGenreMusicGenreId(anyInt(), anyInt());
//     }

//     @Test
//     void testGetAllGenres_Success() {
//         List<MusicGenre> allGenres = Arrays.asList(
//                 MusicGenre.builder().musicGenreId(1).name("Rock").build(),
//                 MusicGenre.builder().musicGenreId(2).name("Metal").build(),
//                 MusicGenre.builder().musicGenreId(3).name("Pop").build(),
//                 MusicGenre.builder().musicGenreId(4).name("Jazz").build()
//         );

//         when(musicGenreRepository.findAll()).thenReturn(allGenres);

//         List<MusicGenreResponse> genres = preferenceService.getAllGenres();

//         assertThat(genres).hasSize(4);
//         assertThat(genres.get(0).getName()).isEqualTo("Rock");
//         assertThat(genres.get(3).getName()).isEqualTo("Jazz");
//         verify(musicGenreRepository, times(1)).findAll();
//     }
// }