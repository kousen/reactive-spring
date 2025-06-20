---
theme: seriph
background: https://source.unsplash.com/1920x1080/?java,programming
class: text-center
highlighter: shiki
lineNumbers: false
info: |
  ## Reactive Spring
  
  By Kenneth Kousen
  
  Learn more at [KouseniT](https://kousenit.com)
drawings:
  persist: false
transition: slide-left
title: "Reactive Spring"
mdc: true
slidev:
  slide-number: true
---

# Reactive Spring

<div class="pt-12">
  <span @click="$slidev.nav.next" class="px-2 py-1 rounded cursor-pointer" hover="bg-white bg-opacity-10">
    Press Space for next page <carbon:arrow-right class="inline"/>
  </span>
</div>

---

# Contact Info

Ken Kousen  
Kousen IT, Inc.

- ken.kousen@kousenit.com
- http://www.kousenit.com
- http://kousenit.org (blog)
- Social Media:
  - [@kenkousen](https://twitter.com/kenkousen) (twitter)
  - [@kenkousen@foojay.social](https://foojay.social/@kenkousen) (mastodon)
  - [@kousenit.com](https://bsky.app/profile/kousenit.com) (bluesky)
- *Tales from the jar side* (free newsletter)
  - https://kenkousen.substack.com
  - https://youtube.com/@talesfromthejarside

---

# Exercises


<v-clicks>

- HTML docs: See the [labs.md](https://github.com/kousen/reactive-spring/blob/main/labs.md) file in the GitHub repository
- Solutions:
  - https://github.com/kousen/reactive-spring
  - https://github.com/kousen/spring-and-spring-boot (MVC, non-reactive)

</v-clicks>

---

# Spring Framework


<v-clicks>

- Project infrastructure
- Lifecycle management of "beans"
- Any `POJO` with getters/setters
- Provides "services"
  - transactions, security, persistence, …
- Library of beans available
  - transaction managers
  - rest client
  - DB connection pools
  - testing mechanisms

</v-clicks>

---

# Spring Configuration


<v-clicks>

- Need "metadata"
- Tells Spring what to instantiate and configure
- **XML → old style**
  - Verbose configuration files
- **Annotations → better**
  - `@Component`, `@Service`, `@Repository`
- **JavaConfig → preferred**
  - Type-safe configuration
- All approaches still supported
- **Application Context**
  - Collection of managed beans
  - The "lightweight" Spring container

</v-clicks>

---

# Spring Boot


<v-clicks>

- Easy creation and configuration for Spring apps
- Many "starters"
- Gradle or Maven based
- Automatic configuration based on classpath
  - If you add JDBC driver, it adds `DataSource` bean
- Application with main method created automatically
  - Annotated with `@SpringBootApplication`
- Gradle or Maven build produces executable jar in build/libs folder
  - $ java -jar appname.jar
  - Or use gradle task bootRun

</v-clicks>

---

# When to Use Reactive

<v-clicks>

- **High-concurrency scenarios**
  - Many simultaneous requests
  - Limited thread pools
- **Non-blocking I/O operations**
  - Database calls, external APIs, file operations
- **Stream processing**
  - Real-time data, event-driven architectures
- **Backpressure handling**
  - Consumer controls flow rate from producer
- **Not always the answer**
  - Simple CRUD operations may not benefit
  - Debugging complexity increases

</v-clicks>

---

# Dependency Injection


<v-clicks>

- Spring adds dependencies on request
  - Annotate field, or setter, or constructor
  - `@Autowired` → autowiring by type
  - `@Resource` (from Java EE) → autowiring by (bean) name, then by type if necessary

</v-clicks>

---

# Spring Initializr


<v-clicks>

- Website for creating new Spring (Boot) apps
- http://start.spring.io
- Incorporated into major IDEs
- Select features you want
- Download zip containing build file

</v-clicks>

---

# Spring MVC


<v-clicks>

- Annotation based MVC framework
- `@Controller` → controllers
- `@GetMapping` → annotations for HTTP methods
- `@RequestParam` and more for model parameters

</v-clicks>

---

# RestClient


<v-clicks>

- Spring 6.1 includes a class called `RestClient`
- Access RESTful web services
  - Set HTTP methods, headers, query string, templates
  - Use .create or .builder methods to create one
  - Use content negotiation to return JSON or XML
- Spring 5 added asynchronous `WebClient`
  - Can do async processing
  - Understands `Flux` and `Mono`

</v-clicks>

---

# Testing


<v-clicks>

- Spring tests include the `JUnit 5` extension
  - `@ExtendWith(SpringExtension.class)`
  - Part of `@SpringBootTest`
- Annotate tests with `@Test`
- Use normal asserts as usual, but with `JUnit 5` additions
- Special annotations for web integration tests
  - `@WebMvcTest(... controller class …)`
  - `MockMvc` package
  - `MockMvcRequestBuilders`
  - `MockMvcRequestMatchers`

</v-clicks>

---

# Parsing JSON


<v-clicks>

- Spring uses Jackson JSON library by default
- Create classes that map to JSON structure
- Parsing happens implicitly when returning objects from controllers
- Spring automatically converts objects to/from JSON
- Can inject `ObjectMapper` for explicit control if needed

</v-clicks>

---

# Component Scan


<v-clicks>

- `@SpringBootApplication` includes `@ComponentScan`
- Spring detects annotated classes in package and subpackages
- `@Component` → generic Spring bean
- `@Controller`, `@Service`, `@Repository` → specialized `@Component`
  - Provide semantic meaning for different layers
- `@Configuration` → also includes `@Component`
  - Used for Java-based configuration classes

</v-clicks>

---

# Application properties


<v-clicks>

- Two options for file name
- Default folder is `src/main/resources`
- `application.properties` → standard Java properties file
- `application.yml` → YAML format

</v-clicks>

---

# Transactions


<v-clicks>

- Spring transactions configured with `@Transactional`
- Spring uses `TransactionManager` to talk to resource
- Usually a relational DB, but other options available

</v-clicks>

---

# Reactive Spring


<v-clicks>

- Spring 5 → requires Java SE8
- Spring Boot 3 (Spring 6) → requires Java 17+
- `WebFlux` module introduced in Spring 5
- Alternative to traditional Spring MVC
- Built on [Project Reactor](https://projectreactor.io)
- Implements the Reactive Streams specification

</v-clicks>

---

# WebFlux Overview


<v-clicks>

- **Two approaches:**
  - Annotation-based → Similar to MVC
  - Functional → Uses a "routing configuration"
- **Annotated Controllers**
  - Support reactive return types
  - Reactor, RxJava 2 compatible
  - Familiar `@Controller`, `@GetMapping` annotations
- **Server Options**
  - Uses Netty by default (non-blocking)
  - Others: Undertow, Tomcat (Servlet 3.1+), Jetty (Servlet 3.1+)

</v-clicks>

---

# WebFlux Annotated Controllers


<v-clicks>

- **Similar to Spring MVC but reactive**
- **Return `Mono` or `Flux` instead of objects/lists**

```java
@RestController
@RequestMapping("/users")
public class UserController {
    
    @GetMapping
    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public Mono<User> getUser(@PathVariable String id) {
        return userRepository.findById(id);
    }
    
    @PostMapping
    public Mono<User> createUser(@RequestBody User user) {
        return userRepository.save(user);
    }
}
```

</v-clicks>

---

# WebFlux Functional Endpoints


<v-clicks>

- **Lambda-based, functional programming model**
- **Library of utilities to route and handle requests**
- **Key Components:**
  - `HandlerFunction<T>` → processes requests
  - `RouterFunction<T>` → routes requests to handlers
- **Example:**

```java
@Bean
public RouterFunction<ServerResponse> routes(UserHandler handler) {
    return RouterFunctions.route()
        .GET("/users", handler::getAllUsers)
        .GET("/users/{id}", handler::getUser)
        .POST("/users", handler::createUser)
        .build();
}
```

</v-clicks>

---

# Reactive Streams


<v-clicks>

- Industry specification with wide adoption
- http://www.reactive-streams.org/
- Supported in Java 9
- `java.util.concurrent.Flow`
- Four interfaces:
  - `Publisher`
  - `Subscriber`
  - `Subscription`
  - `Processor`

</v-clicks>

---

# Publisher


<v-clicks>

- Provides a sequence of elements to a `Subscriber`
- Emits signals: `onSubscribe` → `onNext`* → (`onError` | `onComplete`)
- Only has one method: `subscribe()`
```java
public interface Publisher<T> {
  void subscribe(Subscriber<? super T> s);
}
```

</v-clicks>

---

# Subscriber


<v-clicks>

- Receives signals from `Publisher`
- Must handle all four callback methods

```java
public interface Subscriber<T> {
  void onSubscribe(Subscription s);
  void onNext(T t);
  void onError(Throwable t);
  void onComplete();
}
```
</v-clicks>

---

# Subscription


<v-clicks>

- Sent from `Publisher` to `Subscriber`
- Controls flow of data (backpressure mechanism)
- `request(n)` → ask for n more elements
- `cancel()` → stop receiving elements

```java
public interface Subscription {
  void request(long n);
  void cancel();
}
```
</v-clicks>

---

# Processor


<v-clicks>

- A combination `Publisher`/`Subscriber`
```java
public interface Processor<T,R>
  extends Subscriber<T>, Publisher<R> {
  // No additional methods
}
```

</v-clicks>

---

# Project Reactor


<v-clicks>

- Reactive library for the JVM based on Reactive Streams
- Reactive Core → fully non-blocking
- Typed sequences → `Flux`, `Mono`
- Non-blocking IO with backpressure

</v-clicks>

---

# Flux and Mono Examples

<v-clicks>

```java
// Flux - sequence of 0..N items
Flux<String> names = Flux.just("Alice", "Bob", "Charlie")
    .map(String::toUpperCase)
    .filter(name -> name.length() > 3);

// Mono - sequence of 0..1 item
Mono<String> result = Mono.just("Hello")
    .map(s -> s + " World!")
    .defaultIfEmpty("Empty");

// Error handling
Flux<String> data = Flux.just("1", "2", "invalid", "4")
    .map(Integer::parseInt)
    .map(String::valueOf)
    .onErrorReturn("Error occurred");
```

</v-clicks>

---

# Common Operators

<v-clicks>

- **Transformation:** `map()`, `flatMap()`, `filter()`
- **Side Effects:** `doOnNext()`, `doOnError()`, `doOnComplete()`
- **Error Handling:** `onErrorReturn()`, `onErrorResume()`, `retry()`
- **Utility:** `timeout()`, `zip()`, `merge()`
- **Combining:** `zipWith()`, `mergeWith()`, `concatWith()`
- **Filtering:** `take()`, `skip()`, `distinct()`, `sample()`

</v-clicks>

---

# Schedulers Overview


<v-clicks>

- Project Reactor is concurrency agnostic
- You can choose which thread pool to use with a `Scheduler`
- `Scheduler` is a combination of a worker and a clock
- The `Schedulers` class makes it easy to choose a scheduler
- Critical for non-blocking applications

</v-clicks>

---

# Scheduler Types & Usage


<v-clicks>

- `Schedulers.immediate()` → don't change the thread pool
- `Schedulers.single()` → single thread for all callers
- `Schedulers.boundedElastic()` → for I/O blocking work
- `Schedulers.parallel()` → fixed size pool (num of processors)

```java
// Wrong - blocking on main thread
String result = webClient.get().retrieve()
    .bodyToMono(String.class).block();

// Right - using appropriate scheduler
Mono<String> result = webClient.get().retrieve()
    .bodyToMono(String.class)
    .publishOn(Schedulers.boundedElastic())
    .map(this::processData);
```

</v-clicks>

---

# Operators


<v-clicks>

- `publishOn` → use the supplied scheduler
- `subscribeOn` → change the schedule for the whole chain of operators

</v-clicks>

---

# Summary


<v-clicks>

- Reactive Spring is based on Reactive Streams specification
- Several libraries implement reactive streams
- Spring uses Project Reactor
- Reactor defines `Flux`, `Mono`, `StepVerifier`
- Spring has two ways to use Reactor in a web service
  - Annotated controllers are like Spring MVC
  - Functional endpoints use handlers and routing functions

</v-clicks>

---

# Thank You!

<div class="text-center">

## Questions?

<div class="pt-12">
  <span class="text-6xl"><carbon:logo-github /></span>
</div>

**Kenneth Kousen**  
*Author, Speaker, Java & AI Expert*

[kousenit.com](https://kousenit.com) | [@kenkousen](https://twitter.com/kenkousen)

</div>
