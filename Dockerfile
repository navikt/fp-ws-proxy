FROM ghcr.io/navikt/fp-baseimages/java:25
LABEL org.opencontainers.image.source=https://github.com/navikt/fp-ws-proxy

ENV TZ=Europe/Oslo
ENV JAVA_OPTS="-Duser.timezone=Europe/Oslo"

COPY target/*.jar app.jar
