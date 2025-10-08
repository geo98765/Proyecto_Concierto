package com.example.rockStadium.service;

import com.example.rockStadium.dto.VenueRequest;
import com.example.rockStadium.dto.VenueResponse;
import com.example.rockStadium.dto.VenueSearchRequest;
import com.example.rockStadium.mapper.VenueMapper;
import com.example.rockStadium.model.Venue;
import com.example.rockStadium.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VenueServiceImpl implements VenueService {
    
    private final VenueRepository venueRepository;
    private final VenueMapper venueMapper;
    
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
}
