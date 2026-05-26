FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy all service files and build using wrapper if present
COPY . .
RUN if [ -f mvnw ]; then chmod +x mvnw && ./mvnw -B -DskipTests package; else mvn -B -DskipTests package; fi

FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the built jar from the build stage
ARG JAR_FILE=target/*.jar
COPY --from=build /workspace/${JAR_FILE} app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
