package com.example.rockStadium.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "alumnos")

public class UserPreference {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "preference_id", nullable = false, length = 32)
    private Integer preferenceId;

    @Column(name = "search_radius", nullable = false, length = 100)
    private Integer searchRadius;

    @Column(name = "email_notifications", nullable = false, length = 120)
    private Boolean emailNotifications;
}
