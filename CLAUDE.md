# CLAUDE.md - Project Context for Reactive Spring Training

## Project Overview

This is a **Reactive Spring Training Course** repository containing exercise solutions and hands-on labs. The project demonstrates reactive programming with Spring Boot, Spring WebFlux, and Project Reactor through practical, working examples.

## Repository Structure

```
reactive-spring/                     <- Multi-module Gradle project (ROOT)
├── README.md                       <- Professional documentation for students  
├── CLAUDE.md                       <- This file - context for Claude
├── .gitignore                      <- Comprehensive ignore patterns
├── labs.md                         <- Complete step-by-step lab exercises
├── slides.md                       <- Slidev presentation (35 slides, 5-hour course)
├── settings.gradle                 <- Multi-module configuration: includes restclient, reactive-customers
├── build.gradle                    <- Shared: Spring Boot 3.5.2, Gradle 8.14.2, Java 17
├── gradlew, gradlew.bat           <- Root-level Gradle wrapper (8.14.2)
├── restclient/                    <- REST client examples (com.kousenit.restclient)
│   ├── build.gradle               <- Dependencies: spring-boot-starter-web, webflux
│   └── src/main/java/com/kousenit/restclient/
│       ├── RestclientApplication.java
│       ├── config/AppConfig.java          <- HTTP Interface proxy factory
│       ├── json/
│       │   ├── Assignment.java            <- Record for astronaut data
│       │   └── AstroResponse.java         <- Record for API response
│       └── services/
│           ├── AstroService.java          <- RestClient, WebClient + Scheduler examples
│           └── AstroInterface.java        <- HTTP Interface (@GetExchange)
└── reactive-customers/            <- Reactive data + web (com.kousenit.reactivecustomers)
    ├── build.gradle               <- Dependencies: spring-data-r2dbc, webflux, h2, r2dbc-h2
    └── src/main/java/com/kousenit/reactivecustomers/
        ├── ReactiveCustomersApplication.java
        ├── config/
        │   ├── AppInit.java               <- CommandLineRunner for data initialization
        │   └── RouterConfig.java          <- Functional web programming routes
        ├── controllers/
        │   ├── CustomerController.java    <- Annotated REST controller (@RestController)
        │   ├── CustomerHandler.java       <- Functional web handler (ServerRequest/Response)
        │   └── CustomerAdvice.java        <- Exception handling (@RestControllerAdvice)
        ├── dao/
        │   └── CustomerRepository.java    <- ReactiveCrudRepository<Customer, Long>
        └── entities/
            └── Customer.java              <- Record with custom equals/hashCode (excludes id)
```

## Key Technologies & Versions

- **Spring Boot**: 3.5.2 (latest stable)
- **Gradle**: 8.14.2 (latest stable)
- **Java**: 17 (minimum requirement)
- **Package Base**: `com.kousenit` (consolidated from previous `com.oreilly`)
- **Database**: H2 with R2DBC (reactive JDBC)
- **Testing**: JUnit 5, WebTestClient, StepVerifier

## Important Design Decisions

### 1. **Consolidated Architecture**
- **Previously**: Had separate `reactive-officers` project with MongoDB
- **Now**: Single `reactive-customers` project with R2DBC (preferred for training)
- **Why**: Consistent database technology, focused learning, reduced complexity

### 2. **Dual Web Programming Approaches**
- **Annotated Controllers**: Traditional `@RestController` style at `/customers`
- **Functional Programming**: `RouterFunction` + `Handler` style at `/functional/customers`
- **Why**: Shows both approaches side-by-side for comparison

### 3. **Multiple REST Client Examples**
- **RestClient**: Modern synchronous approach (Spring 6.1+, replaces RestTemplate)
- **WebClient**: Reactive, non-blocking
- **HTTP Interfaces**: Declarative, proxy-based (@GetExchange)

### 4. **Package Naming Convention**
- **Base**: `com.kousenit` (instructor's domain)
- **Projects**: `com.kousenit.restclient`, `com.kousenit.reactivecustomers`
- **Important**: Labs.md references match actual code packages

## API Endpoints Used

### External APIs (restclient project)
- **Astronaut API**: `http://api.open-notify.org/astros.json` ✅ Working
- **Chuck Norris Joke API**: Removed (was failing, not used in labs)

### Internal APIs (reactive-customers project)
- **Annotated**: `/customers/*` - Traditional REST endpoints
- **Functional**: `/functional/customers/*` - Router-based endpoints

## Database Schema (H2 + R2DBC)

```sql
CREATE TABLE customer (
    id LONG GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL
);
```

**Test Data**: Firefly crew (Malcolm Reynolds, Zoë Washburne, etc.)

## Build & Test Status

- **✅ All builds successful**: Both projects compile and package correctly
- **✅ All tests passing**: 32/32 tests pass (100% success rate)
- **✅ Multi-module structure**: Root gradlew manages both subprojects
- **✅ No external dependencies**: Works offline (except for external API calls)

### Test Breakdown
- **restclient**: 12 tests (11 AstroService with scheduler examples, 1 AstroInterface)
- **reactive-customers**: Multiple test classes for controllers, handlers, repositories

## Labs.md Structure

1. **Building a REST Client** - RestClient basics (modern synchronous client)
2. **Asynchronous Access** - WebClient introduction  
3. **HTTP Interfaces** - Spring 6+ declarative clients
4. **Project Reactor Tutorial** - External GitHub tutorial reference
5. **Working with Schedulers** - Thread management and blocking operations (NEW)
6. **Reactive Spring Data** - R2DBC + repositories
7. **Annotated Controllers** - Traditional @RestController approach
8. **Functional Web Programming** - RouterFunction approach (Optional)

## Common Operations

### Build Commands
```bash
./gradlew build                    # Build all projects
./gradlew :restclient:test         # Test specific project
./gradlew :reactive-customers:bootRun  # Run specific application
```

### Development Notes
- **Use root gradlew**: Subprojects don't have individual wrapper files
- **Port conflicts**: Both apps use default 8080, run separately
- **Database**: H2 resets on restart, uses in-memory storage
- **Tests**: Use `@DataR2dbcTest` for repositories, `@SpringBootTest` for integration

## Recent Changes & History

### Major Refactoring (Latest)
1. **Consolidated Projects**: Removed reactive-officers, enhanced reactive-customers
2. **Package Rename**: com.oreilly → com.kousenit throughout
3. **Version Updates**: Spring Boot 3.5.2, Gradle 8.14.2
4. **Functional Web**: Added RouterConfig, CustomerHandler, CustomerHandlerTest
5. **Scheduler Examples**: Added comprehensive scheduler demonstrations in AstroService
6. **Enhanced Labs**: Added navigation links and working code examples
7. **Slidev Presentation**: Complete 35-slide presentation with progressive reveal
8. **Cleanup**: Removed failing external APIs, duplicate files, outdated docs
9. **Presentation Improvements**: Added course overview, testing tools slide, fixed image URLs

### Git History Pattern
- Multiple commits with detailed messages
- Uses Claude Code attribution format
- Clean commit history with logical groupings

## Training Course Context

**Target Audience**: Java developers learning reactive programming
**Duration**: 5-hour intensive course (upgraded from 4 hours)
**Learning Progression**: REST clients → Schedulers → Reactive data → Reactive web
**Hands-on Focus**: Working code examples, comprehensive tests, practical exercises

### Course Materials Structure
- **slides.md**: 35-slide Slidev presentation with progressive reveal
  - Spring fundamentals → Reactive concepts → WebFlux → Reactive Streams → Advanced topics
  - Includes live code examples, proper reactive interfaces, scheduler demonstrations
  - Professional formatting with clickable links and consistent code fonts
  - Course overview and agenda slides for clear expectations
  - Dedicated testing tools slide covering StepVerifier and WebTestClient
- **labs.md**: Step-by-step hands-on exercises
- **Working solutions**: Complete implementations in restclient/ and reactive-customers/

## Notes for Future Sessions

- **Labs.md is authoritative**: Always check labs against actual code
- **Package consistency critical**: Students copy-paste from labs
- **External APIs can fail**: Focus on patterns, not specific services  
- **Both web approaches important**: Traditional vs. functional comparison valuable
- **Database resets**: Good for training, shows clean slate each run
- **Test coverage complete**: Every major concept has working tests

## Quick Health Check Commands

```bash
# Verify everything works
./gradlew build
./gradlew test

# Check specific functionality  
./gradlew :restclient:test                    # REST clients
./gradlew :reactive-customers:test            # Reactive data + web

# Run applications (test external APIs)
./gradlew :restclient:bootRun                 # Test astronaut API
./gradlew :reactive-customers:bootRun         # Test reactive endpoints
```

## Slidev Presentation Best Practices

### Lessons Learned from slides.md Creation

**Image Implementation:**
- ❌ `background: url` doesn't work on default layouts
- ❌ `layout: cover` only works for title slides, breaks content formatting
- ✅ `layout: image-right` + `image: url` works perfectly for content slides
- ✅ Use specific Unsplash URLs, not dynamic `source.unsplash.com/?terms`
- ✅ Add `class: text-sm` if content is too dense for split layout

**Content Organization:**
- Consolidate duplicate slides using `<v-clicks>` for progressive reveal
- Balance bullet points - keep essential examples, remove verbose sub-bullets
- Use code font (`backticks`) for all Java classes, methods, annotations
- Strategic image placement: 4-5 images max for conceptual slides only

**Technical Setup:**
- Restart Slidev after adding images or major changes
- Use `npx @slidev/cli slides.md` for viewing
- Theme `seriph` works well for technical presentations
- Progressive reveal keeps audience engaged

**Slide Structure for 5-hour course:**
- 35 slides optimal (8-10 minutes per slide)
- Foundation → Concepts → Implementation → Advanced → Testing
- Include practical code examples in proper code blocks
- Balance theory with hands-on examples
- Course overview at beginning sets expectations

## Troubleshooting

- **Build failures**: Usually dependency or package name issues
- **Test failures**: Check external API availability (restclient)
- **Port conflicts**: Only run one Spring Boot app at a time
- **IDE issues**: Refresh Gradle project, reimport modules
- **Package errors**: Verify com.kousenit base package throughout
- **Slidev images not showing**: Use `layout: image-right` instead of `background:`