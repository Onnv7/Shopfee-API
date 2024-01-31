FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY .. /app/drinks-backend
RUN mvn package -f /app/drinks-backend/pom.xml

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/drinks-backend/target/drinks-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]