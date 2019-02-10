FROM maven:3.6.0-jdk-11-slim@sha256:de42309cb7d717b97505804b686dddbe613d896ca093ed049ec36e289c538260

WORKDIR /app

# Restore maven dependencies in a separate build step
ADD pom.xml .
RUN mvn clean verify --fail-never

ADD src src
RUN mvn package

FROM solsson/jdk-opensource:11.0.2@sha256:9088fd8eff0920f6012e422cdcb67a590b2a6edbeae1ff0ca8e213e0d4260cf8

WORKDIR /app

COPY --from=0 /app/target target

EXPOSE 8080
ENTRYPOINT [ "java", "-cp", "target/streams_store-1.0-SNAPSHOT.jar", "com.bakdata.streams_store.App" ]
