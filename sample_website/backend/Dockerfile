# Use the official OpenJDK image as the base image
FROM adoptopenjdk:11-jre-hotspot

# Set the working directory inside the container
WORKDIR /app

# Copy the Ktor backend source files to the container
COPY . .

# Build the Ktor backend
RUN ./gradlew build

# Command to run the backend application
CMD ["./gradlew", "run"]