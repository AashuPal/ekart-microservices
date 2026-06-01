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

COPY --from=build /workspace/target/cart-service-*.jar app.jar

RUN mkdir -p /app/logs && chown -R appuser:appuser /app

USER appuser

ENV JAVA_OPTS="-Xms64m -Xmx200m \
               -XX:MaxMetaspaceSize=100m \
               -XX:+UseSerialGC \
               -XX:TieredStopAtLevel=1 \
               -XX:+ExitOnOutOfMemoryError \
               -Djava.security.egd=file:/dev/./urandom \
               -Dspring.jmx.enabled=false"

EXPOSE 8082

HEALTHCHECK --interval=30s --timeout=10s --start-period=120s --retries=3 \
    CMD curl -sf http://localhost:8082/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
