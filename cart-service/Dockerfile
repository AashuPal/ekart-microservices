# ── Stage 1: Build ────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml ./
RUN mvn -B dependency:go-offline -q
COPY src ./src
RUN mvn -B -DskipTests clean package -q

# ── Stage 2: Runtime ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl \
 && addgroup -S appuser \
 && adduser  -S appuser -G appuser
COPY --from=build /workspace/target/cart-service-*.jar app.jar
RUN mkdir -p /app/logs && chown -R appuser:appuser /app
USER appuser

# ── JVM tuning ────────────────────────────────────────────────────────────────
ENV JAVA_TOOL_OPTIONS="\
-Xms64m \
-Xmx180m \
-XX:MaxMetaspaceSize=220m \
-XX:ReservedCodeCacheSize=64m \
-XX:+UseSerialGC \
-XX:TieredStopAtLevel=1 \
-XX:+ExitOnOutOfMemoryError \
-Djava.security.egd=file:/dev/./urandom \
-Dspring.jmx.enabled=false \
-Dmanagement.tracing.enabled=false \
-Dmanagement.zipkin.tracing.export.enabled=false"

EXPOSE 8082
HEALTHCHECK --interval=30s --timeout=10s --start-period=180s --retries=3 \
    CMD curl -sf http://localhost:8082/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
