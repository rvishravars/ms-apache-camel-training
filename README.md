# Apache Camel Training

This project is a learning microservice for Apache Camel with Quarkus. The
application runs natively on the local host. RabbitMQ is the only external
service and runs locally in Podman.

## Prerequisites

- Java 21 or higher
- JDK 21
- Gradle (or use the included Gradle wrapper)
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
./gradlew build
./gradlew test
```

### Start Quarkus with the Game of Thrones router

Start RabbitMQ first, then enable the Game of Thrones route in
`src/main/resources/application.properties`:

```properties
app.route.gameofthrones.enabled=true
```

Leave the other route flags set to `false` if you only want to run this router.
Then start Quarkus in development mode:

```bash
./gradlew quarkusDev
```

Alternatively, enable the route for one session without editing the file:

```bash
APP_ROUTE_GAMEOFTHRONES_ENABLED=true ./gradlew quarkusDev
```

After startup, `GameOfThronesRouter` sends seven character messages to the
`got-exchange` RabbitMQ exchange and consumes them from `character-queue`.
The route runs once after a two-second delay. Watch the Quarkus logs for
`Sent character to RabbitMQ` and `Received character from RabbitMQ` messages.

For a native executable, build with:

```bash
./gradlew build -Dquarkus.native.enabled=true
```

Then run the generated executable from `build/`:

```bash
./build/ms-apache-camel-training-1.0.0-SNAPSHOT-runner
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

## Monitor routes with Hawtio

Hawtio is integrated for local Camel route monitoring through JMX. Start the
application with the Game of Thrones route enabled:

```bash
podman start rabbitmq-got
APP_ROUTE_GAMEOFTHRONES_ENABLED=true ./gradlew quarkusDev
```

Open the Hawtio console at:

http://localhost:8080/hawtio

Sign in with the local development credentials:

- Username: `hawtio`
- Password: `hawtio`

Select the local JMX connection, then open the Camel plugin to inspect route
status, processors, exchanges, message counts, failures, and execution timing.
The Game of Thrones route is named `route1` for publishing and `route2` for
consuming. Because the publishing timer runs once after a two-second delay,
start Hawtio promptly after launching Quarkus to observe the execution.

Hawtio uses an embedded development user by default. Change these credentials
before sharing the application or exposing the endpoint outside the local
machine.

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

Confirm that the required Camel Quarkus extension is present in
`build.gradle`.

## Learning resources

- Apache Camel documentation
- Camel Quarkus reference
- Quarkus documentation
- RabbitMQ documentation

This project is for educational and training purposes.
