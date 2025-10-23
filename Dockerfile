FROM ghcr.io/navikt/fp-baseimages/chainguard:jre-21
LABEL org.opencontainers.image.source=https://github.com/navikt/fp-ws-proxy

COPY target/*.jar app.jar
