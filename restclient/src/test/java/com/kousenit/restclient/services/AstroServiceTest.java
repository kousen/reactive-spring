package com.kousenit.restclient.services;

import com.kousenit.restclient.json.AstroResponse;
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

    // Scheduler Tests

    @Test
    void testSaveAstronautsToFile() {
        System.out.println("\n=== Testing saveAstronautsToFile() ===");
        
        service.saveAstronautsToFile()
                .as(StepVerifier::create)
                .assertNext(result -> {
                    assertNotNull(result);
                    assertTrue(result.contains("File written with"));
                    System.out.println("Result: " + result);
                })
                .verifyComplete();
    }

    @Test
    void testSchedulerDifferences() {
        System.out.println("\n=== Testing demonstrateSchedulerDifferences() ===");
        
        service.demonstrateSchedulerDifferences()
                .as(StepVerifier::create)
                .assertNext(result -> {
                    assertNotNull(result);
                    assertTrue(result.contains("single threaded"));
                    System.out.println("Final result: " + result);
                })
                .verifyComplete();
    }

    @Test
    void testLegacyBlockingService() {
        System.out.println("\n=== Testing callLegacyBlockingService() ===");
        
        service.callLegacyBlockingService("test input")
                .as(StepVerifier::create)
                .assertNext(result -> {
                    assertNotNull(result);
                    assertTrue(result.contains("Blocking result for: test input"));
                    System.out.println("Blocking service result: " + result);
                })
                .verifyComplete();
    }

    @Test
    void testProcessAndSaveData() {
        System.out.println("\n=== Testing processAndSaveData() ===");
        
        service.processAndSaveData()
                .as(StepVerifier::create)
                .assertNext(result -> {
                    assertNotNull(result);
                    assertTrue(result.contains("Processing complete"));
                    System.out.println("Combined operation result: " + result);
                })
                .verifyComplete();
    }

    // Tests for publishOn vs subscribeOn demonstrations

    @Test
    void testDemonstratePublishOn() {
        System.out.println("\n=== Testing demonstratePublishOn() ===");
        System.out.println("Watch for thread switch AFTER publishOn:");
        
        service.demonstratePublishOn()
                .as(StepVerifier::create)
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals("data-step2-step3-step4", result);
                    System.out.println("Final result: " + result);
                })
                .verifyComplete();
    }

    @Test
    void testDemonstrateSubscribeOn() {
        System.out.println("\n=== Testing demonstrateSubscribeOn() ===");
        System.out.println("Watch for ALL operations on the same thread:");
        
        service.demonstrateSubscribeOn()
                .as(StepVerifier::create)
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals("data-step2-step3", result);
                    System.out.println("Final result: " + result);
                })
                .verifyComplete();
    }

    @Test
    void testCombineSchedulers() {
        System.out.println("\n=== Testing combineSchedulers() ===");
        System.out.println("Watch for multiple thread switches:");
        
        service.combineSchedulers()
                .as(StepVerifier::create)
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals("computed-result-transformed-io-complete-final", result);
                    System.out.println("Final result: " + result);
                })
                .verifyComplete();
    }

}