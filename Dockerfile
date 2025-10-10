FROM ghcr.io/navikt/fp-baseimages/java:25
LABEL org.opencontainers.image.source=https://github.com/navikt/fp-ws-proxy

ENV TZ=Europe/Oslo
ENV JAVA_OPTS="-Duser.timezone=Europe/Oslo"

ENV JAVA_TOOL_OPTIONS="-Djavax.net.debug=ssl,handshake"

COPY target/*.jar app.jar
