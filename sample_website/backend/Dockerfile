FROM adoptopenjdk:11-jre-hotspot
WORKDIR /app
COPY . .
ENV JAVA_TARGET_VERSION=11
CMD ["./gradlew", "run"]