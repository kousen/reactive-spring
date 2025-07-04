package com.kousenit.restclient.services;

import com.kousenit.restclient.json.AstroResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AstroService {

    private final RestClient restClient;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public AstroService() {
        this.restClient = RestClient.create("http://api.open-notify.org");
        this.webClient = WebClient.create("http://api.open-notify.org");
        this.objectMapper = new ObjectMapper();
    }

    public String getPeopleInSpace() {
        return restClient.get()
                .uri("/astros.json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);
    }

    public AstroResponse getAstroResponseSync() {
        return restClient.get()
                .uri("/astros.json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(AstroResponse.class);
    }

    public Mono<AstroResponse> getAstroResponseAsync() {
        return webClient.get()
                .uri("/astros.json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(AstroResponse.class)
                .log();
    }

    // Scheduler Examples

    /**
     * Demonstrates using publishOn with boundedElastic scheduler for file I/O.
     * The blocking file write operation is moved to a thread pool designed for blocking operations.
     */
    public Mono<String> saveAstronautsToFile() {
        return getAstroResponseAsync()
                .doOnNext(response -> System.out.println("Fetched data on: " + Thread.currentThread().getName()))
                .publishOn(Schedulers.boundedElastic())  // Switch to I/O thread pool
                .<String>handle((response, sink) -> {
                    System.out.println("Writing file on: " + Thread.currentThread().getName());
                    try {
                        Path file = Paths.get("astronauts.json");
                        String jsonString = objectMapper.writeValueAsString(response);
                        Files.writeString(file, jsonString);
                        sink.next("File written with " + response.number() + " astronauts");
                    } catch (Exception e) {
                        sink.error(new RuntimeException("Failed to write file", e));
                    }
                })
                .doOnNext(result -> System.out.println("File operation completed on: " + Thread.currentThread().getName()));
    }

    /**
     * Demonstrates the difference between publishOn and subscribeOn.
     * subscribeOn affects the entire chain from subscription upward.
     * publishOn affects the chain from that point downward.
     */
    public Mono<String> demonstrateSchedulerDifferences() {
        return Mono.fromCallable(() -> {
                    System.out.println("Starting work on: " + Thread.currentThread().getName());
                    return "Initial data";
                })
                .subscribeOn(Schedulers.boundedElastic())  // This affects the whole chain upward
                .map(data -> {
                    System.out.println("First transform on: " + Thread.currentThread().getName());
                    return data + " -> processed";
                })
                .publishOn(Schedulers.parallel())  // This affects later operations
                .map(data -> {
                    System.out.println("Second transform on: " + Thread.currentThread().getName());
                    return data + " -> parallel processed";
                })
                .publishOn(Schedulers.single())  // Switch to single thread
                .map(data -> {
                    System.out.println("Final transform on: " + Thread.currentThread().getName());
                    return data + " -> single threaded";
                });
    }

    /**
     * Simulates calling a legacy blocking service and properly handling it with schedulers.
     * This pattern is common when integrating reactive code with existing blocking APIs.
     */
    public Mono<String> callLegacyBlockingService(String input) {
        return Mono.fromCallable(() -> {
                    System.out.println("Calling blocking service on: " + Thread.currentThread().getName());
                    // Simulate blocking operation (e.g., database call, file I/O, HTTP call)
                    try {
                        Thread.sleep(100); // Simulate blocking delay
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                    return "Blocking result for: " + input;
                })
                .subscribeOn(Schedulers.boundedElastic())  // Execute blocking work on appropriate thread pool
                .doOnNext(result -> System.out.println("Received result on: " + Thread.currentThread().getName()));
    }

    /**
     * Demonstrates combining reactive and blocking operations efficiently.
     * Shows how to fetch data reactively, then write to file without blocking the event loop.
     */
    public Mono<String> processAndSaveData() {
        return getAstroResponseAsync()
                .doOnNext(response -> System.out.println("Reactive fetch on: " + Thread.currentThread().getName()))
                .flatMap(response -> 
                    callLegacyBlockingService("Processing " + response.number() + " astronauts")
                )
                .publishOn(Schedulers.boundedElastic())
                .map(result -> {
                    System.out.println("Final processing on: " + Thread.currentThread().getName());
                    return result + " - Processing complete";
                });
    }

    // Detailed Examples for Understanding publishOn vs subscribeOn

    /**
     * Demonstrates publishOn() - affects everything AFTER it in the chain.
     * Watch the thread names to see when the switch happens.
     */
    public Mono<String> demonstratePublishOn() {
        return Mono.fromCallable(() -> {
                System.out.println("1. Source: " + Thread.currentThread().getName());
                return "data";
            })
            .map(data -> {
                System.out.println("2. Before publishOn: " + Thread.currentThread().getName());
                return data + "-step2";
            })
            .publishOn(Schedulers.boundedElastic())  // ← Switch happens HERE
            .map(data -> {
                System.out.println("3. After publishOn: " + Thread.currentThread().getName());
                return data + "-step3";
            })
            .map(data -> {
                System.out.println("4. Still after publishOn: " + Thread.currentThread().getName());
                return data + "-step4";
            });
    }

    /**
     * Demonstrates subscribeOn() - affects the WHOLE chain regardless of placement.
     * The entire reactive chain runs on the specified scheduler.
     */
    public Mono<String> demonstrateSubscribeOn() {
        return Mono.fromCallable(() -> {
                System.out.println("1. Source: " + Thread.currentThread().getName());
                return "data";
            })
            .map(data -> {
                System.out.println("2. Transform: " + Thread.currentThread().getName());
                return data + "-step2";
            })
            .subscribeOn(Schedulers.boundedElastic())  // ← Affects the WHOLE chain
            .map(data -> {
                System.out.println("3. After subscribeOn: " + Thread.currentThread().getName());
                return data + "-step3";
            });
    }

    /**
     * Demonstrates combining both schedulers for real-world scenarios.
     * Shows how to optimize different types of work on appropriate thread pools.
     */
    public Mono<String> combineSchedulers() {
        return Mono.fromCallable(() -> {
                System.out.println("1. Expensive computation: " + Thread.currentThread().getName());
                // Simulate CPU work
                try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                return "computed-result";
            })
            .subscribeOn(Schedulers.parallel())      // CPU work on parallel scheduler
            .map(result -> {
                System.out.println("2. Transform: " + Thread.currentThread().getName());
                return result + "-transformed";
            })
            .publishOn(Schedulers.boundedElastic())  // Switch to I/O scheduler
            .map(data -> {
                System.out.println("3. Simulated I/O: " + Thread.currentThread().getName());
                // Simulate blocking I/O
                try { Thread.sleep(30); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                return data + "-io-complete";
            })
            .publishOn(Schedulers.single())          // Switch to single thread for final work
            .map(result -> {
                System.out.println("4. Final processing: " + Thread.currentThread().getName());
                return result + "-final";
            });
    }
}
