# 1. Use Maven with JDK 17 to build the project
FROM maven:3.9.6-eclipse-temurin-17 AS build

# 2. Set working directory
WORKDIR /app

# 3. Copy pom.xml and download dependencies first (for caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# 4. Copy the source code and build the JAR
COPY src ./src
RUN mvn clean package -DskipTests

# 5. Use a smaller JDK image for running the app
FROM openjdk:17-jdk-slim
WORKDIR /app

# 6. Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# 7. Expose the port
EXPOSE 8080

# 8. Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
