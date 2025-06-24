# Reactive Spring Training Course

This repository contains exercise solutions and hands-on labs for a comprehensive Reactive Spring training course.

## Overview

A 5-hour intensive workshop teaching reactive programming with Spring Boot, Spring WebFlux, and Project Reactor through practical exercises covering:

- **REST Clients**: RestClient, WebClient, and HTTP Interfaces
- **Reactive Data Access**: Spring Data R2DBC with H2 database  
- **Reactive Web Programming**: Both annotated controllers and functional approaches
- **Testing**: Comprehensive test coverage with WebTestClient and StepVerifier
- **Schedulers**: Thread management and handling blocking operations

## Repository Structure

```
reactive-spring/
├── labs.md                    # Complete lab exercises and instructions
├── slides.md                  # Slidev presentation (35 slides, 5-hour course)
├── restclient/               # REST client examples and solutions
│   ├── RestClient            # Modern synchronous HTTP access
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

### Exploring the Course Materials

1. **View the presentation** - `slides.md` (use [Slidev](https://slidev.antfu.me/) or any Markdown viewer)
2. **Follow the labs** - `labs.md` contains complete step-by-step exercises
3. **Check the solutions** - Working implementations in each project
4. **Run the tests** - See reactive programming patterns in action
5. **Experiment** - Modify code and observe reactive behavior

## Lab Exercises

The `labs.md` file contains detailed exercises covering:

1. **Building a REST Client** - RestClient basics and JSON parsing
2. **Asynchronous Access** - WebClient and reactive streams
3. **HTTP Interfaces** - Spring 6+ declarative HTTP clients
4. **Project Reactor Tutorial** - Flux and Mono fundamentals
5. **Working with Schedulers** - Thread management and blocking operations
6. **Reactive Spring Data** - R2DBC and reactive repositories
7. **Annotated Controllers** - Traditional Spring MVC style with reactive types
8. **Functional Web Programming** - RouterFunction and handler-based approach (Optional)

## Key Learning Objectives

- Understand reactive programming concepts and benefits
- Master Project Reactor's Flux and Mono types
- Build reactive REST clients using multiple approaches
- Control thread execution with schedulers for blocking operations
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

## Course Materials

### Presentation
- **slides.md** - Complete 35-slide Slidev presentation covering reactive concepts
- View with [Slidev](https://slidev.antfu.me/): `npx @slidev/cli slides.md`
- Includes course overview, testing tools, and progressive reveal animations
- Or open in any Markdown viewer for reference

### Additional Resources

- [Spring WebFlux Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
- [Project Reactor Documentation](https://projectreactor.io/docs)
- [Spring Data R2DBC Reference](https://docs.spring.io/spring-data/r2dbc/docs/current/reference/html/)
- [Reactive Streams Specification](https://www.reactive-streams.org/)
- [Slidev Documentation](https://slidev.antfu.me/) - For presentation framework

---

*This repository is designed for hands-on learning. Follow the labs, explore the code, run the tests, and experiment with reactive programming concepts to master Spring's reactive stack.*