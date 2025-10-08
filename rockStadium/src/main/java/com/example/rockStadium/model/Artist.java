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
@Table(name = "artists")
public class Artist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_id", nullable = false)
    private Integer artistId;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @Column(name = "spotify_id", length = 100)
    private String spotifyId;
    
    @ManyToOne
    @JoinColumn(name = "artist_genre_id")
    private ArtistGenre artistGenre;
    
    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL)
    private List<ConcertArtist> concertArtists;
    
    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL)
    private List<FavoriteArtist> favoriteArtists;
}