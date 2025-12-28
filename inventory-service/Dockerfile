FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/inventory-service-1.0-SNAPSHOT.jar inventory-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "inventory-service.jar"]

