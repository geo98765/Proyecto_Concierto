package com.example.model;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@Entity
//@Table(name = "Nombre de la tabla")
public class Car {
    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //@COlum(name = "Nombre de la columna, nullable = false, length = 32")
    private UUID id;
    private String brand;
    private String year;
    private String email;
    
}
