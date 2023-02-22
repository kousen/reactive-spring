package com.oreilly.restclient.services;

import com.oreilly.restclient.json.AstroResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AstroService {

    private final RestTemplate template;
    private final WebClient client;

    @Autowired
    public AstroService(RestTemplateBuilder builder, WebClient.Builder webClientBuilder) {
        this.template = builder.build();
        this.client = webClientBuilder.baseUrl("http://api.open-notify.org").build();
    }

    public Mono<AstroResponse> getAstroResponseAsync() {
        return client.get()
                .uri("/astros.json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(AstroResponse.class)
                .log();
    }

    public String getPeopleInSpace() {
        return template.getForObject("http://api.open-notify.org/astros.json", String.class);
    }

    public AstroResponse getAstroResponse() {
        return template.getForObject("http://api.open-notify.org/astros.json", AstroResponse.class);
    }
}
