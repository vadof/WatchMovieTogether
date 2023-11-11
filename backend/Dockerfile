FROM openjdk:17

WORKDIR /app

COPY target/backend-0.0.1-SNAPSHOT.jar /app/backend.jar

ENTRYPOINT ["java", "-jar", "backend.jar"]