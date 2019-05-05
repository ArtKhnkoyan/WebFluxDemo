package com.example.demo.rest;

import com.example.demo.model.Car;
import com.example.demo.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/cars")
public class CarEndPoint {

    private CarRepository carRepository;

    @Autowired
    public CarEndPoint(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @PostMapping
    public Mono<Car> saveCar(@RequestBody Mono<Car> carMono) {
        carMono.map(car -> carRepository.save(car)).subscribe();
        return Mono.empty();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Car>> findById(@PathVariable("id") int id) {
        return carRepository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Flux<Car> findAll() {
        return carRepository.findAll();
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Car>> update(@PathVariable("id") int id, @RequestBody Mono<Car> carMono) {
        return carRepository.findById(id)
                .flatMap(car -> {
                    car.setPrice(carMono.block().getPrice());
                    return carRepository.save(car);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable("id") int id) {
        return carRepository.findById(id)
                .flatMap(car ->
                        carRepository.delete(car)
                                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK))))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping
    public Mono<Void> deleteAll() {
        return carRepository.deleteAll();
    }

}
