FROM maven:3.9-amazoncorretto-19 AS MAVEN_BUILD

MAINTAINER Serhii Kushnerov

COPY pom.xml /build/
COPY src /build/src/

WORKDIR /build/
RUN mvn package

FROM openjdk:19-alpine
WORKDIR /app
COPY --from=MAVEN_BUILD /build/target/backend-0.1.0.jar /app/
EXPOSE 8080
CMD ["java", "-jar", "backend-0.1.0.jar"]
