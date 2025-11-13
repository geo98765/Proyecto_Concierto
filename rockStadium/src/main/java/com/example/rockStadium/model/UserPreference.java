package com.example.rockStadium.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "user_preferences")
public class UserPreference {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_preference_id", nullable = false)
    private Integer userPreferenceId;
    
    @Column(name = "search_radius", precision = 5, scale = 2)
    private BigDecimal searchRadius;
    
    @Column(name = "email_notifications")
    private Boolean emailNotifications;
    
    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;
}