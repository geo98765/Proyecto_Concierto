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
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id", nullable = false)
    private Integer notificationId;
    
    @Column(name = "name", length = 100)
    private String name;
    
    @Column(name = "notification_type", length = 100)
    private String notificationType;
    
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;
    
    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;
    
    @ManyToOne
    @JoinColumn(name = "concert_id")
    private Concert concert;
}
