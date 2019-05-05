package com.example.demo.repository;

import com.example.demo.model.Car;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CarRepository extends ReactiveMongoRepository<Car, String> {
    Mono<Car> findById(int id);
}
