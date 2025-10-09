package com.example.rockStadium.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.rockStadium.dto.NearbySearchResponse;
import com.example.rockStadium.dto.PlaceInfoResponse;
import com.example.rockStadium.dto.VenueRequest;
import com.example.rockStadium.dto.VenueResponse;
import com.example.rockStadium.dto.VenueSearchRequest;
import com.example.rockStadium.mapper.VenueMapper;
import com.example.rockStadium.model.Venue;
import com.example.rockStadium.repository.VenueRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VenueServiceImpl implements VenueService {
    
    private final VenueRepository venueRepository;
    private final VenueMapper venueMapper;
    private final SerpApiService serpApiService;
    
    @Override
    @Transactional
    public VenueResponse createVenue(VenueRequest request) {
        Venue venue = venueMapper.toEntity(request);
        venue = venueRepository.save(venue);
        return venueMapper.toResponse(venue);
    }
    
    @Override
    @Transactional
    public VenueResponse updateVenue(Integer venueId, VenueRequest request) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado"));
        
        venue.setName(request.getName());
        venue.setCity(request.getCity());
        venue.setLatitude(request.getLatitude());
        venue.setLongitude(request.getLongitude());
        venue.setCapacity(request.getCapacity());
        venue.setParkingAvailable(request.getParkingAvailable());
        
        venue = venueRepository.save(venue);
        return venueMapper.toResponse(venue);
    }
    
    @Override
    public VenueResponse getVenueById(Integer venueId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado"));
        return venueMapper.toResponse(venue);
    }
    
    @Override
    public List<VenueResponse> getAllVenues() {
        return venueRepository.findAll().stream()
                .map(venueMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<VenueResponse> getVenuesByCity(String city) {
        return venueRepository.findByCity(city).stream()
                .map(venueMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<VenueResponse> searchVenuesNearby(VenueSearchRequest request) {
        return venueRepository.findVenuesWithinRadius(
                request.getLatitude(),
                request.getLongitude(),
                request.getRadius()
        ).stream()
                .map(venueMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deleteVenue(Integer venueId) {
        if (!venueRepository.existsById(venueId)) {
            throw new RuntimeException("Recinto no encontrado");
        }
        venueRepository.deleteById(venueId);
    }
    
    // NUEVOS MÉTODOS CON SERPAPI
    
    /**
     * Enriquece la información del venue con datos de SerpApi
     */
    @Override
    public VenueResponse getVenueWithEnrichedInfo(Integer venueId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado"));
        
        try {
            // Buscar información adicional del venue en Google Maps
            PlaceInfoResponse placeInfo = serpApiService.searchVenueInfo(
                venue.getName(), 
                venue.getCity()
            );
            
            log.info("Información enriquecida obtenida para: {}", venue.getName());
            
            // Aquí puedes mapear la información adicional al response
            VenueResponse response = venueMapper.toResponse(venue);
            // TODO: Agregar campos adicionales al VenueResponse si lo deseas
            
            return response;
        } catch (Exception e) {
            log.error("Error enriqueciendo información del venue: {}", e.getMessage());
            // Retornar información básica si falla la API
            return venueMapper.toResponse(venue);
        }
    }
    
    /**
     * Obtiene hoteles cercanos al venue usando SerpApi
     */
    @Override
    public NearbySearchResponse getNearbyHotels(Integer venueId, Integer radius) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado"));
        
        return serpApiService.searchNearbyHotels(
            venue.getLatitude(),
            venue.getLongitude(),
            radius
        );
    }
    
    /**
     * Obtiene restaurantes cercanos al venue
     */
    @Override
    public NearbySearchResponse getNearbyRestaurants(Integer venueId, Integer radius) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado"));
        
        return serpApiService.searchNearbyRestaurants(
            venue.getLatitude(),
            venue.getLongitude(),
            radius
        );
    }
    
    /**
     * Obtiene estacionamientos cercanos al venue
     */
    @Override
    public NearbySearchResponse getNearbyParkings(Integer venueId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado"));
        
        return serpApiService.searchNearbyParkings(
            venue.getLatitude(),
            venue.getLongitude(),
            2 // 2 km de radio por defecto
        );
    }
    
    /**
     * Obtiene transporte público cercano al venue
     */
    @Override
    public NearbySearchResponse getNearbyTransport(Integer venueId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado"));
        
        return serpApiService.searchNearbyTransport(
            venue.getLatitude(),
            venue.getLongitude()
        );
    }
}