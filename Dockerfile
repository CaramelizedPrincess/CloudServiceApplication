FROM openjdk:17-jdk-alpine

EXPOSE  5500

COPY target/CloudServiceApplication-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]