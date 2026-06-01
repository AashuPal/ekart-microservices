
# ── Stage 1: Build ────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY pom.xml ./
COPY cart-service/pom.xml       cart-service/pom.xml
COPY cart-service/src            cart-service/src
COPY cart-service/mvnw           cart-service/mvnw
COPY cart-service/mvnw.cmd       cart-service/mvnw.cmd

RUN mvn -B -f pom.xml \
        -pl cart-service -am \
        -DskipTests \
        clean package

# ── Stage 2: Runtime ──────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl

RUN addgroup -S appuser && adduser -S appuser -G appuser

COPY --from=build /workspace/cart-service/target/cart-service-*.jar app.jar

RUN chown appuser:appuser app.jar

USER appuser

ENV JAVA_OPTS="-Xms256m -Xmx512m \
               -XX:+UseG1GC \
               -XX:+ExitOnOutOfMemoryError \
               -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8082

HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=3 \
    CMD curl -sf http://localhost:8082/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
