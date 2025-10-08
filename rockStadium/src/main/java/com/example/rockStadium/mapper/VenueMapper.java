package com.example.rockStadium.mapper;

import com.example.rockStadium.dto.VenueRequest;
import com.example.rockStadium.dto.VenueResponse;
import com.example.rockStadium.dto.WeatherResponse;
import com.example.rockStadium.model.Venue;
import com.example.rockStadium.model.Weather;
import org.springframework.stereotype.Component;

@Component
public class VenueMapper {
    
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
    
    public VenueResponse toResponse(Venue venue) {
        return VenueResponse.builder()
                .venueId(venue.getVenueId())
                .name(venue.getName())
                .city(venue.getCity())
                .latitude(venue.getLatitude())
                .longitude(venue.getLongitude())
                .capacity(venue.getCapacity())
                .parkingAvailable(venue.getParkingAvailable())
                .weather(venue.getWeather() != null ? toWeatherResponse(venue.getWeather()) : null)
                .build();
    }
    
    public WeatherResponse toWeatherResponse(Weather weather) {
        return WeatherResponse.builder()
                .weatherId(weather.getWeatherId())
                .weatherType(weather.getWeatherType())
                .temperature(weather.getTemperature())
                .humidity(weather.getHumidity())
                .precipitation(weather.getPrecipitation())
                .wind(weather.getWind())
                .build();
    }
}