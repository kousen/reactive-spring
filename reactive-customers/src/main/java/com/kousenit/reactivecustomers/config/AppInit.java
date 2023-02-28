package com.kousenit.reactivecustomers.config;

import com.kousenit.reactivecustomers.dao.CustomerRepository;
import com.kousenit.reactivecustomers.entities.Customer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
public class AppInit {

    @Bean
    public CommandLineRunner initializeDatabase(CustomerRepository repository) {
        return args ->
                repository.count().switchIfEmpty(Mono.just(0L))
                        .flatMapMany(count -> repository.deleteAll()
                                .thenMany(Flux.just(
                                        new Customer(null, "Malcolm", "Reynolds"),
                                        new Customer(null, "Zoë", "Washburne"),
                                        new Customer(null, "Hoban", "Washburne"),
                                        new Customer(null, "Jayne", "Cobb"),
                                        new Customer(null, "Kaylee", "Frye")))
                                .flatMap(repository::save))
                        .subscribe(System.out::println);
    }
}
