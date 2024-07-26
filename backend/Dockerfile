FROM gradle:8.4-focal AS build

WORKDIR /workspace

COPY src ./src
COPY build.gradle ./build.gradle
COPY settings.gradle ./settings.gradle
RUN gradle bootJar

FROM bellsoft/liberica-openjdk-debian:17

RUN adduser --system spring-boot && addgroup --system spring-boot && adduser spring-boot spring-boot

USER spring-boot

WORKDIR /app

COPY --from=build /workspace/build/libs/JWTSecureTFA-1.0.0.jar ./application.jar

ENTRYPOINT ["java", "-jar", "application.jar"]
