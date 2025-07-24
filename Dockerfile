# 🧱 Build Stage
FROM gradle:8.7-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle bootJar

# 🚀 Run Stage
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/build/libs/conversion-tracker-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
