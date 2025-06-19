package com.oreilly.restclient.services;

import com.oreilly.restclient.json.AstroResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AstroService {

    private final RestTemplate template;
    private final WebClient client;
    private final RestClient restClient;

    @Autowired
    public AstroService(RestTemplateBuilder builder) {
        this.template = builder.build();
        this.client = WebClient.create("http://api.open-notify.org");
        this.restClient = RestClient.create("http://api.open-notify.org");
    }

    public Mono<AstroResponse> getAstroResponseAsync() {
        return client.get()
                .uri("/astros.json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(AstroResponse.class)
                .log();
    }

    public AstroResponse getAstroResponseWithRestClient() {
        return restClient.get()
                .uri("/astros.json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(AstroResponse.class);
    }

    public String getPeopleInSpace() {
        return template.getForObject("http://api.open-notify.org/astros.json", String.class);
    }

    public AstroResponse getAstroResponseSync() {
        return template.getForObject("http://api.open-notify.org/astros.json", AstroResponse.class);
    }
}
