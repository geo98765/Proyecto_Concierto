package com.example.rockStadium.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "music_genres")
public class MusicGenre {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "music_genre_id", nullable = false)
    private Integer musicGenreId;
    
    @Column(name = "name", length = 100)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @OneToMany(mappedBy = "musicGenre", cascade = CascadeType.ALL)
    private List<ArtistGenre> artistGenres;
    
    @OneToMany(mappedBy = "musicGenre", cascade = CascadeType.ALL)
    private List<FavoriteGenre> favoriteGenres;
}