package com.kousenit.reactivecustomers.controllers;

import com.kousenit.reactivecustomers.dao.CustomerRepository;
import com.kousenit.reactivecustomers.entities.Customer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class CustomerHandlerTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private DatabaseClient databaseClient;

    @BeforeEach
    void setUp() {
        var statements = List.of(
                """
                DROP TABLE IF EXISTS customer;
                CREATE TABLE customer(
                    id long generated always as identity primary key,
                    first_name VARCHAR(100) NOT NULL,
                    last_name VARCHAR(100) NOT NULL
                );
                INSERT INTO customer (first_name, last_name) VALUES ('Malcolm', 'Reynolds');
                INSERT INTO customer (first_name, last_name) VALUES ('Zoë', 'Washburne');
                INSERT INTO customer (first_name, last_name) VALUES ('Hoban', 'Washburne');
                INSERT INTO customer (first_name, last_name) VALUES ('Jayne', 'Cobb');
                INSERT INTO customer (first_name, last_name) VALUES ('Kaylee', 'Frye');
                """
        );
        statements.forEach(it -> databaseClient.sql(it)
                .fetch()
                .rowsUpdated()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete());
    }

    private List<Long> getIds() {
        return databaseClient.sql("select id from customer")
                .map(row -> row.get("id", Long.class))
                .all()
                .collectList()
                .block();
    }

    @Test
    void testCreateCustomer() {
        Customer customer = new Customer(null, "Inara", "Serra");
        client.post()
                .uri("/functional/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(customer), Customer.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id")
                .isNotEmpty()
                .jsonPath("$.firstName")
                .isEqualTo("Inara")
                .jsonPath("$.lastName")
                .isEqualTo("Serra");
    }

    @Test
    void testGetAllCustomers() {
        client.get()
                .uri("/functional/customers")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Customer.class)
                .hasSize(5);
    }

    @Test
    void testGetSingleCustomer() {
        List<Long> ids = getIds();
        Long firstId = ids.get(0);

        client.get()
                .uri("/functional/customers/{id}", firstId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .consumeWith(response ->
                        Assertions.assertThat(response.getResponseBody())
                                .isNotNull());
    }

    @Test
    void testUpdateCustomer() {
        List<Long> ids = getIds();
        Long firstId = ids.get(0);
        Customer updatedCustomer = new Customer(firstId, "Malcolm", "Reynolds Jr.");

        client.put()
                .uri("/functional/customers/{id}", firstId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updatedCustomer), Customer.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.firstName")
                .isEqualTo("Malcolm")
                .jsonPath("$.lastName")
                .isEqualTo("Reynolds Jr.");
    }

    @Test
    void testDeleteCustomer() {
        List<Long> ids = getIds();
        Long firstId = ids.get(0);

        client.delete()
                .uri("/functional/customers/{id}", firstId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void testGetNonExistentCustomer() {
        client.get()
                .uri("/functional/customers/999")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testDeleteNonExistentCustomer() {
        client.delete()
                .uri("/functional/customers/999")
                .exchange()
                .expectStatus()
                .isNotFound();
    }
}