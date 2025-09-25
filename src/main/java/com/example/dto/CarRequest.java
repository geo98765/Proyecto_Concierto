package com.example.dto;


import jakarta.validation.constraints.NotBlank;

public record CarRequest(
    @NotBlank(message = "El nombre de la marca es obligatorio")
    String brand,

    @NotBlank(message = "Año inválido")
    String year,

    @NotBlank(message = "El email es obligatorio")
    String email
) {}


