# syntax=docker/dockerfile:1
FROM eclipse-temurin:21-jdk-jammy as deps
WORKDIR /build
COPY --chmod=0755 mvnw mvnw
COPY .mvn/ .mvn/
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 ./mvnw -q -DskipTests dependency:go-offline

FROM deps as package
WORKDIR /build
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 ./mvnw -q -DskipTests package
RUN cp target/*-SNAPSHOT.jar target/app.jar || cp target/*.jar target/app.jar

FROM package as extract
WORKDIR /build
RUN java -Djarmode=layertools -jar target/app.jar extract --destination target/extracted

FROM eclipse-temurin:21-jre-jammy AS final
ARG UID=10001
RUN adduser --disabled-password --gecos "" --home "/nonexistent" --shell "/sbin/nologin" --no-create-home --uid "${UID}" appuser
USER appuser
WORKDIR /app
COPY --from=extract /build/target/extracted/dependencies/          ./
COPY --from=extract /build/target/extracted/snapshot-dependencies/ ./
COPY --from=extract /build/target/extracted/spring-boot-loader/    ./
COPY --from=extract /build/target/extracted/application/           ./
EXPOSE 8080
ENTRYPOINT ["java","org.springframework.boot.loader.launch.JarLauncher"]
