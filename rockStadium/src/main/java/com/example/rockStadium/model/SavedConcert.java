package com.example.rockStadium.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "saved_concerts")
public class SavedConcert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "saved_concert_id", nullable = false)
    private Integer savedConcertId;
    
    @ManyToOne
    @JoinColumn(name = "concert_id")
    private Concert concert;
    
    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;
}