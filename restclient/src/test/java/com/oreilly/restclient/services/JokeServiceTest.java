package com.oreilly.restclient.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
public class JokeServiceTest {
    private final Logger logger = LoggerFactory.getLogger(JokeServiceTest.class);

    @Autowired
    private JokeService service;

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        HttpResponse<Void> response = HttpClient.newHttpClient()
                .send(HttpRequest.newBuilder()
                                .uri(URI.create("http://icndb.com"))  // .HEAD() in Java 18
                                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                                .build(),
                        HttpResponse.BodyHandlers.discarding());
        assumeTrue(response.statusCode() == 200, "ICNDB API site is down");
    }


    @Test
    public void getJokeSync() {
        String joke = service.getJokeSync("Craig", "Walls");
        logger.info("\nSynchronous: " + joke);
        assertTrue(joke.contains("Craig") || joke.contains("Walls"));
    }

    @Test
    public void getJokeWrappedSync() {
        String joke = service.getJokeSyncWrapped("Craig", "Walls")
                .block(Duration.ofSeconds(2));
        logger.info("\nWrapped synchronous: " + joke);
        assertTrue(joke.contains("Craig") || joke.contains("Walls"));
    }

    @Test
    public void getJokeAsync() {
        String joke = service.getJokeAsync("Craig", "Walls")
                .block(Duration.ofSeconds(2));
        logger.info("\nAsynchronous: " + joke);
        assertTrue(joke.contains("Craig") || joke.contains("Walls"));
    }

    @Test
    public void useStepVerifier() {
        StepVerifier.create(service.getJokeAsync("Craig", "Walls"))
                .assertNext(joke -> {
                    logger.info("\nStepVerifier: " + joke);
                    assertTrue(joke.contains("Craig") || joke.contains("Walls"));
                })
                .verifyComplete();
    }

    @Test
    public void useStepVerifierForMultipleCalls() {
        StepVerifier.create(service.getMultipleJokes(5, "Craig", "Walls"))
                .assertNext(joke -> {
                    logger.info("\nJoke 1: " + joke);
                    assertTrue(joke.contains("Craig") || joke.contains("Walls"));
                })
                .assertNext(joke -> {
                    logger.info("\nJoke 2: " + joke);
                    assertTrue(joke.contains("Craig") || joke.contains("Walls"));
                })
                .assertNext(joke -> {
                    logger.info("\nJoke 3: " + joke);
                    assertTrue(joke.contains("Craig") || joke.contains("Walls"));
                })
                .assertNext(joke -> {
                    logger.info("\nJoke 4: " + joke);
                    assertTrue(joke.contains("Craig") || joke.contains("Walls"));
                })
                .assertNext(joke -> {
                    logger.info("\nJoke 5: " + joke);
                    assertTrue(joke.contains("Craig") || joke.contains("Walls"));
                })
                .verifyComplete();
    }

    @Test
    public void useStepVerifierForMultipleCallsPublishOn() {
        StepVerifier.create(service.getMultipleJokesPublishOn(4, "Craig", "Walls"))
                .assertNext(joke -> {
                    logger.info("\nJoke 1: " + joke);
                    assertTrue(joke.contains("Craig") || joke.contains("Walls"));
                })
                .assertNext(joke -> {
                    logger.info("\nJoke 2: " + joke);
                    assertTrue(joke.contains("Craig") || joke.contains("Walls"));
                })
                .assertNext(joke -> {
                    logger.info("\nJoke 3: " + joke);
                    assertTrue(joke.contains("Craig") || joke.contains("Walls"));
                })
                .assertNext(joke -> {
                    logger.info("\nJoke 4: " + joke);
                    assertTrue(joke.contains("Craig") || joke.contains("Walls"));
                })
                .verifyComplete();
    }
}