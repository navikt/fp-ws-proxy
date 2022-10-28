FROM ghcr.io/navikt/fp-baseimages/java:17
LABEL org.opencontainers.image.source=https://github.com/navikt/fp-ws-proxy
COPY target/*.jar app.jar
