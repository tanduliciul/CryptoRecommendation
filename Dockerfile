FROM adoptopenjdk/openjdk11:alpine-jre
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

# docker build --platform linux/x86_64 -t crypto-recommendation .
# docker run -p 8080:8080 crypto-recommendation:latest