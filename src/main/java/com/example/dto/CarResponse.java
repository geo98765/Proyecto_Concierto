package com.example.dto;

import java.util.UUID;

public record CarResponse(
        UUID id,
        String brand,
        String year,
        String email
) { }



