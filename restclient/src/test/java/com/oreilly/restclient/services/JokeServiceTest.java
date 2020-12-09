package com.oreilly.restclient.services;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class JokeServiceTest {
    private final Logger logger = LoggerFactory.getLogger(JokeServiceTest.class);

    @Autowired
    private JokeService service;

    @Test
    public void getJokeSync() {
        String joke = service.getJokeSync("Craig", "Walls");
        logger.info("\nSynchronous: " + joke);
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
}