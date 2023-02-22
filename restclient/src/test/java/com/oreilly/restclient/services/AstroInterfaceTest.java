package com.oreilly.restclient.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AstroInterfaceTest {

    @Test
    void astroInterfaceTest(@Autowired AstroInterface astroInterface) {
        StepVerifier.create(astroInterface.getResponse())
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