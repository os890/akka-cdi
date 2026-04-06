# CDI Pekko Integration

A lightweight library that integrates [Apache Pekko](https://pekko.apache.org/) actors with Jakarta CDI, enabling:

- Injection of `ActorRef` and `ActorSystem` via CDI qualifiers
- CDI bean injection within actors
- Multiple named actor systems

Migrated from Akka to [Apache Pekko](https://pekko.apache.org/) (the open-source Akka fork).

## Usage

### Injecting actors

```java
@Inject
@Actor(type = MyActor.class)
private ActorRef standardActor;

@Inject
@Actor(type = MyActor.class, systemName = "alternativeActorSystem")
private ActorRef alternativeActor;
```

### Injecting actor systems

```java
@Inject
private ActorSystem defaultSystem;

@Inject
@ActorSystemName("mainActorSystem")
private ActorSystem namedSystem;
```

### CDI injection in actors

```java
@Dependent
public class MyActor extends AbstractActor {
    @Inject
    private MyService myService;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .matchAny(msg -> myService.process(msg))
            .build();
    }
}
```

## Requirements

| Component     | Version     |
|---------------|-------------|
| Java          | 25+         |
| Maven         | 3.6.3+      |
| CDI API       | 4.1.0       |
| Apache Pekko  | 1.1.3       |
| Scala         | 2.13.16     |

## Code Quality

| Plugin                    | Phase    | Purpose                                         |
|---------------------------|----------|-------------------------------------------------|
| `maven-compiler-plugin`   | compile  | `-Xlint:all` with `failOnWarning=true`          |
| `maven-enforcer-plugin`   | validate | Java 25+, Maven 3.6.3+, banned `javax.*`       |
| `maven-checkstyle-plugin` | validate | Code style enforcement (Checkstyle 10.23.1)     |
| `apache-rat-plugin`       | validate | Apache License 2.0 header verification          |
| `maven-surefire-plugin`   | test     | Test execution                                  |
| `jacoco-maven-plugin`     | test     | Code coverage reporting                         |
| `maven-javadoc-plugin`    | on demand| Javadoc generation                              |

## Testing

Tests use the [dynamic-cdi-test-bean-addon](https://github.com/os890/dynamic-cdi-test-bean-addon)
with full classpath scan to boot a CDI SE container automatically.
Clone and install it locally before building:

```bash
git clone https://github.com/os890/dynamic-cdi-test-bean-addon.git
cd dynamic-cdi-test-bean-addon
mvn clean install
```

## Building

```bash
mvn clean verify
```

## License

[Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)
