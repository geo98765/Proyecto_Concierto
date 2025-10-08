package com.example.rockStadium.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequest {
    
    @NotBlank(message = "La contraseña anterior es requerida")
    private String oldPassword;
    
    @NotBlank(message = "La nueva contraseña es requerida")
    @Size(min = 8, message = "La contraseña debe tener mínimo 8 caracteres")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).+$", 
             message = "La contraseña debe contener al menos una mayúscula y un número")
    private String newPassword;
}