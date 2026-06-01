# ── Stage 2: Runtime ──────────────────────────
# Alpine is smaller (~50 MB vs ~200 MB for jammy)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Alpine uses apk instead of apt-get
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
