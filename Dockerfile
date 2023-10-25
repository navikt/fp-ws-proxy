FROM ghcr.io/navikt/fp-baseimages/java:17
LABEL org.opencontainers.image.source=https://github.com/navikt/k9-ws-proxy

ENV TZ=Europe/Oslo
ENV JAVA_OPTS="-Duser.timezone=Europe/Oslo"

COPY target/*.jar app.jar
