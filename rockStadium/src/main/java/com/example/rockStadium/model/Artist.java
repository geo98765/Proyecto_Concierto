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
@Table(name = "artists")
public class Artist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_id", nullable = false)
    private Integer artistId;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @Column(name = "spotify_id", length = 100, unique = true)
    private String spotifyId;
    
    // REMOVIDO: La relación con Genre que causaba el error
    // @ManyToOne
    // @JoinColumn(name = "artist_genre_id")
    // private ArtistGenre artistGenre;
    
    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL)
    private List<ArtistGenre> artistGenres;
    
    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL)
    private List<ConcertArtist> concertArtists;
    
    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL)
    private List<FavoriteArtist> favoriteArtists;
}