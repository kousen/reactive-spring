# Reactive Spring Training Course

This repository contains exercise solutions and hands-on labs for a comprehensive Reactive Spring training course.

## Overview

Learn reactive programming with Spring Boot, Spring WebFlux, and Project Reactor through practical exercises covering:

- **REST Clients**: RestTemplate, WebClient, and HTTP Interfaces
- **Reactive Data Access**: Spring Data R2DBC with H2 database  
- **Reactive Web Programming**: Both annotated controllers and functional approaches
- **Testing**: Comprehensive test coverage with WebTestClient and StepVerifier

## Repository Structure

```
reactive-spring/
├── labs.md                    # Complete lab exercises and instructions
├── restclient/               # REST client examples and solutions
│   ├── RestTemplate          # Synchronous HTTP access
│   ├── WebClient             # Reactive HTTP access
│   └── HTTP Interfaces       # Spring 6+ declarative clients
└── reactive-customers/       # Reactive Spring Data + WebFlux
    ├── Annotated Controllers # Traditional @RestController approach
    ├── Functional Programming # RouterFunction approach  
    ├── R2DBC Integration     # Reactive database access
    └── Comprehensive Tests   # WebTestClient and repository tests
```

## Technologies Used

- **Spring Boot 3.5.2** - Latest stable release
- **Spring WebFlux** - Reactive web framework
- **Spring Data R2DBC** - Reactive database access
- **Project Reactor** - Reactive streams implementation
- **H2 Database** - In-memory database for examples
- **JUnit 5** - Testing framework
- **Gradle 8.14.2** - Build tool

## Getting Started

### Prerequisites

- Java 17 or later
- IDE with Spring Boot support (IntelliJ IDEA, Eclipse, VS Code)

### Running the Examples

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd reactive-spring
   ```

2. **Build all projects**
   ```bash
   ./gradlew build
   ```

3. **Run individual projects**
   ```bash
   # REST client examples
   ./gradlew :restclient:bootRun
   
   # Reactive customers application
   ./gradlew :reactive-customers:bootRun
   ```

4. **Run tests**
   ```bash
   # All tests
   ./gradlew test
   
   # Specific project tests
   ./gradlew :reactive-customers:test
   ```

### Exploring the Labs

1. **Start with `labs.md`** - Complete step-by-step exercises
2. **Check the solutions** - Working implementations in each project
3. **Run the tests** - See reactive programming patterns in action
4. **Experiment** - Modify code and observe reactive behavior

## Lab Exercises

The `labs.md` file contains detailed exercises covering:

1. **Building a REST Client** - RestTemplate basics and JSON parsing
2. **Asynchronous Access** - WebClient and reactive streams
3. **HTTP Interfaces** - Spring 6+ declarative HTTP clients
4. **Project Reactor Tutorial** - Flux and Mono fundamentals
5. **Reactive Spring Data** - R2DBC and reactive repositories
6. **Annotated Controllers** - Traditional Spring MVC style with reactive types
7. **Functional Web Programming** - RouterFunction and handler-based approach

## Key Learning Objectives

- Understand reactive programming concepts and benefits
- Master Project Reactor's Flux and Mono types
- Build reactive REST clients using multiple approaches
- Implement reactive data access with R2DBC
- Create reactive web endpoints using both programming models
- Write effective tests for reactive applications
- Handle backpressure and reactive stream lifecycle

## Course Notes

This repository represents a complete, production-ready example of reactive Spring applications with:

- ✅ **100% test coverage** - All examples include comprehensive tests
- ✅ **Latest Spring Boot** - Uses current stable releases and best practices  
- ✅ **Multiple approaches** - Demonstrates both traditional and functional styles
- ✅ **Real-world patterns** - Includes error handling, testing, and configuration
- ✅ **Clean architecture** - Well-organized, documented, and maintainable code

## Additional Resources

- [Spring WebFlux Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
- [Project Reactor Documentation](https://projectreactor.io/docs)
- [Spring Data R2DBC Reference](https://docs.spring.io/spring-data/r2dbc/docs/current/reference/html/)
- [Reactive Streams Specification](https://www.reactive-streams.org/)

---

*This repository is designed for hands-on learning. Follow the labs, explore the code, run the tests, and experiment with reactive programming concepts to master Spring's reactive stack.*