package com.example.demo.rest;

import com.example.demo.model.Car;
import com.example.demo.model.Person;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/persons")
@AllArgsConstructor
public class PersonEndPoint {

    private PersonRepository personRepository;
    private CarRepository carRepository;


    @PostMapping
    public Mono<Person> savePerson(@RequestBody Mono<Person> personMono) {
        personMono.map(person -> {
            Flux<Car> carsFlux = carRepository.findAll();
            carsFlux.buffer().flatMap(cars -> {
                List<Car> carList = IntStream.range(0, cars.size())
                        .filter(n -> n % 2 == 0)
                        .mapToObj(cars::get)
                        .collect(Collectors.toList());
                int balance = 0;
                for (Car car : carList) {
                    balance += car.getPrice();
                }
                person.setCars(carList);
                person.setBalance(balance);
                return personRepository.save(person);
            });
            return Mono.empty();
        });
        return Mono.empty();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Person>> findById(@PathVariable("id") int id) {
        return personRepository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Flux<Person> findAll() {
        return personRepository.findAll();
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Person>> update(@PathVariable("id") int id, @RequestBody Mono<Person> personMono) {
        return personRepository.findById(id)
                .flatMap(person -> {
                    person.setName(personMono.block().getName());
                    person.setAge(personMono.block().getAge());
                    return personRepository.save(person);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable("id") int id) {
        return personRepository.findById(id)
                .flatMap(person ->
                        personRepository.delete(person)
                                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK))))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping
    public Mono<Void> deleteAll() {
        return personRepository.deleteAll();
    }
}
