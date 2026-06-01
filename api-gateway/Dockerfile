# ── Stage 1: Build ────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY pom.xml ./
COPY src ./src

RUN mvn -B -f pom.xml -DskipTests clean package

# ── Stage 2: Runtime ──────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl

RUN addgroup -S appuser && adduser -S appuser -G appuser

COPY --from=build /workspace/target/api-gateway-*.jar app.jar

RUN chown appuser:appuser app.jar

USER appuser

ENV JAVA_OPTS="-Xms256m -Xmx512m \
               -XX:+UseG1GC \
               -XX:+ExitOnOutOfMemoryError \
               -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=3 \
    CMD curl -sf http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
