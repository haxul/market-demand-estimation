FROM openjdk:11
WORKDIR /webapp
COPY build/libs/haxul-0.0.1.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","./app.jar"]