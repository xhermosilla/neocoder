FROM eclipse-temurin:22-jdk-alpine AS builder

RUN apk update && apk add --no-cache bash wget unzip

# Install gradle
ENV GRADLE_VERSION=8.10.2
RUN wget https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -P /tmp \
    && unzip /tmp/gradle-${GRADLE_VERSION}-bin.zip -d /opt \
    && rm /tmp/gradle-${GRADLE_VERSION}-bin.zip

ENV GRADLE_HOME=/opt/gradle-${GRADLE_VERSION}
ENV PATH=${GRADLE_HOME}/bin:${PATH}

WORKDIR /usr/src/app

# Copy source code and gradle files
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

COPY build.gradle settings.gradle ./
COPY ./src ./src

# Build application
RUN gradle build -x test --no-daemon

FROM eclipse-temurin:22-jre-alpine AS runtime

WORKDIR /usr/src/app

# Copy the jar from de builder image
COPY --from=builder /usr/src/app/build/libs/*.jar ./app.jar

EXPOSE 3010

ENTRYPOINT ["java", "-jar", "/usr/src/app/app.jar"]
