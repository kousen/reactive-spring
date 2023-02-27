package com.oreilly.restclient.services;

import com.oreilly.restclient.json.AstroResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AstroServiceTest {
    @Autowired
    private AstroService service;

    @Test
    void getPeopleInSpace() {
        String people = service.getPeopleInSpace();
        assertNotNull(people);
        assertTrue(people.contains("people"));
        System.out.println(people);
    }

    @Test
    void getAstroResponseSync() {
        AstroResponse response = service.getAstroResponseSync();
        assertNotNull(response);
        assertEquals("success", response.message());
        assertTrue(response.number() >= 0);
        assertEquals(response.number(), response.people().size());
        System.out.println(response);
    }

    @Test
    void getAstroResponseAsync() {
        AstroResponse response = service.getAstroResponseAsync()
                .block(Duration.ofSeconds(2));
        assertNotNull(response);
        assertEquals("success", response.message());
        assertTrue(response.number() >= 0);
        assertEquals(response.number(), response.people().size());
        System.out.println(response);
    }

    @Test
    void getAstroResponseAsyncStepVerifier() {
        service.getAstroResponseAsync()
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals("success", response.message());
                    assertTrue(response.number() >= 0);
                    assertEquals(response.number(), response.people().size());
                    System.out.println(response);
                })
                .verifyComplete();
    }

}