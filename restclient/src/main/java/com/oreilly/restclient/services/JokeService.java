package com.oreilly.restclient.services;

import com.oreilly.restclient.json.JokeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class JokeService {
    private final RestTemplate template;
    private final WebClient client;

    @Autowired
    public JokeService(RestTemplateBuilder rtBuilder, WebClient.Builder wcBuilder) {
        template = rtBuilder.build();
        client = wcBuilder.baseUrl("http://api.icndb.com")
                .build();
    }

    public String getJokeSync(String first, String last) {
        return template.getForObject(getURL(first, last), JokeResponse.class)
                .getValue()
                .getJoke();
    }

    // Put synchronous blocking call on a dedicated thread
    public Mono<String> getJokeSyncWrapped(String first, String last) {
        return Mono.fromCallable(() -> template.getForObject(getURL(first, last), JokeResponse.class))
                .subscribeOn(Schedulers.boundedElastic())
                .map(jokeResponse -> jokeResponse.getValue()
                        .getJoke());
    }

    // Put each blocking call on its own thread
    public Flux<String> getMultipleJokes(int num, String first, String last) {
        return Flux.range(1, num)
                .map(i -> getURL(first, last))
                .flatMap(url -> Mono.fromCallable(() -> template.getForObject(url, JokeResponse.class)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(jokeResponse -> jokeResponse.getValue().getJoke());
    }

    // Publish on the elastic scheduler
    public Flux<String> getMultipleJokesPublishOn(int num, String first, String last) {
        return Flux.range(1, num)
                .map(i -> getURL(first, last))
                .publishOn(Schedulers.boundedElastic())
                .flatMap(url -> Mono.justOrEmpty(template.getForObject(url, JokeResponse.class)))
                .map(jokeResponse -> jokeResponse.getValue().getJoke());
    }

    private String getURL(String first, String last) {
        String base = "http://api.icndb.com/jokes/random?limitTo=nerdy";
        return String.format("%s&firstName=%s&lastName=%s", base, first, last);
    }

    public Mono<String> getJokeAsync(String first, String last) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/jokes/random")
                        .queryParam("limitTo", "[nerdy]")
                        .queryParam("firstName", first)
                        .queryParam("lastName", last)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JokeResponse.class)
                .map(jokeResponse -> jokeResponse.getValue().getJoke());
    }
}
