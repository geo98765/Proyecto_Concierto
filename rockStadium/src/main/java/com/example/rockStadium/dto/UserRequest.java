package com.example.rockStadium.dto;

import jakarta.validation.constraints.Email;
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
public class UserRequest {
    
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe ser válido")
    @Size(max = 255)
    private String email;
    
    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 8, message = "La contraseña debe tener mínimo 8 caracteres")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).+$", 
             message = "La contraseña debe contener al menos una mayúscula y un número")
    private String password;
    
    @NotBlank(message = "El nombre es requerido")
    @Size(max = 100)
    private String name;
    
    @NotBlank(message = "La ubicación es requerida")
    private String location;
}
