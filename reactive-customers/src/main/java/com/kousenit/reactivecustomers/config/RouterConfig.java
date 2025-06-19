package com.kousenit.reactivecustomers.config;

import com.kousenit.reactivecustomers.controllers.CustomerHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterConfig {
    @Bean
    public RouterFunction<ServerResponse> route(CustomerHandler handler) {
        return RouterFunctions
                .route(GET("/functional/customers/{id}").and(accept(APPLICATION_JSON)), handler::getCustomer)
                .andRoute(GET("/functional/customers").and(accept(APPLICATION_JSON)), handler::listCustomers)
                .andRoute(POST("/functional/customers").and(contentType(APPLICATION_JSON)), handler::createCustomer)
                .andRoute(PUT("/functional/customers/{id}").and(contentType(APPLICATION_JSON)), handler::updateCustomer)
                .andRoute(DELETE("/functional/customers/{id}"), handler::deleteCustomer);
    }
}