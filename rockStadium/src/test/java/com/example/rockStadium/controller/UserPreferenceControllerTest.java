// package com.example.rockStadium.controller;

// import java.math.BigDecimal;
// import java.util.Arrays;
// import java.util.List;

// import static org.hamcrest.Matchers.hasSize;
// import static org.hamcrest.Matchers.is;
// import org.junit.jupiter.api.Test;
// import org.mockito.ArgumentMatchers;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.context.bean.override.mockito.MockitoBean;
// import org.springframework.test.web.servlet.MockMvc;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// import com.example.rockStadium.dto.AddFavoriteArtistRequest;
// import com.example.rockStadium.dto.AddFavoriteGenreRequest;
// import com.example.rockStadium.dto.ArtistResponse;
// import com.example.rockStadium.dto.MusicGenreResponse;
// import com.example.rockStadium.dto.UserPreferenceRequest;
// import com.example.rockStadium.dto.UserPreferenceResponse;
// import com.example.rockStadium.service.UserPreferenceService;
// import com.fasterxml.jackson.databind.ObjectMapper;

// /**
//  * Pruebas unitarias para UserPreferenceController
//  * Valida el comportamiento de los endpoints REST de preferencias de usuario
//  */
// @WebMvcTest(controllers = UserPreferenceController.class)
// class UserPreferenceControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockitoBean
//     private UserPreferenceService preferenceService;

//     @Autowired
//     private ObjectMapper objectMapper;

//     // ===== TESTS DE PREFERENCIAS DE BÚSQUEDA =====

//     @Test
//     void testGetPreferences_Success() throws Exception {
//         Integer userId = 1;
//         UserPreferenceResponse response = UserPreferenceResponse.builder()
//                 .userPreferenceId(1)
//                 .profileId(1)
//                 .searchRadius(BigDecimal.valueOf(10.0))
//                 .emailNotifications(true)
//                 .favoriteArtistsCount(5)
//                 .favoriteGenresCount(3)
//                 .maxFavoriteArtists(50)
//                 .maxFavoriteGenres(10)
//                 .build();

//         when(preferenceService.getPreferences(userId)).thenReturn(response);

//         mockMvc.perform(get("/api/users/{userId}/preferences", userId)
//                 .accept(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.userPreferenceId", is(1)))
//                 .andExpect(jsonPath("$.profileId", is(1)))
//                 .andExpect(jsonPath("$.searchRadius", is(10.0)))
//                 .andExpect(jsonPath("$.emailNotifications", is(true)))
//                 .andExpect(jsonPath("$.favoriteArtistsCount", is(5)))
//                 .andExpect(jsonPath("$.maxFavoriteArtists", is(50)));

//         verify(preferenceService, times(1)).getPreferences(userId);
//     }

//     @Test
//     void testUpdatePreferences_Success() throws Exception {
//         Integer userId = 1;
//         UserPreferenceRequest request = UserPreferenceRequest.builder()
//                 .searchRadiusKm(BigDecimal.valueOf(15.0))
//                 .emailNotifications(false)
//                 .build();

//         UserPreferenceResponse response = UserPreferenceResponse.builder()
//                 .userPreferenceId(1)
//                 .profileId(1)
//                 .searchRadius(BigDecimal.valueOf(15.0))
//                 .emailNotifications(false)
//                 .build();

//         when(preferenceService.createOrUpdatePreferences(eq(userId), ArgumentMatchers.any(UserPreferenceRequest.class)))
//                 .thenReturn(response);

//         mockMvc.perform(put("/api/users/{userId}/preferences", userId)
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(request)))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.searchRadius", is(15.0)))
//                 .andExpect(jsonPath("$.emailNotifications", is(false)));

//         verify(preferenceService, times(1)).createOrUpdatePreferences(eq(userId), ArgumentMatchers.any(UserPreferenceRequest.class));
//     }

//     @Test
//     void testUpdatePreferences_InvalidRadiusTooSmall() throws Exception {
//         Integer userId = 1;
//         UserPreferenceRequest request = UserPreferenceRequest.builder()
//                 .searchRadiusKm(BigDecimal.valueOf(2.0)) // Menor al mínimo de 5.0
//                 .emailNotifications(true)
//                 .build();

//         mockMvc.perform(put("/api/users/{userId}/preferences", userId)
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(request)))
//                 .andExpect(status().isBadRequest());
//     }

//     @Test
//     void testUpdatePreferences_InvalidRadiusTooLarge() throws Exception {
//         Integer userId = 1;
//         UserPreferenceRequest request = UserPreferenceRequest.builder()
//                 .searchRadiusKm(BigDecimal.valueOf(100.0)) // Mayor al máximo de 50.0
//                 .emailNotifications(true)
//                 .build();

//         mockMvc.perform(put("/api/users/{userId}/preferences", userId)
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(request)))
//                 .andExpect(status().isBadRequest());
//     }

//     // ===== TESTS DE ARTISTAS FAVORITOS =====

//     @Test
//     void testGetFavoriteArtists_Success() throws Exception {
//         Integer userId = 1;
//         List<ArtistResponse> artists = Arrays.asList(
//                 ArtistResponse.builder()
//                         .spotifyId("2ye2Wgw4gimLv2eAKyk1NB")
//                         .name("Metallica")
//                         .build(),
//                 ArtistResponse.builder()
//                         .spotifyId("6mdiAmATAx73kdxrNrnlao")
//                         .name("Iron Maiden")
//                         .build()
//         );

//         when(preferenceService.getFavoriteArtists(userId)).thenReturn(artists);

//         mockMvc.perform(get("/api/users/{userId}/preferences/artists", userId)
//                 .accept(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$", hasSize(2)))
//                 .andExpect(jsonPath("$[0].name", is("Metallica")))
//                 .andExpect(jsonPath("$[1].name", is("Iron Maiden")));

//         verify(preferenceService, times(1)).getFavoriteArtists(userId);
//     }

//     @Test
//     void testAddFavoriteArtist_Success() throws Exception {
//         Integer userId = 1;
//         AddFavoriteArtistRequest request = AddFavoriteArtistRequest.builder()
//                 .spotifyId("2ye2Wgw4gimLv2eAKyk1NB")
//                 .build();

//         UserPreferenceResponse response = UserPreferenceResponse.builder()
//                 .userPreferenceId(1)
//                 .profileId(1)
//                 .favoriteArtistsCount(6)
//                 .maxFavoriteArtists(50)
//                 .build();

//         when(preferenceService.addFavoriteArtist(eq(userId), eq("2ye2Wgw4gimLv2eAKyk1NB")))
//                 .thenReturn(response);

//         mockMvc.perform(post("/api/users/{userId}/preferences/artists", userId)
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(request)))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.favoriteArtistsCount", is(6)));

//         verify(preferenceService, times(1)).addFavoriteArtist(userId, "2ye2Wgw4gimLv2eAKyk1NB");
//     }

//     @Test
//     void testRemoveFavoriteArtist_Success() throws Exception {
//         Integer userId = 1;
//         Integer artistId = 5;

//         UserPreferenceResponse response = UserPreferenceResponse.builder()
//                 .userPreferenceId(1)
//                 .profileId(1)
//                 .favoriteArtistsCount(4)
//                 .maxFavoriteArtists(50)
//                 .build();

//         when(preferenceService.removeFavoriteArtist(userId, artistId)).thenReturn(response);

//         mockMvc.perform(delete("/api/users/{userId}/preferences/artists/{artistId}", userId, artistId))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.favoriteArtistsCount", is(4)));

//         verify(preferenceService, times(1)).removeFavoriteArtist(userId, artistId);
//     }

//     // ===== TESTS DE GÉNEROS FAVORITOS =====

//     @Test
//     void testGetFavoriteGenres_Success() throws Exception {
//         Integer userId = 1;
//         List<MusicGenreResponse> genres = Arrays.asList(
//                 MusicGenreResponse.builder()
//                         .musicGenreId(1)
//                         .name("Rock")
//                         .build(),
//                 MusicGenreResponse.builder()
//                         .musicGenreId(2)
//                         .name("Metal")
//                         .build()
//         );

//         when(preferenceService.getFavoriteGenres(userId)).thenReturn(genres);

//         mockMvc.perform(get("/api/users/{userId}/preferences/genres", userId)
//                 .accept(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$", hasSize(2)))
//                 .andExpect(jsonPath("$[0].name", is("Rock")))
//                 .andExpect(jsonPath("$[1].name", is("Metal")));

//         verify(preferenceService, times(1)).getFavoriteGenres(userId);
//     }

//     @Test
//     void testAddFavoriteGenre_Success() throws Exception {
//         Integer userId = 1;
//         AddFavoriteGenreRequest request = AddFavoriteGenreRequest.builder()
//                 .genreId(3)
//                 .build();

//         UserPreferenceResponse response = UserPreferenceResponse.builder()
//                 .userPreferenceId(1)
//                 .profileId(1)
//                 .favoriteGenresCount(4)
//                 .maxFavoriteGenres(10)
//                 .build();

//         when(preferenceService.addFavoriteGenre(userId, 3)).thenReturn(response);

//         mockMvc.perform(post("/api/users/{userId}/preferences/genres", userId)
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(request)))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.favoriteGenresCount", is(4)));

//         verify(preferenceService, times(1)).addFavoriteGenre(userId, 3);
//     }

//     @Test
//     void testRemoveFavoriteGenre_Success() throws Exception {
//         Integer userId = 1;
//         Integer genreId = 2;

//         UserPreferenceResponse response = UserPreferenceResponse.builder()
//                 .userPreferenceId(1)
//                 .profileId(1)
//                 .favoriteGenresCount(2)
//                 .maxFavoriteGenres(10)
//                 .build();

//         when(preferenceService.removeFavoriteGenre(userId, genreId)).thenReturn(response);

//         mockMvc.perform(delete("/api/users/{userId}/preferences/genres/{genreId}", userId, genreId))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.favoriteGenresCount", is(2)));

//         verify(preferenceService, times(1)).removeFavoriteGenre(userId, genreId);
//     }

//     @Test
//     void testGetAllGenres_Success() throws Exception {
//         Integer userId = 1;
//         List<MusicGenreResponse> allGenres = Arrays.asList(
//                 MusicGenreResponse.builder().musicGenreId(1).name("Rock").build(),
//                 MusicGenreResponse.builder().musicGenreId(2).name("Metal").build(),
//                 MusicGenreResponse.builder().musicGenreId(3).name("Pop").build(),
//                 MusicGenreResponse.builder().musicGenreId(4).name("Jazz").build()
//         );

//         when(preferenceService.getAllGenres()).thenReturn(allGenres);

//         mockMvc.perform(get("/api/users/{userId}/preferences/genres/available", userId)
//                 .accept(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$", hasSize(4)))
//                 .andExpect(jsonPath("$[0].name", is("Rock")))
//                 .andExpect(jsonPath("$[3].name", is("Jazz")));

//         verify(preferenceService, times(1)).getAllGenres();
//     }
// }