package com.example.rockstadium.mapper;

import org.springframework.stereotype.Component;

import com.example.rockstadium.dto.ConcertRequest;
import com.example.rockstadium.dto.ConcertResponse;
import com.example.rockstadium.model.Concert;
import com.example.rockstadium.model.Venue;

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