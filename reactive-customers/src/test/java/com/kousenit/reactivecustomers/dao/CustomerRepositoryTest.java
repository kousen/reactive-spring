package com.kousenit.reactivecustomers.dao;

import com.kousenit.reactivecustomers.entities.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

@DataR2dbcTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository repository;

    private List<Customer> customers = List.of(
            new Customer(null, "Malcolm", "Reynolds"),
            new Customer(null, "Zoë", "Washburne"),
            new Customer(null, "Hoban", "Washburne"),
            new Customer(null, "Jayne", "Cobb"),
            new Customer(null, "Kaylee", "Frye"));

    @BeforeEach
    void setUp() {
        customers = repository.deleteAll()
                .thenMany(Flux.fromIterable(customers))
                .flatMap(repository::save)
                .collectList().block();
    }

    @Test
    void fetchAllCustomers() {
        repository.findAll()
                .doOnNext(System.out::println)
                .as(StepVerifier::create)
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void fetchCustomerById() {
        repository.findById(customers.get(0).id())
                .doOnNext(System.out::println)
                .as(StepVerifier::create)
                .expectNextMatches(customer -> customer.firstName().equals("Malcolm"))
                .verifyComplete();
    }

    @Test
    void fetchCustomersByLastName() {
        repository.findByLastName("Washburne")
                .doOnNext(System.out::println)
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void insertCustomer() {
        Customer newCustomer = new Customer(null, "Inara", "Serra");
        repository.save(newCustomer)
                .doOnNext(System.out::println)
                .as(StepVerifier::create)
                .expectNextMatches(customer -> customer.firstName().equals("Inara"))
                .verifyComplete();
    }

    @Test
    void updateCustomer() {
        Customer updatedCustomer = new Customer(customers.get(0).id(), "Malcolm", "Reynolds, Jr.");
        repository.save(updatedCustomer)
                .doOnNext(System.out::println)
                .as(StepVerifier::create)
                .expectNextMatches(customer -> customer.firstName().equals("Malcolm"))
                .verifyComplete();
    }

    @Test
    void deleteCustomer() {
        repository.deleteById(customers.get(0).id())
                .doOnNext(System.out::println)
                .as(StepVerifier::create)
                .verifyComplete();
    }
}