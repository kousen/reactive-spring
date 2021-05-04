package com.oreilly.reactiveofficers.controllers;

import com.oreilly.reactiveofficers.dao.OfficerRepository;
import com.oreilly.reactiveofficers.entities.Officer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/officers")
public class OfficerController {
    private final OfficerRepository repository;

    @Autowired
    public OfficerController(OfficerRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Flux<Officer> getAllOfficers() {
        return repository.findAll().log();
    }

    @GetMapping("{id}")
    public Mono<Officer> getOfficer(@PathVariable String id) {
        return repository.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Officer> saveOfficer(@RequestBody Officer officer) {
        return repository.save(officer);
    }

    @PutMapping("{id}")
    public Mono<ResponseEntity<Officer>> updateOfficer(@PathVariable(value = "id") String id,
                                                       @RequestBody Officer officer) {
        return repository.findById(id)
                .flatMap(existingOfficer -> {
                    existingOfficer.setRank(officer.getRank());
                    existingOfficer.setFirst(officer.getFirst());
                    existingOfficer.setLast(officer.getLast());
                    return repository.save(existingOfficer);
                })
                .map(updateOfficer -> new ResponseEntity<>(updateOfficer, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    // @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteOfficer(@PathVariable(value = "id") String id) {
        return repository.deleteById(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping
    public Mono<Void> deleteAllOfficers() {
        return repository.deleteAll();
    }
}
