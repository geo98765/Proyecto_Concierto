package com.example.rockStadium.mapper;

import org.springframework.stereotype.Component;

import com.example.rockStadium.dto.VenueRequest;
import com.example.rockStadium.dto.VenueResponse;
import com.example.rockStadium.dto.WeatherResponse;
import com.example.rockStadium.model.Venue;

@Component
public class VenueMapper {
    
    /**
     * Convierte VenueRequest a entidad Venue
     */
    public Venue toEntity(VenueRequest request) {
        return Venue.builder()
                .name(request.getName())
                .city(request.getCity())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .capacity(request.getCapacity())
                .parkingAvailable(request.getParkingAvailable())
                .build();
    }
    
    /**
     * Convierte entidad Venue a VenueResponse (sin clima)
     */
    public VenueResponse toResponse(Venue venue) {
        return VenueResponse.builder()
                .venueId(venue.getVenueId())
                .name(venue.getName())
                .city(venue.getCity())
                .latitude(venue.getLatitude())
                .longitude(venue.getLongitude())
                .capacity(venue.getCapacity())
                .parkingAvailable(venue.getParkingAvailable())
                .weather(null) // El clima se obtendrá por separado si se necesita
                .build();
    }
    
    /**
     * Convierte entidad Venue a VenueResponse CON clima de Google
     */
    public VenueResponse toResponseWithWeather(Venue venue, WeatherResponse weather) {
        return VenueResponse.builder()
                .venueId(venue.getVenueId())
                .name(venue.getName())
                .city(venue.getCity())
                .latitude(venue.getLatitude())
                .longitude(venue.getLongitude())
                .capacity(venue.getCapacity())
                .parkingAvailable(venue.getParkingAvailable())
                .weather(weather) // Clima de Google Weather API
                .build();
    }
    
    /**
     * Actualiza una entidad Venue existente con datos de VenueRequest
     */
    public void updateEntity(Venue venue, VenueRequest request) {
        if (request.getName() != null) {
            venue.setName(request.getName());
        }
        if (request.getCity() != null) {
            venue.setCity(request.getCity());
        }
        if (request.getLatitude() != null) {
            venue.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            venue.setLongitude(request.getLongitude());
        }
        if (request.getCapacity() != null) {
            venue.setCapacity(request.getCapacity());
        }
        if (request.getParkingAvailable() != null) {
            venue.setParkingAvailable(request.getParkingAvailable());
        }
    }
}