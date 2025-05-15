# First stage, build the custom JRE
FROM eclipse-temurin:21-jdk-alpine AS jre-builder

RUN mkdir /opt/app
COPY . /opt/app

WORKDIR /opt/app

# Install Gradle and other dependencies
RUN apk update && \
    apk add --no-cache wget unzip openjdk21-jre bash

# Download and use Gradle wrapper
RUN wget https://services.gradle.org/distributions/gradle-8.13-bin.zip && \
    unzip gradle-8.13-bin.zip -d /opt/gradle && \
    rm gradle-8.13-bin.zip

ENV GRADLE_HOME /opt/gradle/gradle-8.13
ENV PATH $GRADLE_HOME/bin:$PATH
#TODO: this should come from other place or make the contextLoads test don't fail when absent
ENV SERVER_VERSION="Containerized"

# Build the application using Gradle
RUN gradle build

# Unpack the generated JAR file
RUN jar xvf build/libs/ggauth-0.0.1-SNAPSHOT.jar
RUN jdeps --ignore-missing-deps -q  \
    --recursive  \
    --multi-release 21  \
    --print-module-deps  \
    --class-path 'BOOT-INF/lib/*'  \
    build/libs/ggauth-0.0.1-SNAPSHOT.jar > modules.txt

# Build small JRE image
RUN $JAVA_HOME/bin/jlink \
         --verbose \
         --add-modules $(cat modules.txt) \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /optimized-jdk-21

# Second stage, Use the custom JRE and build the app image
FROM alpine:latest
ENV JAVA_HOME=/opt/jdk/jdk-21
ENV PATH="${JAVA_HOME}/bin:${PATH}"

#TODO: this should come from other place or make the contextLoads test don't fail when absent
ENV SERVER_VERSION="Containerized"

# copy JRE from the base image
COPY --from=jre-builder /optimized-jdk-21 $JAVA_HOME

# Add app user
ARG APPLICATION_USER=spring

# Create a user to run the application, don't run as root
RUN addgroup --system $APPLICATION_USER &&  adduser --system $APPLICATION_USER --ingroup $APPLICATION_USER

# Create the application directory
RUN mkdir /app && chown -R $APPLICATION_USER /app

COPY --chown=$APPLICATION_USER:$APPLICATION_USER build/libs/ggauth-0.0.1-SNAPSHOT.jar /app/app.jar
COPY --from=jre-builder /opt/app/src/main/resources/application.yml /app

WORKDIR /app

USER $APPLICATION_USER

EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "/app/app.jar" ]
