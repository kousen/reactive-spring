package com.oreilly.reactiveofficers.configuration;

import com.oreilly.reactiveofficers.controllers.OfficerHandler;
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
    public RouterFunction<ServerResponse> route(OfficerHandler handler) {
        return RouterFunctions
                .route(GET("/route/{id}").and(accept(APPLICATION_JSON)), handler::getOfficer)
                .andRoute(GET("/route").and(accept(APPLICATION_JSON)), handler::listOfficers)
                .andRoute(POST("/route").and(contentType(APPLICATION_JSON)), handler::createOfficer);
    }
}
