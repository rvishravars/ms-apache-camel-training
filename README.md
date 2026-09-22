# Apache Camel Training - Learning Microservice

This project is a learning and training microservice for Apache Camel, designed to practice and understand the fundamental concepts of integration using Apache Camel with Quarkus.

## Installation Guide

This project can be run on Ubuntu 24.04 in three common ways:

- Local native host execution
- Podman container execution
- Kubernetes (K3s) deployment

Choose the path that matches your environment. The project is designed to run the Camel Quarkus app and can be limited to the RabbitMQ Game of Thrones router for deployment scenarios.

### 1) Local native execution

Use this when you want to run the app directly on the host for development and debugging.

#### Build

```bash
./mvnw package
```

#### Test

```bash
./mvnw test
```

#### Deploy

```bash
./mvnw quarkus:dev
```

This is the simplest option for learning and debugging route behavior.

### 2) Podman execution

#### 2.1 Install Podman and supporting tools

Run this on Ubuntu 24.04:

```bash
sudo apt update
sudo apt install -y podman curl vim git
```

#### 2.2 Build

The multi-stage Dockerfile builds the Quarkus application in a Maven builder stage and copies only the runtime artifacts into the final image:

```bash
cd /path/to/ms-apache-camel-training
./mvnw test
podman build -f src/main/docker/Dockerfile.jvm \
  -t localhost/ms-apache-camel-training:latest .
```

#### 2.3 Test

Run the Java tests before building or deploying the image:

```bash
./mvnw test
```

#### 2.4 Deploy

Start RabbitMQ locally:

```bash
podman run -d --name rabbitmq-got \
  -p 5672:5672 \
  -p 15672:15672 \
  docker.io/library/rabbitmq:3-management
```

Check it is running:

```bash
podman ps
```

Open the management UI at:

- http://localhost:15672
- Username: `guest`
- Password: `guest`

Run the app locally with Podman:

```bash
podman run --rm -p 8080:8080 \
  -e RABBITMQ_HOST=host.containers.internal \
  -e RABBITMQ_PORT=5672 \
  -e RABBITMQ_USERNAME=guest \
  -e RABBITMQ_PASSWORD=guest \
  localhost/ms-apache-camel-training:latest
```

### 3) Kubernetes (K3s) deployment

Use this on a real Ubuntu 24.04 host with sudo access.

#### 3.1 Install Kubernetes (K3s)

```bash
curl -sfL https://get.k3s.io | sudo sh -
```

Then verify Kubernetes is active:

```bash
sudo kubectl get nodes
```

If you prefer to use the k3s binary directly:

```bash
sudo k3s kubectl get nodes
```

#### 3.2 Build

Build and validate the application before packaging it for Kubernetes:

```bash
./mvnw test
podman build -f src/main/docker/Dockerfile.jvm \
  -t localhost/ms-apache-camel-training:latest .
```

#### 3.3 Test

Run the app tests locally before deploying to the cluster:

```bash
./mvnw test
```

#### 3.4 Deploy RabbitMQ to Kubernetes

Apply the RabbitMQ manifest:

```bash
kubectl apply -f k8s/rabbitmq-service.yaml
```

This creates the RabbitMQ service and deployment needed by the Game of Thrones example.

#### 3.5 Deploy the Camel app with only the RabbitMQ router enabled

K3s uses its own containerd image store, separate from Podman. Import the locally
built image into the `k8s.io` namespace before applying the deployment:

```bash
podman save localhost/ms-apache-camel-training:latest | \
  sudo k3s ctr -n k8s.io images import -
```

Verify the image is available to Kubernetes:

```bash
sudo k3s ctr -n k8s.io images list | grep ms-apache-camel-training
```

Apply the app manifest:

```bash
kubectl apply -f k8s/game-of-thrones-router.yaml
```

The manifest uses `localhost/ms-apache-camel-training:latest` with
`imagePullPolicy: Never`, so Kubernetes uses the imported local image instead of
trying to pull it from a registry.

This manifest sets the following route flags:

```yaml
- name: APP_ROUTE_EXERCISE1_ENABLED
  value: "false"
- name: APP_ROUTE_EXERCISE2_ENABLED
  value: "false"
- name: APP_ROUTE_EXERCISE3_ENABLED
  value: "false"
- name: APP_ROUTE_EXERCISE4_ENABLED
  value: "false"
- name: APP_ROUTE_EXERCISE5_ENABLED
  value: "false"
- name: APP_ROUTE_TRAINING_ENABLED
  value: "false"
- name: APP_ROUTE_GAMEOFTHRONES_ENABLED
  value: "true"
```

#### 3.6 Check pod and log status

```bash
kubectl get pods
kubectl get svc
kubectl logs -l app=camel-got-router -f
```

#### 3.7 Verify the RabbitMQ app is working

The Game of Thrones router publishes character messages to RabbitMQ. To inspect messages:

```bash
kubectl port-forward svc/rabbitmq 15672:15672
```

Then open:

- http://localhost:15672
- Username: `guest`
- Password: `guest`

### Installation notes

- Kubernetes is best used on a real Ubuntu 24.04 machine with root/sudo access.
- The project includes route enablement flags so you can keep one app but start only the desired router.
- The Game of Thrones example is the one configured for the Kubernetes deployment example.
- After rebuilding the image, import it into k3s again and restart the deployment:

```bash
podman build -f src/main/docker/Dockerfile.jvm \
  -t localhost/ms-apache-camel-training:latest .
podman save localhost/ms-apache-camel-training:latest | \
  sudo k3s ctr -n k8s.io images import -
sudo kubectl rollout restart deployment/camel-got-router
```

## 📚 Description

This project contains a series of practical exercises covering basic and advanced Apache Camel concepts, including:

- Basic components (Timer, Direct)
- Message processing
- Data transformation
- RabbitMQ integration
- Enterprise Integration Patterns (EIPs)

## 🛠️ Technologies Used

- Java 21
- Quarkus 3.8.6
- Apache Camel
- RabbitMQ
- Maven

## 📁 Project structure

```text
src/main/java/co/com/fduenasc/
├── Exercise1Router.java
├── Exercise2Router.java
├── Exercise3Router.java
├── Exercise4Router.java
├── Exercise5Router.java
├── GameOfThronesRouter.java
├── RabbitMQConfiguration.java
├── TrainingRouter.java
└── ...
```

## 📖 Included exercises

### Exercise 1: Timer Component

- Objective: learn to use Apache Camel's `timer` component
- Functionality: prints sequential numbers every 3 seconds

### Exercise 2: Direct endpoint and log component

- Objective: learn to use `direct` endpoints and `log`
- Functionality: reads from `direct:start` and prints a fixed message

### Exercise 3: Custom processor

- Objective: transform message content with a custom processor
- Functionality: converts text to uppercase

### Exercise 4: Content-based router

- Objective: route messages by content using `choice()`
- Functionality: sends admin/user/default messages to different logs

### Exercise 5: Dynamic API routing

- Objective: route to different HTTP endpoints using `toD()`
- Functionality: route user/admin requests to mock APIs based on content

### GameOfThronesRouter

- Objective: publish JSON messages to RabbitMQ
- Functionality: generates characters, splits the array, and sends each one to a RabbitMQ exchange

### TrainingRouter

- Objective: basic timer-based message logging
- Functionality: prints a fixed message repeatedly

## 📋 Prerequisites

Before running this project, make sure you have installed:

- Java 21 or higher
- Maven 3.8+
- RabbitMQ (optional, only for exercises using messaging)

## ⚙️ Configuration

RabbitMQ configuration is located in `src/main/resources/application.properties`:

```properties
rabbitmq.host=localhost
rabbitmq.port=5672
rabbitmq.username=guest
rabbitmq.******

app.route.exercise1.enabled=true
app.route.exercise2.enabled=true
app.route.exercise3.enabled=true
app.route.exercise4.enabled=true
app.route.exercise5.enabled=true
app.route.training.enabled=true
app.route.gameofthrones.enabled=true
```

You can modify these values according to your environment or set them through environment variables in Docker/Kubernetes.
All routers are disabled by default. Set an individual property to `true` only
when you want that router to run.

### Camel Quarkus extensions

The project uses these Camel Quarkus extensions:

- `camel-quarkus-core`
- `camel-quarkus-timer`
- `camel-quarkus-direct`
- `camel-quarkus-http`
- `camel-quarkus-spring-rabbitmq`
- `camel-quarkus-jackson`
- `camel-quarkus-log`

## 🔍 Verification and monitoring

Application logs will show route startup and message processing. Examples:

```text
INFO  [co.com.fduenasc.Exercise1Router] Número secuencial: 0
INFO  [co.com.fduenasc.Exercise3Router] Texto transformado a mayúsculas: HOLA MUNDO
```

### RabbitMQ management UI

- URL: http://localhost:15672
- Username: guest
- Password: guest

## 📚 Learning resources

- Apache Camel documentation
- Camel Quarkus reference
- Quarkus documentation
- RabbitMQ documentation

## 🐛 Troubleshooting

### RabbitMQ container doesn't start

```bash
podman ps | grep rabbitmq
podman start rabbitmq-got
podman logs rabbitmq-got
```

### No messages visible in RabbitMQ

1. Confirm the broker is running
2. Check application logs for RabbitMQ connection errors
3. Verify the exchange and queue are created automatically

### Error: "No endpoint could be found for: direct://..."

Make sure the `camel-quarkus-direct` extension is present in the project:

```xml
<dependency>
    <groupId>org.apache.camel.quarkus</groupId>
    <artifactId>camel-quarkus-direct</artifactId>
</dependency>
```

## 🧭 EIP classification of the examples

- Exercise1Router.java — Polling Consumer

```bash
./mvnw package -Dnative
```

## 📁 Project structure

```text
src/main/java/co/com/fduenasc/
├── Exercise1Router.java
├── Exercise2Router.java
├── Exercise3Router.java
├── Exercise4Router.java
├── Exercise5Router.java
├── GameOfThronesRouter.java
├── RabbitMQConfiguration.java
├── TrainingRouter.java
└── ...
```

## 📖 Included exercises

### Exercise 1: Timer Component

- Objective: learn to use Apache Camel's `timer` component
- Functionality: prints sequential numbers every 3 seconds

### Exercise 2: Direct endpoint and log component

- Objective: learn to use `direct` endpoints and `log`
- Functionality: reads from `direct:start` and prints a fixed message

### Exercise 3: Custom processor

- Objective: transform message content with a custom processor
- Functionality: converts text to uppercase

### Exercise 4: Content-based router

- Objective: route messages by content using `choice()`
- Functionality: sends admin/user/default messages to different logs

### Exercise 5: Dynamic API routing

- Objective: route to different HTTP endpoints using `toD()`
- Functionality: route user/admin requests to mock APIs based on content

### GameOfThronesRouter

- Objective: publish JSON messages to RabbitMQ
- Functionality: generates characters, splits the array, and sends each one to a RabbitMQ exchange

### TrainingRouter

- Objective: basic timer-based message logging
- Functionality: prints a fixed message repeatedly

## ⚙️ Configuration

RabbitMQ configuration is located in `src/main/resources/application.properties`:

```properties
rabbitmq.host=localhost
rabbitmq.port=5672
rabbitmq.username=guest
rabbitmq.password=guest

app.route.exercise1.enabled=true
app.route.exercise2.enabled=true
app.route.exercise3.enabled=true
app.route.exercise4.enabled=true
app.route.exercise5.enabled=true
app.route.training.enabled=true
app.route.gameofthrones.enabled=true
```

You can modify these values according to your environment or set them through environment variables in Docker/Kubernetes.

### Camel Quarkus extensions

The project uses these Camel Quarkus extensions:

- `camel-quarkus-core`
- `camel-quarkus-timer`
- `camel-quarkus-direct`
- `camel-quarkus-http`
- `camel-quarkus-spring-rabbitmq`
- `camel-quarkus-jackson`
- `camel-quarkus-log`

## 🔍 Verification and monitoring

Application logs will show route startup and message processing. Examples:

```text
INFO  [co.com.fduenasc.Exercise1Router] Número secuencial: 0
INFO  [co.com.fduenasc.Exercise3Router] Texto transformado a mayúsculas: HOLA MUNDO
```

### RabbitMQ management UI

- URL: http://localhost:15672
- Username: guest
- Password: guest

## 📚 Learning resources

- Apache Camel documentation
- Camel Quarkus reference
- Quarkus documentation
- RabbitMQ documentation

## 🐛 Troubleshooting

### RabbitMQ container doesn't start

```bash
podman ps | grep rabbitmq
podman start rabbitmq-got
podman logs rabbitmq-got
```

### No messages visible in RabbitMQ

1. Confirm the broker is running
2. Check application logs for RabbitMQ connection errors
3. Verify the exchange and queue are created automatically

### Error: "No endpoint could be found for: direct://..."

Make sure the `camel-quarkus-direct` extension is present in the project:

```xml
<dependency>
    <groupId>org.apache.camel.quarkus</groupId>
    <artifactId>camel-quarkus-direct</artifactId>
</dependency>
```

## 🧭 EIP classification of the examples

- Exercise1Router.java — Polling Consumer
- Exercise2Router.java — Message Translator / simple processing
- Exercise3Router.java — Message Translator
- Exercise4Router.java — Content-Based Router
- Exercise5Router.java — Content-Based Router with dynamic endpoint selection
- GameOfThronesRouter.java — Splitter (plus JSON marshalling)
- TrainingRouter.java — Polling Consumer / simple timer route
- RabbitMQConfiguration.java — infrastructure configuration, not an EIP router

## 📝 Additional notes

- This project is designed for learning and experimentation.
- Exercises are intentionally simple and are meant to be studied in logs.
- Route enablement flags allow a single route to be selected for deployment scenarios such as Kubernetes.

## 🤝 Contributions

This is a learning project. Feel free to experiment, add new exercises, or improve the documentation.

## 📄 License

This project is for educational and training purposes.
