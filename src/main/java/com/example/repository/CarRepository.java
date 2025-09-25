package com.example.repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.example.model.Car;


@Repository
public class CarRepository {
    private final Map<UUID, Car> data = new ConcurrentHashMap<>();
    
    
    
    public Car add(Car car) {
        if (car.getId() == null) {
            car.setId(UUID.randomUUID());
        }
        data.put(car.getId(), car);
        return car;
    }


    public List<Car> findAll() {
        return new ArrayList<>(data.values());
    }

    public Optional<Car> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }
    public Car save(Car car) {
        if (car.getId() == null)
            car.setId(UUID.randomUUID());
        data.put(car.getId(), car);
        return car;
    }

    public boolean deleteById(UUID id) {
        return data.remove(id) != null;
    }

    public Optional<Car> findByEmail(String email) {
        return data.values().stream()
                .filter(st -> st.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}