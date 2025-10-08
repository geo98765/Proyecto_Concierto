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
@Table(name = "profiles")
public class Profile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id", nullable = false)
    private Integer profileId;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<SavedConcert> savedConcerts;
    
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Notification> notifications;
    
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<FavoriteGenre> favoriteGenres;
    
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<FavoriteArtist> favoriteArtists;
    
    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL)
    private UserPreference userPreference;
    
    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL)
    private ProfileLocation profileLocation;
}
