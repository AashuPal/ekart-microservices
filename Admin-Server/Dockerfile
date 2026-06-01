# Build stage
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app
COPY . .

# Build the current module (remove the -pl flag)
RUN mvn -B -DskipTests clean package -q

# Runtime stage
FROM eclipse-temurin:21-jre

RUN groupadd -r appuser && useradd -r -g appuser appuser
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy the built JAR - adjust path if needed
COPY --from=builder /app/target/*.jar app.jar

USER appuser
EXPOSE 1111
ENTRYPOINT ["java", "-jar", "app.jar"]
