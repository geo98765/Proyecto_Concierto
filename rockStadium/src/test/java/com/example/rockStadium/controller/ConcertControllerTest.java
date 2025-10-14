// package com.example.rockStadium.controller;

// import com.example.rockStadium.dto.ConcertRequest;
// import com.example.rockStadium.dto.ConcertResponse;
// import com.example.rockStadium.service.ConcertService;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.junit.jupiter.api.Test;
// import org.mockito.ArgumentMatchers;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;

// import java.math.BigDecimal;
// import java.time.LocalDateTime;
// import java.util.Arrays;
// import java.util.List;

// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.when;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.times;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
// import static org.hamcrest.Matchers.*;

// @WebMvcTest(controllers = ConcertControllerTest.class)
// class ConcertControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private ConcertService concertService; // Mockeado en el contexto de prueba

//     @Autowired
//     private ObjectMapper objectMapper;

//     @Test
//     void testFindAll_Success() throws Exception {
//         ConcertResponse concert1 = ConcertResponse.builder()
//                 .concertId(1)
//                 .name("Rock Festival 2025")
//                 .dateTime(LocalDateTime.of(2025, 12, 31, 20, 0))
//                 .status("ACTIVO")
//                 .price(BigDecimal.valueOf(500))
//                 .venueName("Foro Sol")
//                 .venueCity("Ciudad de México")
//                 .build();

//         ConcertResponse concert2 = ConcertResponse.builder()
//                 .concertId(2)
//                 .name("Metal Night")
//                 .dateTime(LocalDateTime.of(2025, 11, 15, 19, 30))
//                 .status("ACTIVO")
//                 .price(BigDecimal.valueOf(350))
//                 .venueName("Palacio de los Deportes")
//                 .venueCity("Ciudad de México")
//                 .build();

//         List<ConcertResponse> concerts = Arrays.asList(concert1, concert2);
//         when(concertService.findAll()).thenReturn(concerts);

//         mockMvc.perform(get("/api/concerts")
//                 .accept(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$", hasSize(2)))
//                 .andExpect(jsonPath("$[0].name", is("Rock Festival 2025")))
//                 .andExpect(jsonPath("$[0].status", is("ACTIVO")))
//                 .andExpect(jsonPath("$[1].name", is("Metal Night")));
//     }

//     @Test
//     void testFindById_Success() throws Exception {
//         Integer concertId = 1;
//         ConcertResponse concert = ConcertResponse.builder()
//                 .concertId(concertId)
//                 .name("Pop Concert")
//                 .dateTime(LocalDateTime.of(2025, 10, 20, 21, 0))
//                 .status("ACTIVO")
//                 .price(BigDecimal.valueOf(450))
//                 .venueName("Arena CDMX")
//                 .build();

//         when(concertService.findById(concertId)).thenReturn(concert);

//         mockMvc.perform(get("/api/concerts/{concertId}", concertId)
//                 .accept(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.concertId", is(1)))
//                 .andExpect(jsonPath("$.name", is("Pop Concert")))
//                 .andExpect(jsonPath("$.price", is(450)));
//     }

//     @Test
//     void testCreate_Success() throws Exception {
//         ConcertRequest request = ConcertRequest.builder()
//                 .name("Jazz Night")
//                 .dateTime(LocalDateTime.of(2025, 12, 1, 20, 0))
//                 .status("ACTIVO")
//                 .price(BigDecimal.valueOf(300))
//                 .venueId(1)
//                 .build();

//         ConcertResponse response = ConcertResponse.builder()
//                 .concertId(3)
//                 .name("Jazz Night")
//                 .dateTime(LocalDateTime.of(2025, 12, 1, 20, 0))
//                 .status("ACTIVO")
//                 .price(BigDecimal.valueOf(300))
//                 .venueName("Venue Test")
//                 .build();

//         // usamos ArgumentMatchers.any(...) explícito para evitar ambigüedad con otros 'any'
//         when(concertService.create(ArgumentMatchers.any(ConcertRequest.class))).thenReturn(response);

//         mockMvc.perform(post("/api/concerts")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(request)))
//                 .andExpect(status().isCreated())
//                 .andExpect(header().string("Location", "/api/concerts/3"))
//                 .andExpect(jsonPath("$.concertId", is(3)))
//                 .andExpect(jsonPath("$.name", is("Jazz Night")));
//     }

//     @Test
//     void testUpdate_Success() throws Exception {
//         Integer concertId = 1;
//         ConcertRequest request = ConcertRequest.builder()
//                 .name("Updated Concert")
//                 .dateTime(LocalDateTime.of(2025, 11, 30, 19, 0))
//                 .status("ACTIVO")
//                 .price(BigDecimal.valueOf(400))
//                 .venueId(1)
//                 .build();

//         ConcertResponse response = ConcertResponse.builder()
//                 .concertId(concertId)
//                 .name("Updated Concert")
//                 .dateTime(LocalDateTime.of(2025, 11, 30, 19, 0))
//                 .status("ACTIVO")
//                 .price(BigDecimal.valueOf(400))
//                 .build();

//         when(concertService.update(eq(concertId), ArgumentMatchers.any(ConcertRequest.class))).thenReturn(response);

//         mockMvc.perform(put("/api/concerts/{concertId}", concertId)
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(request)))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.name", is("Updated Concert")));
//     }

//     @Test
//     void testDelete_Success() throws Exception {
//         Integer concertId = 1;

//         mockMvc.perform(delete("/api/concerts/{concertId}", concertId))
//                 .andExpect(status().isNoContent());

//         verify(concertService, times(1)).delete(concertId);
//     }
// }
