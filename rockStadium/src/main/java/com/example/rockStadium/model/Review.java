package com.example.rockStadium.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reviews")
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", nullable = false)
    private Integer reviewId;
    
    @Column(name = "review_type", length = 50)
    private String reviewType;
    
    @Column(name = "user_comments", columnDefinition = "TEXT")
    private String userComments;
    
    @Column(name = "stars_number")
    private Integer starsNumber;
    
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<Venue> venues;
    
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<HotelVenue> hotelVenues;
    
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<Transport> transports;
}