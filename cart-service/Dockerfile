# ── Stage 1: Build ────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Cache dependency downloads separately from source
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
# Each flag is a separate ENV so the shell never mangles them.
# JAVA_TOOL_OPTIONS is picked up automatically by the JVM without any shell
# interpolation, which is what caused "Unrecognized option: -" on Render.
ENV JAVA_TOOL_OPTIONS="\
-Xms64m \
-Xmx200m \
-XX:MaxMetaspaceSize=100m \
-XX:+UseSerialGC \
-XX:TieredStopAtLevel=1 \
-XX:+ExitOnOutOfMemoryError \
-Djava.security.egd=file:/dev/./urandom \
-Dspring.jmx.enabled=false"

EXPOSE 8082

HEALTHCHECK --interval=30s --timeout=10s --start-period=120s --retries=3 \
    CMD curl -sf http://localhost:8082/actuator/health || exit 1

# Exec form — no shell involved, so WEB_CONCURRENCY and other Render env vars
# are never accidentally forwarded as JVM arguments.
ENTRYPOINT ["java", "-jar", "app.jar"]
