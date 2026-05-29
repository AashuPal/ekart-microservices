# ─────────────────────────────────────────────
# Stage 1 – Build
# ─────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build

ARG SERVICE_NAME
WORKDIR /app

COPY . .
RUN mvn -B -DskipTests clean package -q -pl ${SERVICE_NAME} -am

# ─────────────────────────────────────────────
# Stage 2 – Runtime
# ─────────────────────────────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

RUN groupadd -r appuser && useradd -r -g appuser appuser

ARG SERVICE_NAME
COPY --from=build /app/${SERVICE_NAME}/target/*.jar app.jar
RUN chown appuser:appuser app.jar
USER appuser

ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"

ARG PORT=8080
ENV PORT=${PORT}
EXPOSE ${PORT}

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${PORT}/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
