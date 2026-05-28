FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# ✅ Cache deps separately — only re-downloads if pom.xml changes
COPY pom.xml .
RUN mvn -B dependency:go-offline -q

COPY src ./src
RUN mvn -B -DskipTests clean package -q

FROM eclipse-temurin:21-jre
WORKDIR /app

# ✅ Non-root user
RUN groupadd -r appuser && useradd -r -g appuser appuser
COPY --from=build /app/target/*.jar app.jar
RUN chown appuser:appuser app.jar
USER appuser

# ✅ Tunable memory via env var
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"

EXPOSE 1111

# ✅ Health check so Render knows when service is truly ready
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:1111/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
