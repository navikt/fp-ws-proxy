package no.nav.foreldrepenger.ws.proxy;

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

import static no.nav.boot.conditionals.Cluster.profiler;

@EnableCaching
@EnableJwtTokenValidation
@ConfigurationPropertiesScan("no.nav.foreldrepenger.ws.proxy")
@SpringBootApplication
public class K9WsProxyApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(K9WsProxyApplication.class)
                .profiles(profiler())
                .run(args);
    }
}
