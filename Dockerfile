FROM ghcr.io/navikt/fp-baseimages/java:25
LABEL org.opencontainers.image.source=https://github.com/navikt/fp-ws-proxy

ENV TZ=Europe/Oslo
ENV JAVA_OPTS="-Duser.timezone=Europe/Oslo"

ENV JDK_JAVA_OPTIONS="-XX:+PrintCommandLineFlags \
                      -XX:ActiveProcessorCount=2 \
                      -XX:MaxRAMPercentage=75 \
                      -Duser.timezone=Europe/Oslo \
                      -Djava.security.egd=file:/dev/urandom \
                      -Djavax.net.debug=ssl,handshake"
COPY target/*.jar app.jar
