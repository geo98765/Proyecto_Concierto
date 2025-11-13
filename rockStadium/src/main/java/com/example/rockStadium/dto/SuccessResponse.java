package com.example.rockStadium.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard success response for operations that don't return data
 * Used for DELETE operations and other actions that only need confirmation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessResponse {
    
    @Schema(description = "Timestamp of the operation", example = "2025-10-16T23:00:00")
    private LocalDateTime timestamp;
    
    @Schema(description = "HTTP status code", example = "200")
    private Integer status;
    
    @Schema(description = "Success message", example = "Artist removed successfully")
    private String message;
    
    @Schema(description = "Additional details (optional)", example = "The Beatles")
    private String details;
    
    /**
     * Create a simple success response
     */
    public static SuccessResponse of(String message) {
        return SuccessResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message(message)
                .build();
    }
    
    /**
     * Create a success response with details
     */
    public static SuccessResponse of(String message, String details) {
        return SuccessResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message(message)
                .details(details)
                .build();
    }
}