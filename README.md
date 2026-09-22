# Apache Camel Training

This project is a learning microservice for Apache Camel with Quarkus. The
application runs natively on the local host. RabbitMQ is the only external
service and runs locally in Podman.

## Prerequisites

- Java 21 or higher
- Maven 3.8+ (or use the included Maven wrapper)
- Podman

## Run RabbitMQ with Podman

Create the RabbitMQ container once:

```bash
podman run -d --name rabbitmq-got \
  -p 5672:5672 \
  -p 15672:15672 \
  docker.io/library/rabbitmq:3-management
```

If the container already exists, start it with:

```bash
podman start rabbitmq-got
```

RabbitMQ is available at:

- AMQP: `localhost:5672`
- Management UI: http://localhost:15672
- Username: `guest`
- Password: `guest`

Check the container and inspect its logs with:

```bash
podman ps
podman logs rabbitmq-got
```

## Run the application locally

Build and test the application:

```bash
./mvnw package
./mvnw test
```

Run in Quarkus development mode:

```bash
./mvnw quarkus:dev
```

For a native executable, build with:

```bash
./mvnw package -Dnative
```

Then run the generated executable from `target/`:

```bash
./target/ms-apache-camel-training-1.0.0-SNAPSHOT-runner
```

The default RabbitMQ connection is:

```properties
rabbitmq.host=localhost
rabbitmq.port=5672
rabbitmq.username=guest
rabbitmq.password=guest
```

These values can be overridden with the corresponding `RABBITMQ_*`
environment variables.

## Route configuration

Routes are disabled by default. Enable only the routes being studied in
`src/main/resources/application.properties`:

```properties
app.route.exercise1.enabled=true
app.route.exercise2.enabled=true
app.route.exercise3.enabled=true
app.route.exercise4.enabled=true
app.route.exercise5.enabled=true
app.route.training.enabled=true
app.route.gameofthrones.enabled=true
```

The `GameOfThronesRouter` publishes JSON character messages to the RabbitMQ
exchange `got-exchange`, which is bound to the `character-queue` queue.

## Included exercises

- `Exercise1Router` - Timer component
- `Exercise2Router` - Direct endpoint and logging
- `Exercise3Router` - Custom message processor
- `Exercise4Router` - Content-based routing
- `Exercise5Router` - Dynamic HTTP endpoint routing
- `GameOfThronesRouter` - JSON messages sent to RabbitMQ
- `TrainingRouter` - Timer-based message logging
- `RabbitMQConfiguration` - RabbitMQ connection, exchange, queue, and binding setup

## Project structure

```text
src/main/java/co/com/fduenasc/
├── Exercise1Router.java
├── Exercise2Router.java
├── Exercise3Router.java
├── Exercise4Router.java
├── Exercise5Router.java
├── GameOfThronesRouter.java
├── RabbitMQConfiguration.java
└── TrainingRouter.java
```

## Troubleshooting

### RabbitMQ is not running

```bash
podman start rabbitmq-got
podman logs rabbitmq-got
```

### No messages are visible

Confirm that RabbitMQ is running, the `GameOfThronesRouter` is enabled, and the
application logs do not report a connection error.

### Missing Camel endpoint

Confirm that the required Camel Quarkus extension is present in `pom.xml`.

## Learning resources

- Apache Camel documentation
- Camel Quarkus reference
- Quarkus documentation
- RabbitMQ documentation

This project is for educational and training purposes.
