package com.example.service;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.dto.CarRequest;
import com.example.dto.CarResponse;
import com.example.model.Car;
import com.example.repository.CarRepository;


@Service
public class CarService {
    private final CarRepository repository;

    public CarService(CarRepository repository) {
        this.repository = repository;
    }
    public CarResponse add(CarRequest request) {
        Car car = Car.builder()
            .brand(request.brand())
            .year(request.year())
            .email(request.email())
            .build();
return toResponse(repository.add(car));    
}

 public CarResponse findById(UUID id, CarRequest req) {
        Car existente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado: " + id));

        // si cambia email, validamos unicidad
        if (!existente.getEmail().equalsIgnoreCase(req.email())) {
            repository.findByEmail(req.email()).ifPresent(s -> {
                throw new IllegalArgumentException("Ya existe un estudiante con ese email");
            });
        }

        existente.setBrand(req.brand());
        existente.setYear(req.year());
        existente.setEmail(req.email());
        return toResponse(repository.save(existente));
    }

    public void deleteById(UUID id) {
        if (!repository.deleteById(id)) {
            throw new IllegalArgumentException("Estudiante no encontrado: " + id);
        }
    }

    public List<CarResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public CarResponse findById(UUID id) {
        Car s = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado: " + id));
        return toResponse(s);
    }


    private CarResponse toResponse(Car car) {
        return new CarResponse(
            car.getId(),
            car.getBrand(),
            car.getYear(),
            car.getEmail()
        );
    }
    
}
