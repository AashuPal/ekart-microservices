FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY . .
RUN if [ -f mvnw ]; then chmod +x mvnw && ./mvnw -B -DskipTests clean package; else mvn -B -DskipTests clean package; fi \
    && mkdir -p /workspace/dist \
    && find . -type f -path '*/target/*' -name '*.jar' ! -name '*-sources.jar' ! -name '*-javadoc.jar' -exec cp {} /workspace/dist/ \; \
    && echo "=== Build completed ===" && ls -lah /workspace/dist/ && echo "Total JARs: $(ls /workspace/dist/*.jar | wc -l)"

FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app
COPY --from=build /workspace/dist/*.jar /app/

ENV JAVA_OPTS="-Xmx1024m -Xms512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200" \
    SPRING_PROFILES_ACTIVE="docker"

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD java -version || exit 1

ENTRYPOINT ["sh","-c","JAR=$(ls /app/api-gateway-*.jar 2>/dev/null | head -n1); if [ -z \"$JAR\" ]; then echo 'ERROR: api-gateway JAR not found in /app'; ls -la /app/; exit 1; fi; echo \"Starting: $JAR with JAVA_OPTS=$JAVA_OPTS\"; exec java $JAVA_OPTS -jar \"$JAR\""]