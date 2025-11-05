FROM openjdk:17-slim
WORKDIR /app
COPY movie-review-demo/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]