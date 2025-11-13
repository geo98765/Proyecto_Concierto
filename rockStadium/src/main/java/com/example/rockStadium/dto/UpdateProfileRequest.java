package com.example.rockStadium.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    
    @Size(max = 100)
    private String name;
    
    @Email(message = "El email debe ser válido")
    @Size(max = 255)
    private String email;
    
    private String location;
}
