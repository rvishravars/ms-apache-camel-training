FROM docker.io/library/maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY . .
RUN ./mvnw package -DskipTests

FROM docker.io/library/eclipse-temurin:21-jdk
WORKDIR /work/
COPY --from=build /workspace/target/quarkus-app /work/quarkus-app
EXPOSE 8080
ENTRYPOINT ["java","-jar","/work/quarkus-app/quarkus-run.jar"]
