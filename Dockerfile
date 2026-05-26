# Multi-stage Dockerfile for full ekart-microservices backend
# Builds all modules and runs API Gateway as main entry point
# Uses Java 21 for optimal performance

FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY . .

# Build all modules with Maven
RUN if [ -f mvnw ]; then chmod +x mvnw && ./mvnw -B -DskipTests clean package; else mvn -B -DskipTests clean package; fi \
    && mkdir -p /workspace/dist \
    && find . -type f -path '*/target/*' -name '*.jar' ! -name '*-sources.jar' ! -name '*-javadoc.jar' -exec cp {} /workspace/dist/ \; \
    && echo "=== Build completed ===" && ls -lah /workspace/dist/ && echo "Total JARs: $(ls /workspace/dist/*.jar | wc -l)"

FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

# Copy all built JARs from builder stage
COPY --from=build /workspace/dist/*.jar /app/

# Environment configuration
ENV SERVICE_JAR="api-gateway-*.jar" \
    CONFIG_SERVER_URI="http://localhost:8888" \
    EUREKA_CLIENT_SERVICEURL_DEFAULTZONE="http://localhost:8761/eureka/" \
    SPRING_PROFILES_ACTIVE="docker" \
    JAVA_OPTS="-Xmx1024m -Xms512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Expose ports: API Gateway (8080), Config Server (8888), Service Registry (8761), Admin (1111)
EXPOSE 8080 8888 8761 1111

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD java -version || exit 1

# Run API Gateway as main service entry point
ENTRYPOINT ["sh","-c","JAR=$(ls /app/api-gateway-*.jar 2>/dev/null | head -n1); if [ -z \"$JAR\" ]; then echo 'ERROR: api-gateway JAR not found in /app'; ls -la /app/; exit 1; fi; echo \"Starting: $JAR with JAVA_OPTS=$JAVA_OPTS\"; exec java $JAVA_OPTS -jar \"$JAR\""]