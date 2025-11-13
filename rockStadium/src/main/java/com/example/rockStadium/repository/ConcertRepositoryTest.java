package com.example.rockStadium.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rockStadium.model.Concert;

@Repository
public interface ConcertRepositoryTest extends JpaRepository<Concert, Integer> {
    
    List<Concert> findByStatus(String status);
    List<Concert> findByDateTimeAfter(LocalDateTime dateTime);
    List<Concert> findByNameContainingIgnoreCase(String name);
    List<Concert> findByVenueVenueId(Integer venueId);
}