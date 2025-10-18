FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /workspace

# copy only pom to download dependencies first (cache layer)
COPY pom.xml ./
RUN mvn -B dependency:go-offline

# copy sources and build
COPY src ./src
RUN mvn -B clean package -DskipTests -DskipITs

FROM eclipse-temurin:17-jre
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=prod

# Install curl for container health checks and diagnostics
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Copy fat jar from builder
COPY --from=builder /workspace/target/*.jar app.jar

EXPOSE ${BACKEND_PORT:-8095}

ENTRYPOINT ["java", "-jar", "app.jar"]
