package com.oreilly.reactiveofficers.controllers;

import com.oreilly.reactiveofficers.dao.OfficerRepository;
import com.oreilly.reactiveofficers.entities.Officer;
import com.oreilly.reactiveofficers.entities.Rank;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class HandlerAndRouterTests {
    @Autowired
    private WebTestClient client;

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
                .doOnNext(System.out::println)
                .then()
                .block();
    }

    @Test
    public void testCreateOfficer() {
        Officer officer = new Officer(Rank.LIEUTENANT, "Hikaru", "Sulu");
        client.post()
                .uri("/route")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(officer), Officer.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id")
                .isNotEmpty()
                .jsonPath("$.first")
                .isEqualTo("Hikaru")
                .jsonPath("$.last")
                .isEqualTo("Sulu");
    }

    @Test
    public void testGetAllOfficers() {
        client.get()
                .uri("/route")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Officer.class);
    }

    @Test
    public void testGetSingleOfficer() {
        Officer officer = repository.save(new Officer(Rank.ENSIGN, "Wesley", "Crusher"))
                .block();

        client.get()
                //.uri("/route/{id}", Collections.singletonMap("id", officer.getId()))
                .uri("/route/{id}", officer.getId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .consumeWith(response ->
                        Assertions.assertThat(response.getResponseBody())
                                .isNotNull());
    }
}