# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the fat jar into the container at /app
# Assumes the JAR is built in the 'target' directory and named something like biblioteca-0.0.1-SNAPSHOT.jar
# Adjust the source path if your build tool or JAR name differs.
COPY target/*.jar app.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-jar","/app/app.jar"]
