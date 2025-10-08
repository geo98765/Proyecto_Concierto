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