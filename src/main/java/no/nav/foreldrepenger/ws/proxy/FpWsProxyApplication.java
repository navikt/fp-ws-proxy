package no.nav.foreldrepenger.ws.proxy;

import no.nav.foreldrepenger.ws.proxy.util.ClusterUtil;
import no.nav.foreldrepenger.ws.proxy.util.NaisFileIntoSystemPropertyInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@ConfigurationPropertiesScan("no.nav.foreldrepenger.ws.proxy")
@SpringBootApplication
public class FpWsProxyApplication {

    static void main(String[] args) {
        System.clearProperty("logback.configurationFile");

        var vaultMountPath = "/var/run/secrets/nais.io/serviceuser/";
        new SpringApplicationBuilder(FpWsProxyApplication.class)
            .initializers(
                new NaisFileIntoSystemPropertyInitializer("SYSTEMBRUKER_USERNAME", vaultMountPath + "username"),
                new NaisFileIntoSystemPropertyInitializer("SYSTEMBRUKER_PASSWORD", vaultMountPath + "password"))
            .profiles(ClusterUtil.profiler())
            .run(args);
    }
}
