package com.example.rockStadium.mapper;

import com.example.rockStadium.dto.ConcertRequest;
import com.example.rockStadium.dto.ConcertResponse;
import com.example.rockStadium.model.Concert;
import com.example.rockStadium.model.Venue;
import org.springframework.stereotype.Component;

@Component
public class ConcertMapper {
    
    public Concert toEntity(ConcertRequest request, Venue venue) {
        return Concert.builder()
                .name(request.getName())
                .dateTime(request.getDateTime())
                .status(request.getStatus())
                .price(request.getPrice())
                .venue(venue)
                .build();
    }
    
    public ConcertResponse toResponse(Concert concert) {
        return ConcertResponse.builder()
                .concertId(concert.getConcertId())
                .name(concert.getName())
                .dateTime(concert.getDateTime())
                .status(concert.getStatus())
                .price(concert.getPrice())
                .venueName(concert.getVenue() != null ? concert.getVenue().getName() : null)
                .venueCity(concert.getVenue() != null ? concert.getVenue().getCity() : null)
                .build();
    }
}