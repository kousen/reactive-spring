package com.oreilly.restclient.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JokeServiceTest {
    private Logger logger = LoggerFactory.getLogger(JokeServiceTest.class);

    @Autowired
    private JokeService service;

    @Test
    public void getJokeSync() {
        String joke = service.getJokeSync("Craig", "Walls");
        logger.info(joke);
        assertTrue(joke.contains("Craig") || joke.contains("Walls"));
    }
}