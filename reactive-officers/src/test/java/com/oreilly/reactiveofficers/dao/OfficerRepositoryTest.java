package com.oreilly.reactiveofficers.dao;

import com.oreilly.reactiveofficers.entities.Officer;
import com.oreilly.reactiveofficers.entities.Rank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class OfficerRepositoryTest {
    @Autowired
    private OfficerRepository repository;

    private final List<Officer> officers = Arrays.asList(
            new Officer(Rank.CAPTAIN, "James", "Kirk"),
            new Officer(Rank.CAPTAIN, "Jean-Luc", "Picard"),
            new Officer(Rank.CAPTAIN, "Benjamin", "Sisko"),
            new Officer(Rank.CAPTAIN, "Kathryn", "Janeway"),
            new Officer(Rank.CAPTAIN, "Jonathan", "Archer"));

    @BeforeEach
    public void setUp() {
        repository.deleteAll()
                .thenMany(Flux.fromIterable(officers))
                .flatMap(repository::save)
                .then()
                .block();
    }

    @Test
    public void save() {
        Officer lorca = new Officer(Rank.CAPTAIN, "Gabriel", "Lorca");
        StepVerifier.create(repository.save(lorca))
                .expectNextMatches(officer -> !officer.getId().equals(""))
                .verifyComplete();
    }

    @Test
    public void findAll() {
        StepVerifier.create(repository.findAll().log())
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void findById() {
        officers.stream()
                .map(Officer::getId)
                .forEach(id -> StepVerifier.create(repository.findById(id))
                        .expectNextCount(1)
                        .verifyComplete());
    }

    @Test
    public void findByIdNotExist() {
        StepVerifier.create(repository.findById("xyz"))
                .verifyComplete();
    }

    @Test
    public void count() {
        StepVerifier.create(repository.count())
                .expectNext(5L)
                .verifyComplete();
    }

    @Test
    public void findByRank() {
        StepVerifier.create(
                repository.findByRank(Rank.CAPTAIN)
                        .map(Officer::getRank)
                        .distinct())
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier.create(
                repository.findByRank(Rank.ENSIGN)
                        .map(Officer::getRank)
                        .distinct())
                .verifyComplete();

    }

    @Test
    public void findByLast() {
        officers.stream()
                .map(Officer::getLast)
                .forEach(lastName -> StepVerifier.create(repository.findByLast(lastName))
                        .expectNextMatches(officer -> officer.getLast().equals(lastName))
                        .verifyComplete());
    }

}