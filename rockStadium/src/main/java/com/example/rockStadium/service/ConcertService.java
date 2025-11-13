package com.example.rockStadium.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.rockStadium.dto.ConcertRequest;
import com.example.rockStadium.dto.ConcertResponse;
import com.example.rockStadium.mapper.ConcertMapper;
import com.example.rockStadium.model.Concert;
import com.example.rockStadium.model.Venue;
import com.example.rockStadium.repository.ConcertRepositoryTest;
import com.example.rockStadium.repository.VenueRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConcertService {
    
    private final ConcertRepositoryTest concertRepository;
    private final VenueRepository venueRepository;
    private final ConcertMapper concertMapper;
    
    /**
     * Crear un nuevo concierto
     */
    @Transactional
    public ConcertResponse create(ConcertRequest request) {
        log.info("Creando concierto: {}", request.getName());
        
        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new RuntimeException("Venue no encontrado con ID: " + request.getVenueId()));
        
        Concert concert = concertMapper.toEntity(request, venue);
        concert = concertRepository.save(concert);
        
        return concertMapper.toResponse(concert);
    }
    
    /**
     * Buscar concierto por ID
     */
    public ConcertResponse findById(Integer concertId) {
        log.info("Buscando concierto con ID: {}", concertId);
        
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new RuntimeException("Concierto no encontrado con ID: " + concertId));
        
        return concertMapper.toResponse(concert);
    }
    
    /**
     * Obtener todos los conciertos
     */
    public List<ConcertResponse> findAll() {
        log.info("Obteniendo todos los conciertos");
        
        return concertRepository.findAll().stream()
                .map(concertMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Buscar conciertos por estado (ACTIVO, CANCELADO, etc.)
     */
    public List<ConcertResponse> findByStatus(String status) {
        log.info("Buscando conciertos con estado: {}", status);
        
        return concertRepository.findByStatus(status).stream()
                .map(concertMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener conciertos próximos (después de la fecha actual)
     */
    public List<ConcertResponse> findUpcoming() {
        log.info("Buscando conciertos próximos");
        
        return concertRepository.findByDateTimeAfter(LocalDateTime.now()).stream()
                .map(concertMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Buscar conciertos por nombre (búsqueda parcial)
     */
    public List<ConcertResponse> searchByName(String name) {
        log.info("Buscando conciertos por nombre: {}", name);
        
        return concertRepository.findByNameContainingIgnoreCase(name).stream()
                .map(concertMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Buscar conciertos por venue
     */
    public List<ConcertResponse> findByVenue(Integer venueId) {
        log.info("Buscando conciertos en venue: {}", venueId);
        
        return concertRepository.findByVenueVenueId(venueId).stream()
                .map(concertMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Actualizar un concierto existente
     */
    @Transactional
    public ConcertResponse update(Integer concertId, ConcertRequest request) {
        log.info("Actualizando concierto con ID: {}", concertId);
        
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new RuntimeException("Concierto no encontrado con ID: " + concertId));
        
        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new RuntimeException("Venue no encontrado con ID: " + request.getVenueId()));
        
        concert.setName(request.getName());
        concert.setDateTime(request.getDateTime());
        concert.setStatus(request.getStatus());
        concert.setPrice(request.getPrice());
        concert.setVenue(venue);
        
        concert = concertRepository.save(concert);
        
        return concertMapper.toResponse(concert);
    }
    
    /**
     * Eliminar un concierto
     */
    @Transactional
    public void delete(Integer concertId) {
        log.info("Eliminando concierto con ID: {}", concertId);
        
        if (!concertRepository.existsById(concertId)) {
            throw new RuntimeException("Concierto no encontrado con ID: " + concertId);
        }
        
        concertRepository.deleteById(concertId);
    }
}