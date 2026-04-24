# Stage 1: Build the Java JAR file using Maven on Amazon Corretto
FROM maven:3.9.15-amazoncorretto-25-al2023 AS build

WORKDIR /app

# Copy the pom.xml
COPY pom.xml .
# Download dependencies. Docker caches this step FOREVER until pom.xml changes!
RUN mvn dependency:go-offline
# Copy the pom.xml and source code into the container
COPY src ./src

# Build the standard Java JAR file
RUN mvn clean package -DskipTests

# Stage 2: Create a minimal runtime container using Amazon Corretto JRE
# Using the headless version to save space since we don't need GUI libraries
FROM amazoncorretto:25-al2023-headless

WORKDIR /app

# Copy the compiled JAR file from the build stage
COPY --from=build /app/target/SudokuSolver-1.0-SNAPSHOT.jar ./sudoku-app.jar

# Run the Java application
ENTRYPOINT ["java", "-cp", "sudoku-app.jar", "com.sudoku.SudokuApplication"]
