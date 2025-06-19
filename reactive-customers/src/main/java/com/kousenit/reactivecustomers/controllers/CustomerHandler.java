package com.kousenit.reactivecustomers.controllers;

import com.kousenit.reactivecustomers.dao.CustomerRepository;
import com.kousenit.reactivecustomers.entities.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class CustomerHandler {
    private final CustomerRepository repository;

    public CustomerHandler(CustomerRepository repository) {
        this.repository = repository;
    }

    public Mono<ServerResponse> listCustomers(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(repository.findAll(), Customer.class);
    }

    public Mono<ServerResponse> createCustomer(ServerRequest request) {
        Mono<Customer> customerMono = request.bodyToMono(Customer.class);
        return customerMono.flatMap(customer ->
                ServerResponse.status(HttpStatus.CREATED)
                        .contentType(APPLICATION_JSON)
                        .body(repository.save(customer), Customer.class));
    }

    public Mono<ServerResponse> getCustomer(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();
        Mono<Customer> customerMono = this.repository.findById(Long.valueOf(id));
        return customerMono
                .flatMap(customer -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .body(BodyInserters.fromValue(customer)))
                .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> updateCustomer(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Customer> customerMono = request.bodyToMono(Customer.class);
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();
        
        return repository.findById(Long.valueOf(id))
                .flatMap(existingCustomer -> 
                    customerMono.flatMap(customer -> {
                        Customer updatedCustomer = new Customer(Long.valueOf(id), 
                            customer.firstName(), customer.lastName());
                        return ServerResponse.ok()
                                .contentType(APPLICATION_JSON)
                                .body(repository.save(updatedCustomer), Customer.class);
                    }))
                .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> deleteCustomer(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();
        
        return repository.findById(Long.valueOf(id))
                .flatMap(customer -> 
                    repository.delete(customer)
                            .then(ServerResponse.noContent().build()))
                .switchIfEmpty(notFound);
    }
}