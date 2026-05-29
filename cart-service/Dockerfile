FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -B -DskipTests clean package -q -pl cart-service -am

FROM eclipse-temurin:21-jre-alpine          # Alpine is smaller
WORKDIR /app
RUN apk add --no-cache curl                # Alpine uses apk instead of apt-get
RUN addgroup -S appuser && adduser -S appuser -G appuser
COPY --from=build /app/cart-service/target/*.jar app.jar
RUN chown appuser:appuser app.jar
USER appuser

# Memory settings for Render's 512MB limit
ENV JAVA_OPTS="-Xmx256m -Xms128m -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError -XX:MaxRAMPercentage=70.0"

# CRITICAL: Use dynamic PORT from Render
EXPOSE ${PORT:-8082}

# Healthcheck uses the actual PORT
HEALTHCHECK --interval=30s --timeout=10s --start-period=120s --retries=3 \
    CMD curl -f http://localhost:${PORT:-8082}/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar --server.port=${PORT:-8082}"]
