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
@Table(name = "artists_genres")
public class ArtistGenre {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_genre_id", nullable = false)
    private Integer artistGenreId;
    
    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;
    
    @ManyToOne
    @JoinColumn(name = "music_genre_id")
    private MusicGenre musicGenre;
    
    // REMOVIDO: Esta relación causaba recursión infinita
    // @OneToMany(mappedBy = "artistGenre", cascade = CascadeType.ALL)
    // private List<Artist> artists;
}