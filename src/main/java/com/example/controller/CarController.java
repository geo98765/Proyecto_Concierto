package com.example.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.CarRequest;
import com.example.dto.CarResponse;
import com.example.service.CarService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("api/v1/cars")
public class CarController {
    
    private final CarService service;

    public CarController(CarService service) {
        this.service = service;
    }

    @GetMapping
    public List<CarResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public CarResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarResponse add(@Valid @RequestBody CarRequest request) {
        return service.add(request);
    }
    
    @PutMapping("/{id}")
    public CarResponse findById(@PathVariable UUID id, @Valid @RequestBody CarRequest request) {
        return service.findById(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable UUID id) {
        service.deleteById(id);
    }
}
