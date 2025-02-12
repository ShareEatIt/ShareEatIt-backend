# FROM openjdk:17-jdk
# ARG JAR_FILE=build/libs/*.jar
# COPY ${JAR_FILE} app.jar
# ENTRYPOINT ["java","-Dspring.profiles.active=docker", "-jar", "app.jar"]

FROM arm64v8/eclipse-temurin:17-jdk-focal
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Seoul", "/app.jar"]