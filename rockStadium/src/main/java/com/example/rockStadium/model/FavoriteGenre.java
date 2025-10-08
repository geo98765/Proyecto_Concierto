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
@Table(name = "favorite_genres")
public class FavoriteGenre {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_genre_id", nullable = false)
    private Integer favoriteGenreId;
    
    @ManyToOne
    @JoinColumn(name = "music_genre_id")
    private MusicGenre musicGenre;
    
    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;
}