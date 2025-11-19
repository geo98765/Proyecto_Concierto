package com.example.rockStadium.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.rockstadium.controller.UserPreferenceController;
import com.example.rockstadium.dto.AddFavoriteArtistRequest;
import com.example.rockstadium.dto.AddFavoriteGenreRequest;
import com.example.rockstadium.dto.ArtistResponse;
import com.example.rockstadium.dto.DeleteFavoriteGenreRequest;
import com.example.rockstadium.dto.MusicGenreResponse;
import com.example.rockstadium.dto.SuccessResponse;
import com.example.rockstadium.dto.UserPreferenceBasicResponse;
import com.example.rockstadium.dto.UserPreferenceRequest;
import com.example.rockstadium.dto.UserPreferenceResponse;
import com.example.rockstadium.service.UserPreferenceService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit tests for UserPreferenceController
 * Validates the behavior of REST endpoints for user preferences
 * 
 * Pruebas unitarias para UserPreferenceController
 * Valida el comportamiento de los endpoints REST de preferencias de usuario
 */
@WebMvcTest(controllers = UserPreferenceController.class)
class UserPreferenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserPreferenceService preferenceService;

    @Autowired
    private ObjectMapper objectMapper;

    // ===== TESTS DE PREFERENCIAS DE BÚSQUEDA =====

    @Test
    void testGetPreferences_WithFullFalse_Success() throws Exception {
        // Dado: Usuario con preferencias sin listas completas
        Integer userId = 1;
        UserPreferenceResponse response = UserPreferenceResponse.builder()
                .userPreferenceId(1)
                .profileId(1)
                .searchRadius(BigDecimal.valueOf(10.0))
                .emailNotifications(true)
                .favoriteArtistsCount(5)
                .favoriteGenresCount(3)
                .maxFavoriteArtists(50)
                .maxFavoriteGenres(30)
                .build();

        when(preferenceService.getPreferences(userId, false)).thenReturn(response);

        // Cuando: Se solicitan las preferencias con full=false
        mockMvc.perform(get("/api/v1/users/{userId}/preferences", userId)
                .param("full", "false")
                .accept(MediaType.APPLICATION_JSON))
                // Entonces: Se obtienen las preferencias correctamente
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userPreferenceId", is(1)))
                .andExpect(jsonPath("$.profileId", is(1)))
                .andExpect(jsonPath("$.searchRadius", is(10.0)))
                .andExpect(jsonPath("$.emailNotifications", is(true)))
                .andExpect(jsonPath("$.favoriteArtistsCount", is(5)))
                .andExpect(jsonPath("$.favoriteGenresCount", is(3)))
                .andExpect(jsonPath("$.maxFavoriteArtists", is(50)))
                .andExpect(jsonPath("$.maxFavoriteGenres", is(30)));

        verify(preferenceService, times(1)).getPreferences(userId, false);
    }

    @Test
    void testGetPreferences_WithFullTrue_Success() throws Exception {
        // Dado: Usuario con preferencias incluyendo listas completas
        Integer userId = 1;
        List<ArtistResponse> artists = Arrays.asList(
                ArtistResponse.builder()
                        .spotifyId("2ye2Wgw4gimLv2eAKyk1NB")
                        .name("Metallica")
                        .build()
        );
        
        List<MusicGenreResponse> genres = Arrays.asList(
                MusicGenreResponse.builder()
                        .musicGenreId(1)
                        .name("Rock")
                        .build()
        );

        UserPreferenceResponse response = UserPreferenceResponse.builder()
                .userPreferenceId(1)
                .profileId(1)
                .searchRadius(BigDecimal.valueOf(10.0))
                .emailNotifications(true)
                .favoriteArtists(artists)
                .favoriteGenres(genres)
                .favoriteArtistsCount(1)
                .favoriteGenresCount(1)
                .build();

        when(preferenceService.getPreferences(userId, true)).thenReturn(response);

        // Cuando: Se solicitan las preferencias con full=true
        mockMvc.perform(get("/api/v1/users/{userId}/preferences", userId)
                .param("full", "true")
                .accept(MediaType.APPLICATION_JSON))
                // Entonces: Se obtienen las preferencias con listas completas
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favoriteArtists", hasSize(1)))
                .andExpect(jsonPath("$.favoriteGenres", hasSize(1)))
                .andExpect(jsonPath("$.favoriteArtists[0].name", is("Metallica")))
                .andExpect(jsonPath("$.favoriteGenres[0].name", is("Rock")));

        verify(preferenceService, times(1)).getPreferences(userId, true);
    }

    @Test
    void testUpdatePreferences_Success() throws Exception {
        // Dado: Una solicitud de actualización de preferencias
        Integer userId = 1;
        UserPreferenceRequest request = UserPreferenceRequest.builder()
                .searchRadiusKm(BigDecimal.valueOf(15.0))
                .emailNotifications(false)
                .build();

        // ✅ CAMBIADO: Ahora el servicio devuelve UserPreferenceBasicResponse
        UserPreferenceBasicResponse response = UserPreferenceBasicResponse.builder()
                .userPreferenceId(1)
                .profileId(1)
                .searchRadius(BigDecimal.valueOf(15.0))
                .emailNotifications(false)
                .build();

        when(preferenceService.createOrUpdatePreferences(eq(userId), any(UserPreferenceRequest.class)))
                .thenReturn(response);

        // Cuando: Se actualizan las preferencias
        mockMvc.perform(put("/api/v1/users/{userId}/preferences", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Entonces: Las preferencias se actualizan y solo se devuelven los campos básicos
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userPreferenceId", is(1)))
                .andExpect(jsonPath("$.profileId", is(1)))
                .andExpect(jsonPath("$.searchRadius", is(15.0)))
                .andExpect(jsonPath("$.emailNotifications", is(false)))
                // ✅ VERIFICAR: No hay campos adicionales como listas o contadores
                .andExpect(jsonPath("$.favoriteArtists").doesNotExist())
                .andExpect(jsonPath("$.favoriteGenres").doesNotExist())
                .andExpect(jsonPath("$.favoriteArtistsCount").doesNotExist())
                .andExpect(jsonPath("$.favoriteGenresCount").doesNotExist());

        verify(preferenceService, times(1)).createOrUpdatePreferences(eq(userId), any(UserPreferenceRequest.class));
    }

    @Test
    void testUpdatePreferences_InvalidRadiusTooSmall() throws Exception {
        // Dado: Una solicitud con radio menor al mínimo permitido
        Integer userId = 1;
        UserPreferenceRequest request = UserPreferenceRequest.builder()
                .searchRadiusKm(BigDecimal.valueOf(2.0)) // Menor al mínimo de 5.0
                .emailNotifications(true)
                .build();

        // Cuando: Se intenta actualizar con un radio inválido
        // Entonces: Se rechaza la solicitud con bad request
        mockMvc.perform(put("/api/v1/users/{userId}/preferences", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdatePreferences_InvalidRadiusTooLarge() throws Exception {
        // Dado: Una solicitud con radio mayor al máximo permitido
        Integer userId = 1;
        UserPreferenceRequest request = UserPreferenceRequest.builder()
                .searchRadiusKm(BigDecimal.valueOf(100.0)) // Mayor al máximo de 50.0
                .emailNotifications(true)
                .build();

        // Cuando: Se intenta actualizar con un radio inválido
        // Entonces: Se rechaza la solicitud con bad request
        mockMvc.perform(put("/api/v1/users/{userId}/preferences", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ===== TESTS DE ARTISTAS FAVORITOS =====

    @Test
    void testGetFavoriteArtists_WithPagination_Success() throws Exception {
        // Dado: Una página de artistas favoritos
        Integer userId = 1;
        List<ArtistResponse> artists = Arrays.asList(
                ArtistResponse.builder()
                        .spotifyId("2ye2Wgw4gimLv2eAKyk1NB")
                        .name("Metallica")
                        .build(),
                ArtistResponse.builder()
                        .spotifyId("6mdiAmATAx73kdxrNrnlao")
                        .name("Iron Maiden")
                        .build()
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<ArtistResponse> page = new PageImpl<>(artists, pageable, artists.size());

        when(preferenceService.getFavoriteArtists(eq(userId), any(Pageable.class))).thenReturn(page);

        // Cuando: Se solicitan los artistas favoritos con paginación
        mockMvc.perform(get("/api/v1/users/{userId}/preferences/artists", userId)
                .param("page", "0")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
                // Entonces: Se obtiene la página de artistas correctamente
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name", is("Metallica")))
                .andExpect(jsonPath("$.content[1].name", is("Iron Maiden")))
                .andExpect(jsonPath("$.totalElements", is(2)));

        verify(preferenceService, times(1)).getFavoriteArtists(eq(userId), any(Pageable.class));
    }

    @Test
    void testAddFavoriteArtist_Success() throws Exception {
        // Dado: Una solicitud para agregar un artista favorito
        Integer userId = 1;
        AddFavoriteArtistRequest request = AddFavoriteArtistRequest.builder()
                .spotifyId("2ye2Wgw4gimLv2eAKyk1NB")
                .build();

        ArtistResponse response = ArtistResponse.builder()
                .spotifyId("2ye2Wgw4gimLv2eAKyk1NB")
                .name("Metallica")
                .build();

        when(preferenceService.addFavoriteArtist(eq(userId), eq("2ye2Wgw4gimLv2eAKyk1NB")))
                .thenReturn(response);

        // Cuando: Se agrega un artista a favoritos
        mockMvc.perform(post("/api/v1/users/{userId}/preferences/artists", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Entonces: Se retorna la información del artista agregado
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.spotifyId", is("2ye2Wgw4gimLv2eAKyk1NB")))
                .andExpect(jsonPath("$.name", is("Metallica")));

        verify(preferenceService, times(1)).addFavoriteArtist(userId, "2ye2Wgw4gimLv2eAKyk1NB");
    }

    @Test
    void testRemoveFavoriteArtist_Success() throws Exception {
        // Dado: Un artista favorito existente
        Integer userId = 1;
        Integer artistId = 5;

        SuccessResponse response = SuccessResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Artist removed successfully")
                .details("Metallica")
                .build();

        when(preferenceService.removeFavoriteArtist(userId, artistId)).thenReturn(response);

        // Cuando: Se elimina el artista de favoritos
        mockMvc.perform(delete("/api/v1/users/{userId}/preferences/artists/{artistId}", userId, artistId))
                // Entonces: Se retorna un mensaje de éxito
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Artist removed successfully")))
                .andExpect(jsonPath("$.details", is("Metallica")));

        verify(preferenceService, times(1)).removeFavoriteArtist(userId, artistId);
    }

    // ===== TESTS DE GÉNEROS FAVORITOS =====

    @Test
    void testGetFavoriteGenres_WithPagination_Success() throws Exception {
        // Dado: Una página de géneros favoritos
        Integer userId = 1;
        List<MusicGenreResponse> genres = Arrays.asList(
                MusicGenreResponse.builder()
                        .musicGenreId(1)
                        .name("Rock")
                        .build(),
                MusicGenreResponse.builder()
                        .musicGenreId(2)
                        .name("Metal")
                        .build()
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<MusicGenreResponse> page = new PageImpl<>(genres, pageable, genres.size());

        when(preferenceService.getFavoriteGenres(eq(userId), any(Pageable.class))).thenReturn(page);

        // Cuando: Se solicitan los géneros favoritos con paginación
        mockMvc.perform(get("/api/v1/users/{userId}/preferences/genres", userId)
                .param("page", "0")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
                // Entonces: Se obtiene la página de géneros correctamente
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name", is("Rock")))
                .andExpect(jsonPath("$.content[1].name", is("Metal")))
                .andExpect(jsonPath("$.totalElements", is(2)));

        verify(preferenceService, times(1)).getFavoriteGenres(eq(userId), any(Pageable.class));
    }

    @Test
    void testAddFavoriteGenre_ByGenreId_Success() throws Exception {
        // Dado: Una solicitud para agregar un género por ID
        Integer userId = 1;
        AddFavoriteGenreRequest request = AddFavoriteGenreRequest.builder()
                .genreId(3)
                .build();

        MusicGenreResponse response = MusicGenreResponse.builder()
                .musicGenreId(3)
                .name("Pop")
                .build();

        when(preferenceService.addFavoriteGenre(eq(userId), any(AddFavoriteGenreRequest.class)))
                .thenReturn(response);

        // Cuando: Se agrega un género a favoritos
        mockMvc.perform(post("/api/v1/users/{userId}/preferences/genres", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Entonces: Se retorna la información del género agregado
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.musicGenreId", is(3)))
                .andExpect(jsonPath("$.name", is("Pop")));

        verify(preferenceService, times(1)).addFavoriteGenre(eq(userId), any(AddFavoriteGenreRequest.class));
    }

    @Test
    void testAddFavoriteGenre_ByGenreName_Success() throws Exception {
        // Dado: Una solicitud para agregar un género por nombre
        Integer userId = 1;
        AddFavoriteGenreRequest request = AddFavoriteGenreRequest.builder()
                .genreName("Jazz")
                .build();

        MusicGenreResponse response = MusicGenreResponse.builder()
                .musicGenreId(4)
                .name("Jazz")
                .build();

        when(preferenceService.addFavoriteGenre(eq(userId), any(AddFavoriteGenreRequest.class)))
                .thenReturn(response);

        // Cuando: Se agrega un género a favoritos por nombre
        mockMvc.perform(post("/api/v1/users/{userId}/preferences/genres", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Entonces: Se retorna la información del género agregado
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.musicGenreId", is(4)))
                .andExpect(jsonPath("$.name", is("Jazz")));

        verify(preferenceService, times(1)).addFavoriteGenre(eq(userId), any(AddFavoriteGenreRequest.class));
    }

    @Test
    void testRemoveFavoriteGenre_ByGenreId_Success() throws Exception {
        // Dado: Un género favorito existente
        Integer userId = 1;
        DeleteFavoriteGenreRequest request = DeleteFavoriteGenreRequest.builder()
                .genreId(2)
                .build();

        SuccessResponse response = SuccessResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Genre removed successfully")
                .details("Metal")
                .build();

        when(preferenceService.removeFavoriteGenre(eq(userId), any(DeleteFavoriteGenreRequest.class)))
                .thenReturn(response);

        // Cuando: Se elimina el género de favoritos por ID
        mockMvc.perform(delete("/api/v1/users/{userId}/preferences/genres", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Entonces: Se retorna un mensaje de éxito
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Genre removed successfully")))
                .andExpect(jsonPath("$.details", is("Metal")));

        verify(preferenceService, times(1)).removeFavoriteGenre(eq(userId), any(DeleteFavoriteGenreRequest.class));
    }

    @Test
    void testRemoveFavoriteGenre_ByGenreName_Success() throws Exception {
        // Dado: Un género favorito existente
        Integer userId = 1;
        DeleteFavoriteGenreRequest request = DeleteFavoriteGenreRequest.builder()
                .genreName("Rock")
                .build();

        SuccessResponse response = SuccessResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Genre removed successfully")
                .details("Rock")
                .build();

        when(preferenceService.removeFavoriteGenre(eq(userId), any(DeleteFavoriteGenreRequest.class)))
                .thenReturn(response);

        // Cuando: Se elimina el género de favoritos por nombre
        mockMvc.perform(delete("/api/v1/users/{userId}/preferences/genres", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Entonces: Se retorna un mensaje de éxito
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Genre removed successfully")))
                .andExpect(jsonPath("$.details", is("Rock")));

        verify(preferenceService, times(1)).removeFavoriteGenre(eq(userId), any(DeleteFavoriteGenreRequest.class));
    }

    @Test
    void testGetAllGenres_WithPagination_Success() throws Exception {
        // Dado: Una lista de todos los géneros disponibles
        Integer userId = 1;
        List<MusicGenreResponse> allGenres = Arrays.asList(
                MusicGenreResponse.builder().musicGenreId(1).name("Rock").build(),
                MusicGenreResponse.builder().musicGenreId(2).name("Metal").build(),
                MusicGenreResponse.builder().musicGenreId(3).name("Pop").build(),
                MusicGenreResponse.builder().musicGenreId(4).name("Jazz").build(),
                MusicGenreResponse.builder().musicGenreId(5).name("Blues").build()
        );

        when(preferenceService.getAllGenres()).thenReturn(allGenres);

        // Cuando: Se solicitan todos los géneros disponibles con paginación
        mockMvc.perform(get("/api/v1/users/{userId}/preferences/genres/available", userId)
                .param("page", "0")
                .param("pageSize", "20")
                .accept(MediaType.APPLICATION_JSON))
                // Entonces: Se obtienen los géneros en formato paginado
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.content[0].name", is("Rock")))
                .andExpect(jsonPath("$.content[1].name", is("Metal")))
                .andExpect(jsonPath("$.content[2].name", is("Pop")))
                .andExpect(jsonPath("$.content[3].name", is("Jazz")))
                .andExpect(jsonPath("$.content[4].name", is("Blues")))
                .andExpect(jsonPath("$.totalElements", is(5)))
                .andExpect(jsonPath("$.size", is(20)))
                .andExpect(jsonPath("$.number", is(0)));

        verify(preferenceService, times(1)).getAllGenres();
    }
}