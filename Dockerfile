# muiltiple build with auto to build maven

#FROM maven:3.8.4-openjdk-17-slim AS build
#WORKDIR /app
#COPY . /app/shopfee-api
#RUN mvn package -f /app/shopfee-api/pom.xml
#
#FROM openjdk:17-slim
#WORKDIR /app
#COPY --from=build /app/shopfee-api/target/shopfee-api-1.0-SNAPSHOT.jar app.jar
#COPY . /app/shopfee-api
#
#EXPOSE 8080
#CMD ["java", "-jar", "app.jar"]

# build jar file manual, then run jar in image
FROM openjdk:17-slim
WORKDIR /app
ENV PORT 8080
EXPOSE 8080

# Copy firebasePrivateKey.json
COPY src/main/resources/firebasePrivateKey.json /app/firebasePrivateKey.json

COPY target/*.jar /app/app.jar
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar
