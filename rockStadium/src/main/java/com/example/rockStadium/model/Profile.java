package com.example.rockStadium.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
    
    @JsonManagedReference
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<SavedConcert> savedConcerts;
    
    @JsonManagedReference
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Notification> notifications;
    
    @JsonManagedReference
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<FavoriteGenre> favoriteGenres;
    
    @JsonManagedReference
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<FavoriteArtist> favoriteArtists;
    
    @JsonManagedReference
    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL)
    private UserPreference userPreference;
    
    @JsonManagedReference
    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL)
    private ProfileLocation profileLocation;
}