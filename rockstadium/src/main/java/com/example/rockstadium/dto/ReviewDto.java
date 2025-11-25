package com.example.rockstadium.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para reseñas de usuarios de un lugar
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private String authorName;
    private Integer rating;
    private String text;
    private String relativeTimeDescription;
}