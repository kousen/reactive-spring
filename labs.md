# Reactive Spring Labs

This document contains hands-on exercises for learning reactive programming with Spring Boot, Spring WebFlux, and Project Reactor.

## Table of Contents

1. [Building a REST Client](#building-a-rest-client)
2. [Asynchronous Access](#asynchronous-access)
3. [HTTP Interfaces (Spring Boot 3+)](#http-interfaces-spring-boot-3)
4. [Project Reactor Tutorial](#project-reactor-tutorial)
5. [Reactive Spring Data](#reactive-spring-data)
6. [Spring WebFlux with Annotated Controllers](#spring-webflux-with-annotated-controllers)

## Building a REST Client

This exercise uses the `RestTemplate` class to synchronously access a RESTful web service. The template is used to convert the response into an object for the rest of the system. Later the `WebClient` class, introduced in Spring 5, will be used to do the same asynchronously.

1. Create a new Spring Boot project (either by using the Initializr at http://start.spring.io or using your IDE) called `restclient`. Add both the _Spring Web_ and the _Spring Reactive Web_ dependencies.

2. Create a service class called `AstroService` in a `com.kousenit.restclient.services` package under `src/main/java`

3. Add the annotation `@Service` to the class (from the `org.springframework.stereotype` package, so you'll need an `import` statement)

4. Add a private attribute to `AstroService` of type `RestTemplate` called `template`

5. Add a constructor to `AstroService` that takes a single argument of type `RestTemplateBuilder`.

   > [!NOTE]
   > Because there are so many possible configuration options, Spring does not automatically provide a `RestTemplate`. It does, however, provide a `RestTemplateBuilder`, which can be used to configure and create the `RestTemplate`.

6. Inside the constructor, invoke the `build()` method on the `RestTemplateBuilder` and assign the result to the `template` attribute.

   > [!NOTE]
   > If you provide only a single constructor in a class, you do not need to add the `@Autowired` annotation to it. Spring will inject the arguments anyway

7. The site providing the API is http://open-notify.org/, which is an API based on NASA data. We'll access the _Number of People in Space_ service using a GET request.

8. Add a `public` method to our service called `getPeopleInSpace` that takes no arguments and returns a `String`.

9. Access the API using the `getForObject` method of `RestTemplate` as shown:

   ```java
   public String getPeopleInSpace() {
       return template.getForObject("http://api.open-notify.org/astros.json", String.class);
   }
   ```

10. The `getForObject` method that takes two arguments: the URL to access, and the class to instantiate with the resulting JSON response. It performs an HTTP GET request and parses the returned JSON data. At the moment, all we are asking is for the JSON data to be returned as a `String` in order to verify everything is working properly. To do so, add a test class called `AstroServiceTest` in the same package under `src/test/java`:

    ```java
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
    }
    ```

11. The test asserts that the JSON response contains a field called "people", but that's about all we can do until we parse the data into Java classes. The general form of the response is:

    ```javascript
    {
      "message": "success",
      "number": NUMBER_OF_PEOPLE_IN_SPACE,
      "people": [
        {"name": NAME, "craft": SPACECRAFT_NAME},
        ...
      ]
    }
    ```

12. Since there are only two nested JSON objects, you can create two classes that model them. Create the classes `Assignment`, which will be the combination of "name" and "craft" for each astronaut, and `AstroResponse`, which holds the complete response, both in the `com.kousenit.restclient.json` package.

13. The code for the classes are shown below. Note how the properties match the keys in the JSON response exactly. You can use annotations from the included Jackson 2 JSON parser to customize the attributes if you like, but in this case it's easy enough to make them the same as the JSON variable names.

    ```java
    package com.kousenit.restclient.json;

    public class Assignment {
        private String name;
        private String craft;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCraft() {
            return craft;
        }

        public void setCraft(String craft) {
            this.craft = craft;
        }
    }

    public class AstroResponse {
        private String message;
        private int number;
        private List<Assignment> people;

        // ... getters and setters ...
    }
    ```

14. Note that if you are using Java 17, you can replace these with records instead, because the included Jackson JSON parser understands how to parse JSON into records (in two separate files in the `com.kousenit.restclient.json` package):

    ```java
    public record Assignment(String name, String craft) {
    }

    public record AstroResponse(String message, int number, List<Assignment> people) {
    }
    ```

15. The JSON response from the web service can now be converted into an instance of the `AstroResponse` class. Add a method called `getAstroResponseSync` to the `AstroService` that takes no arguments and returns an `AstroResponse`:

    ```java
    public AstroResponse getAstroResponseSync() {
        return template.getForObject(
            "http://api.open-notify.org/astros.json", 
            AstroResponse.class);
    }
    ```

16. To use the new method, create a test for it. The source for the test is:

    ```java
    @Test
    void getAstroResponseSync() {
        AstroResponse response = service.getAstroResponseSync();
        assertNotNull(response);
        assertEquals("success", response.getMessage());
        assertTrue(response.getNumber() >= 0);
        assertEquals(response.getNumber(), response.getPeople().size());
        System.out.println(response);
    }
    ```

17. Note that if you used records for the parsed data, replace `getMessage()` with `message()`, `getNumber()` with `number()`, and `getPeople()` with `people()`.

18. The test verifies that the returned message string is "success", that the number of people in space is non-negative, and that the reported number matches the size of the people collection.

19. Execute the test and make any needed corrections until it passes.

## Asynchronous Access

The `webflux` module in Spring allows you to use the Project Reactor types `Flux` and `Mono`. Methods that work synchronously can be converted to asynchronous by changing the return type to one of those types. The `WebClient` class then knows how produce those types, and is now the preferred asynchronous rest client (the class `AsyncRestTemplate` is now deprecated).

1. In the `AstroService` class, add an attribute of type `WebClient` that is initialize in the `AstroService` constructor using the `static` method `WebClient.create`, which takes the base URL of the service.

   ```java
   @Service
   public class AstroService {

       private final RestTemplate template;
       private final WebClient client;

       @Autowired
       public AstroService(RestTemplateBuilder builder) {
           this.template = builder.build();
           this.client = WebClient.create("http://api.open-notify.org");
       }

       // ... other methods ...
   }
   ```

2. Now add a new method called `getAstroResponseAsync` that takes no arguments and returns a `Mono<AstroResponse>` instead of the `AstroReponse` we used previously. The implementation is:

   ```java
   public Mono<AstroResponse> getAstroResponseAsync() {
       return client.get()
               .uri("/astros.json")
               .accept(MediaType.APPLICATION_JSON)
               .retrieve()
               .bodyToMono(AstroResponse.class)
               .log();
   }
   ```

3. The `get` method is used to make an HTTP GET request. the `uri` method takes the path, which is the part of the URL after the base. The `retrieve` method schedules the retrieval. Then the `bodyToMono` method extracts the body from the HTTP response and converts it to an instance of `AstroResponse` and wraps it in a `Mono`. Finally, the `log` method on `Mono` will log to the console all the reactive stream interactions, which is useful for debugging.

4. To test this, go back to the `AstroServiceTest` class. There are two ways to test the method. One is to invoke it and block until the request is complete. A test to do that is shown here:

   ```java
   @Test
   void getAstroResponseAsync() {
       AstroResponse response = service.getAstroResponseAsync()
               .block(Duration.ofSeconds(2));
       assertNotNull(response);
       assertEquals("success", response.getMessage());
       assertTrue(response.getNumber() >= 0);
       assertEquals(response.getNumber(), response.getPeople().size());
       System.out.println(response);
   }
   ```

5. As an alternative, the Reactor Test project includes a class called `StepVerifier`, which includes assertion methods. A test using that class is given by:

   ```java
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
   ```

6. Both of the new tests should now pass. The details of the `StepVerifier` class will be discussed during the course.

## HTTP Interfaces (Spring Boot 3+)

If you are using Spring Boot 3.0 or above (and therefore Spring 6.0 or above), there is a new way to access external restful web services. The [Spring 6 documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#spring-integration) has a section on REST clients, which includes the `RestTemplate` and `WebClient` classes discussed above, as well as something called HTTP Interface.

The idea is to declare an interface with the access methods you want, and add a proxy factory bean to the application context, and Spring will implement the interface methods for you. This exercise is a quick example of how to do that for our current application.

1. Add an interface called `AstroInterface` to the `services` package.

2. Inside that interface, add a method to perform an HTTP GET request to our "People In Space" endpoint:

   ```java
   public interface AstroInterface {
       @GetExchange("/astros.json")
       Mono<AstroResponse> getAstroResponse();
   }
   ```

3. Like most publicly available services, this service only supports GET requests. For those that support other HTTP methods, there are annotations `@PutExchange`, `@PostExchange`, `@DeleteExchange`, and so on. Also, this particular request does not take any parameters, so it is particularly simple. If it took parameters, they would appear in the URL at Http Template variables, and in the parameter list of the method annotated with `@PathVariable` or something similar.

4. We now need the proxy factory bean, which goes in a Java configuration class. Since the `RestClientApplication` class (the class with the standard Java `main` method) is annotated with `@SpringBootApplication`, it ultimately contains the annotation `@Configuration`. That means we can add `@Bean` methods to it, which Spring will use to add beans to the application context. Therefore, add the following bean to that class:

   ```java
   @Bean
   public AstroInterface astroInterface() {
       WebClient client = WebClient.create("http://api.open-notify.org/");
       HttpServiceProxyFactory factory =
                   HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();

       return factory.createClient(AstroInterface.class);
   }
   ```

5. That method creates a `WebClient` configured for the base URL, and uses that to build an `HttpServiceProxyFactory`. From the factory, we use the `createClient` method to tell Spring to create a class that implements the `AstroInterface`.

6. To test this, simply reuse the `AstroServiceTest` class by adding another test:

   ```java
   @Test
   void getAstroResponseFromInterface(@Autowired AstroInterface astroInterface) {
       AstroResponse response = astroInterface.getAstroResponse()
               .block(Duration.ofSeconds(2));
       assertNotNull(response);
       assertAll(
               () -> assertEquals("success", response.message()),
               () -> assertTrue(response.number() >= 0),
               () -> assertEquals(response.number(), response.people().size())
       );
       System.out.println(response);
   }
   ```

7. That test should pass. Note that for synchronous access, simply change the return type of the method inside the `getAstroResponse` method of `AstroInterface` to `AstroResponse` instead of the `Mono`. See the documentation for additional details.

## Project Reactor Tutorial

This exercise works with a tutorial provided by [Project Reactor](https://projectreactor.io/) to teach the basics of the classes `Flux` and `Mono`.

1. Project Reactor is located at https://projectreactor.io. Under the _Documentation_ header you will find the Reference Guide for Reactor Core at https://projectreactor.io/docs/core/release/reference/ and the Javadocs for that project at https://projectreactor.io/docs/core/release/api/ .

2. Inside the Reference Guide, go to _Appendix A: Which Operator Do I Need?_. This will help you solve the tutorial exercises.

3. The tutorial project is located on GitHub at https://github.com/reactor/lite-rx-api-hands-on, entitled _Lite Rx API Hands On_. It is a Maven project that requires only Java 8.

4. Clone the project and import it into your IDE.

5. There are two branches that matter here. The _master_ branch contains the exercises as a series of TODO statements inside tests, and the _solution_ branch contains the answers to those exercises.

6. Under `src/main/java`, in the `io.pivotal.literx` package, find the classes `Part01Flux` and `Part02Mono`. The corresponding tests are in the same package under `src/test/java`.

7. Complete those exercises as the comments describe.

8. If you have time, feel free to look at the other exercises, which are classes labeled from `Part03StepVerifier` to `Part11BlockingToReactive`. Alternatively, you can simply browse the code for them in the _solution_ branch.

Hopefully, you will find _Appendix A_ in the reference guide helpful in this, along with the Javadocs.

## Reactive Spring Data

1. Create a new project called `reactive-customers`. Add in the `Spring Reactive Web`, `Spring Data R2DBC`, and `H2Database` dependencies.

2. Add a domain class called `Customer` as an entity the `com.kousenit.reactivecustomers.entities` package.

   ```java
   import org.springframework.data.annotation.Id;

   public class Customer {
       @Id
       private Long id;
       private String firstName;
       private String lastName;

       public Customer() {}

       public Customer(String firstName, String lastName) {
           this.firstName = firstName;
           this.lastName = lastName;
       }

       public Customer(Long id, String firstName, String lastName) {
           this.id = id;
           this.firstName = firstName;
           this.lastName = lastName;
       }

       // ... getters and setters ...
       // ... equals and hashCode (without id) ...
       // ... toString ...
   }
   ```

3. As shown, annotate `id` with `@Id` from `org.springframework.data.annotation`.

4. If you are on Java 17, you can use a record instead, as long as you leave the `id` property out of the `equals` and `hashCode` calculations:

   ```java
   package com.kousenit.reactivecustomers.entities;

   import org.springframework.data.annotation.Id;
   import java.util.Objects;

   // Note: You can use records here, but be sure to override equals() and hashCode()
   // so that they use the non-id properties only

   public record Customer(@Id Long id, String firstName, String lastName) {

       @Override
       public boolean equals(Object o) {
           if (this == o) return true;
           if (o == null || getClass() != o.getClass()) return false;
           Customer customer = (Customer) o;
           return Objects.equals(firstName, customer.firstName) && 
                  Objects.equals(lastName, customer.lastName);
       }

       @Override
       public int hashCode() {
           return Objects.hash(firstName, lastName);
       }
   }
   ```

5. Make a Spring Data interface called `CustomerRepository` that extends `ReactiveCrudRepository<Customer, Long>` in the `com.kousenit.reactivecustomers.dao` package. Add to the interface a query method to retrieve the customers by last name.

   ```java
   package com.oreilly.reactiveofficers.dao;

   import com.kousenit.reactivecustomers.entities.Customer;
   import org.springframework.data.repository.reactive.ReactiveCrudRepository;

   public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {
       Flux<Customer> findByLastName(String lastName);
   }
   ```

6. We need to create a database to store the data. Here we'll use H2. When we created the project, the Spring Initializr provided an H2 database driver that supports R2DBC. To create the database, add a file called `schema.sql` to the `src/main/resources` folder, containing the following table definition (don't forget the trailing semicolon):

   ```sql
   create table customer
   (
       id         long generated always as identity primary key,
       first_name varchar(20) not null,
       last_name  varchar(20) not null
   );
   ```

7. Create a test for the repository called `CustomerRepositoryTest`. Add the annotation `@DataR2dbcTest` to the class.

8. Autowire in an instance of `CustomerRepository` called `repository`.

9. Provide initialization data in the form of a list of customers:

   ```java
   private final List<Customer> customers = List.of(
           new Customer(null, "Malcolm", "Reynolds"),
           new Customer(null, "Zoë", "Washburne"),
           new Customer(null, "Hoban", "Washburne"),
           new Customer(null, "Jayne", "Cobb"),
           new Customer(null, "Kaylee", "Frye"));
   ```

10. Note that the `id` fields will be null until the officers are saved. To save them, add a method called `setUp` that takes no arguments and returns `void`. Annotated it with `@BeforeEach` from JUnit 5. This method reset the database it before each test, though the individual rows will use different primary keys.

11. The body of the `setUp` method is:

    ```java
    @BeforeEach
    void setUp() {
        customers = repository.deleteAll()
                .thenMany(Flux.fromIterable(customers))
                .flatMap(repository::save)
                .collectList().block();
    }
    ```

12. Test `findAll` by checking that there are five customers in the test collection:

    ```java
    @Test
    public void fetchAllCustomers() {
        repository.findAll()
                    .doOnNext(System.out::println)
                    .as(StepVerifier::create)
                    .expectNextCount(5)
                    .verifyComplete();
    }
    ```

13. Check the other query methods by fetching the first customer by id, then searching by last name.

    ```java
    @Test
    void fetchCustomerById() {
        repository.findById(customers.get(0).id())
                .doOnNext(System.out::println)
                .as(StepVerifier::create)
                .expectNextMatches(customer -> customer.firstName().equals("Malcolm"))
                .verifyComplete();
    }

    @Test
    void fetchCustomersByLastName() {
        repository.findByLastName("Washburne")
                .doOnNext(System.out::println)
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }
    ```

14. Add three more tests to verify you can insert, update, and delete customers:

    ```java
    @Test
    void insertCustomer() {
        Customer newCustomer = new Customer(null, "Inara", "Serra");
        repository.save(newCustomer)
                .doOnNext(System.out::println)
                .as(StepVerifier::create)
                .expectNextMatches(customer -> customer.firstName().equals("Inara"))
                .verifyComplete();
    }

    @Test
    void updateCustomer() {
        Customer updatedCustomer = new Customer(customers.get(0).id(), 
            "Malcolm", "Reynolds, Jr.");
        repository.save(updatedCustomer)
                .doOnNext(System.out::println)
                .as(StepVerifier::create)
                .expectNextMatches(customer -> customer.firstName().equals("Malcolm"))
                .verifyComplete();
    }

    @Test
    void deleteCustomer() {
        repository.deleteById(customers.get(0).id())
                .doOnNext(System.out::println)
                .as(StepVerifier::create)
                .verifyComplete();
    }
    ```

15. The tests should all pass. You can see the SQL being executed by adding the following line to the file `application.properties` in the `src/main/resources` folder:
    `logging.level.org.springframework.r2dbc=debug`.

    > [!TIP]
    > Add the `.log()` method to any reactive stream to see the underlying calls to `subscribe`, `onNext`, and so on.

## Spring WebFlux with Annotated Controllers

1. Initialize a collection with sample data using a `CommandLineRunner` from Spring. To do so, create a class called `AppInit` in the `com.kousenit.reactivecustomers.config` package. We could use a `@Component` class for this, but let's use the Java configuration approach instead, since it's a useful technique to know.

   > [!NOTE]
   > If the database was not being reset every time the application starts, this step would not be necessary. Since it is, a `CommandLineRunner` is a convenient way to initialize it.

2. Annotate the class with `@Configuration`, marking it as a Java configuration class that will be read on start up.

3. Add a method to the class called `initializeDatabase`, annotated with `@Bean`, which takes an argument of type `CustomerRepository` and returns a `CommandLineRunner`, containing the following code:

   ```java
   @Configuration
   public class AppInit {
       @Bean
       public CommandLineRunner initializeDatabase(CustomerRepository repository) {
           return args ->
               repository.count().switchIfEmpty(Mono.just(0L))
                       .flatMapMany(count -> repository.deleteAll()
                               .thenMany(Flux.just(
                                       new Customer(null, "Malcolm", "Reynolds"),
                                       new Customer(null, "Zoë", "Washburne"),
                                       new Customer(null, "Hoban", "Washburne"),
                                       new Customer(null, "Jayne", "Cobb"),
                                       new Customer(null, "Kaylee", "Frye")))
                               .flatMap(repository::save))
                       .subscribe(System.out::println);
       }
   }
   ```

4. The initialization adds new customers to the database if the count is zero.

5. To add a controller, let's start with the controller tests. We'll use Spring's functional testing capability, where it can automatically start up a test server, deploy our application, run a series of tests, and shut down the server.

6. Create a class in the `controllers` package under `src/test/java` called `CustomerControllerTest`.

7. Annotate the class with `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)`. This tells Spring to start up a test server on any available open port.

8. Inside the class, autowire properties for the `WebTestClient` and a `DatabaseClient`:

   ```java
   @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
   class CustomerControllerTest {

       @Autowired
       private WebTestClient client;

       @Autowired
       private DatabaseClient databaseClient;

   // ... more to come ...
   ```

9. To reinitialize the database between each test, use the `DatabaseClient` to drop the table, recreate it, and insert five rows:

   ```java
   @BeforeEach
   void setUp() {
       var statements = List.of(
               """
               DROP TABLE IF EXISTS customer;
               CREATE TABLE customer(
                   id long generated always as identity primary key,
                   first_name VARCHAR(100) NOT NULL,
                   last_name VARCHAR(100) NOT NULL
               );
               INSERT INTO customer (first_name, last_name) VALUES ('Malcolm', 'Reynolds');
               INSERT INTO customer (first_name, last_name) VALUES ('Zoë', 'Washburne');
               INSERT INTO customer (first_name, last_name) VALUES ('Hoban', 'Washburne');
               INSERT INTO customer (first_name, last_name) VALUES ('Jayne', 'Cobb');
               INSERT INTO customer (first_name, last_name) VALUES ('Kaylee', 'Frye');
               """
           );
       statements.forEach(it -> databaseClient.sql(it)
               .fetch()
               .rowsUpdated()
               .as(StepVerifier::create)
               .expectNextCount(1)
               .verifyComplete());
   }
   ```

10. Note that this uses Text Blocks from Java 17, as well as Local Variable Type Inference (the `var` reserved word) from Java 11. Neither of these are required, but they make entering SQL inside Java much easier.

11. Now add a private method to retrieve all the current id values from the table:

    ```java
    private List<Long> getIds() {
        return databaseClient.sql("select id from customer")
                .map(row -> row.get("id", Long.class))
                .all()
                .collectList()
                .block();
    }
    ```

12. The advantage of the `WebTestClient` is that it already knows the URL of the test server, including the selected port number. Therefore, you can use it like a regular `WebClient`. Here is a test that retrieves all the available customers:

    ```java
    @Test
    void findAll() {
        client.get()
                .uri("/customers")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Customer.class)
                .hasSize(5);
    }
    ```

13. Methods like `expectBodyList(Class)` make it easy to verify that the response JSON body contains `Customer` instances.

14. The test for `findById` uses the private method `getIds()`:

    ```java
    @Test
    void findById() {
        getIds().forEach(id ->
        client.get()
            .uri("/customers/%d".formatted(id))
            .exchange()
            .expectStatus().isOk()
            .expectBody(Customer.class)
            .value(customer -> assertEquals(id, customer.id())));
    }
    ```

15. Test the `create` method by executing an HTTP POST request with a customer in the body:

    ```java
    @Test
    void create() {
        Customer customer = new Customer(null, "Inara", "Serra");
        client.post()
            .uri("/customers")
            .bodyValue(customer)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Customer.class)
            .value(c -> assertEquals("Inara", c.firstName()));
    }
    ```

16. Finally, here are two `delete` tests, one for ids that we know exist, and one for an id that does not exist:

    ```java
    @Test
    void delete() {
        getIds().forEach(id ->
            client.delete()
                    .uri("/customers/%d".formatted(id))
                    .exchange()
                    .expectStatus().isNoContent());
    }

    @Test
    void deleteNotFound() {
        client.delete()
            .uri("/customers/999")
            .exchange()
            .expectStatus().isNotFound();
    }
    ```

17. All of these tests should fail at this point, because we have not yet implemented the controller.

18. Now add a REST controller by creating a class called `CustomerController` in the `com.kousenit.reactivecustomers.controllers` package and annotate the class with `@RestController`.

19. Since all of the methods will be based on the URL path `customers`, add a `@RequestMapping("/customers")` annotation for that to the class.

20. Autowire in the `CustomerRepository`, as shown:

    ```java
    @RestController
    @RequestMapping("/customers")
    public class CustomerController {
        private final CustomerRepository repository;

        @Autowired
        public CustomerController(CustomerRepository repository) {
            this.repository = repository;
        }

    // ... more to come ...
    }
    ```

21. Here are the controller methods, which will be discussed in class:

    ```java
    @GetMapping
    public Flux<Customer> findAll() {
        return repository.findAll();
    }

    @GetMapping("{id}")
    public Mono<Customer> findById(@PathVariable Long id) {
        return repository.findById(id).switchIfEmpty(
                Mono.error(new IllegalArgumentException(
                        "Customer with id %d not found".formatted(id))));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Customer> create(@RequestBody Customer customer) {
        return repository.save(customer);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        "Customer with id %d not found".formatted(id))))
                .flatMap(repository::delete);
    }
    ```

22. Most of the tests will now pass. Two of the controller methods, however, throw an `IllegalArgumentException` when the desired `id` is not in the database. We want to convert that to a "not found" response instead.

23. To do that, in the `controllers` package, add a class called `CustomerAdvice`, with the following contents:

    ```java
    @RestControllerAdvice
    public class CustomerAdvice {

        @ExceptionHandler(IllegalArgumentException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public String handleIllegalArgumentException(IllegalArgumentException e) {
            return e.getMessage();
        }
    }
    ```

24. This will convert any `IllegalArgumentException` into a `NOT_FOUND`. Spring Boot 3 also contains an interesting class called `ProblemDetail` that can be used as an alternative to give back more information. If there is time, this will be discussed during class.

## Functional Web Programming with WebFlux

Spring WebFlux also supports a functional programming approach to web development, which is an alternative to the traditional annotated controller approach shown above. In this approach, you create handler classes with methods that process requests, and then configure routes using a `RouterFunction`.

1. Create a new class called `CustomerHandler` in the `com.kousenit.reactivecustomers.controllers` package and annotate it with `@Component`:

   ```java
   @Component
   public class CustomerHandler {
       private final CustomerRepository repository;

       public CustomerHandler(CustomerRepository repository) {
           this.repository = repository;
       }

       // ... handler methods to come ...
   }
   ```

2. Handler methods take a `ServerRequest` parameter and return a `Mono<ServerResponse>`. Add a method to list all customers:

   ```java
   public Mono<ServerResponse> listCustomers(ServerRequest request) {
       return ServerResponse.ok()
               .contentType(APPLICATION_JSON)
               .body(repository.findAll(), Customer.class);
   }
   ```

3. Add a method to create a new customer:

   ```java
   public Mono<ServerResponse> createCustomer(ServerRequest request) {
       Mono<Customer> customerMono = request.bodyToMono(Customer.class);
       return customerMono.flatMap(customer ->
               ServerResponse.status(HttpStatus.CREATED)
                       .contentType(APPLICATION_JSON)
                       .body(repository.save(customer), Customer.class));
   }
   ```

4. Add a method to get a single customer by ID. Note how we handle the case where the customer is not found:

   ```java
   public Mono<ServerResponse> getCustomer(ServerRequest request) {
       String id = request.pathVariable("id");
       Mono<ServerResponse> notFound = ServerResponse.notFound().build();
       Mono<Customer> customerMono = this.repository.findById(Long.valueOf(id));
       return customerMono
               .flatMap(customer -> ServerResponse.ok()
                       .contentType(APPLICATION_JSON)
                       .body(BodyInserters.fromValue(customer)))
               .switchIfEmpty(notFound);
   }
   ```

5. Add methods for updating and deleting customers:

   ```java
   public Mono<ServerResponse> updateCustomer(ServerRequest request) {
       String id = request.pathVariable("id");
       Mono<Customer> customerMono = request.bodyToMono(Customer.class);
       Mono<ServerResponse> notFound = ServerResponse.notFound().build();
       
       return repository.findById(Long.valueOf(id))
               .flatMap(existingCustomer -> 
                   customerMono.flatMap(customer -> {
                       Customer updatedCustomer = new Customer(Long.valueOf(id), 
                           customer.firstName(), customer.lastName());
                       return ServerResponse.ok()
                               .contentType(APPLICATION_JSON)
                               .body(repository.save(updatedCustomer), Customer.class);
                   }))
               .switchIfEmpty(notFound);
   }

   public Mono<ServerResponse> deleteCustomer(ServerRequest request) {
       String id = request.pathVariable("id");
       Mono<ServerResponse> notFound = ServerResponse.notFound().build();
       
       return repository.findById(Long.valueOf(id))
               .flatMap(customer -> 
                   repository.delete(customer)
                           .then(ServerResponse.noContent().build()))
               .switchIfEmpty(notFound);
   }
   ```

6. Now create a router configuration class called `RouterConfig` in the `com.kousenit.reactivecustomers.config` package:

   ```java
   @Configuration
   public class RouterConfig {
       @Bean
       public RouterFunction<ServerResponse> route(CustomerHandler handler) {
           return RouterFunctions
                   .route(GET("/functional/customers/{id}").and(accept(APPLICATION_JSON)), handler::getCustomer)
                   .andRoute(GET("/functional/customers").and(accept(APPLICATION_JSON)), handler::listCustomers)
                   .andRoute(POST("/functional/customers").and(contentType(APPLICATION_JSON)), handler::createCustomer)
                   .andRoute(PUT("/functional/customers/{id}").and(contentType(APPLICATION_JSON)), handler::updateCustomer)
                   .andRoute(DELETE("/functional/customers/{id}"), handler::deleteCustomer);
       }
   }
   ```

7. Note that the functional endpoints use the path `/functional/customers` to distinguish them from the annotated controller endpoints at `/customers`. This allows you to demonstrate both approaches in the same application.

8. Create a test class for the functional approach called `CustomerHandlerTest` in the `controllers` package under `src/test/java`:

   ```java
   @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
   @AutoConfigureWebTestClient
   class CustomerHandlerTest {

       @Autowired
       private WebTestClient client;

       @Autowired
       private DatabaseClient databaseClient;

       @BeforeEach
       void setUp() {
           var statements = List.of(
                   """
                   DROP TABLE IF EXISTS customer;
                   CREATE TABLE customer(
                       id long generated always as identity primary key,
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL
                   );
                   INSERT INTO customer (first_name, last_name) VALUES ('Malcolm', 'Reynolds');
                   INSERT INTO customer (first_name, last_name) VALUES ('Zoë', 'Washburne');
                   INSERT INTO customer (first_name, last_name) VALUES ('Hoban', 'Washburne');
                   INSERT INTO customer (first_name, last_name) VALUES ('Jayne', 'Cobb');
                   INSERT INTO customer (first_name, last_name) VALUES ('Kaylee', 'Frye');
                   """
           );
           statements.forEach(it -> databaseClient.sql(it)
                   .fetch()
                   .rowsUpdated()
                   .as(StepVerifier::create)
                   .expectNextCount(1)
                   .verifyComplete());
       }

       private List<Long> getIds() {
           return databaseClient.sql("select id from customer")
                   .map(row -> row.get("id", Long.class))
                   .all()
                   .collectList()
                   .block();
       }

       // ... tests to come ...
   }
   ```

9. Add tests for the functional endpoints. Here are examples for the main CRUD operations:

   ```java
   @Test
   void testGetAllCustomers() {
       client.get()
               .uri("/functional/customers")
               .accept(MediaType.APPLICATION_JSON)
               .exchange()
               .expectStatus()
               .isOk()
               .expectHeader()
               .contentType(MediaType.APPLICATION_JSON)
               .expectBodyList(Customer.class)
               .hasSize(5);
   }

   @Test
   void testCreateCustomer() {
       Customer customer = new Customer(null, "Inara", "Serra");
       client.post()
               .uri("/functional/customers")
               .contentType(MediaType.APPLICATION_JSON)
               .accept(MediaType.APPLICATION_JSON)
               .body(Mono.just(customer), Customer.class)
               .exchange()
               .expectStatus()
               .isCreated()
               .expectHeader()
               .contentType(MediaType.APPLICATION_JSON)
               .expectBody()
               .jsonPath("$.id")
               .isNotEmpty()
               .jsonPath("$.firstName")
               .isEqualTo("Inara")
               .jsonPath("$.lastName")
               .isEqualTo("Serra");
   }

   @Test
   void testGetSingleCustomer() {
       List<Long> ids = getIds();
       Long firstId = ids.get(0);

       client.get()
               .uri("/functional/customers/{id}", firstId)
               .exchange()
               .expectStatus()
               .isOk()
               .expectBody()
               .consumeWith(response ->
                       Assertions.assertThat(response.getResponseBody())
                               .isNotNull());
   }
   ```

10. The functional approach provides a more explicit way to handle HTTP requests and responses, giving you complete control over the response building process. It's particularly useful for complex routing scenarios or when you need fine-grained control over request processing.

11. Run both sets of tests to verify that both the annotated controller approach (`/customers`) and the functional approach (`/functional/customers`) work correctly with the same underlying data and business logic.

---

*For questions or clarifications about these exercises, please refer to the course materials or ask your instructor.*