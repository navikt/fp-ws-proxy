package no.nav.foreldrepenger.ws.proxy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.foreldrepenger.common.util.TokenUtil;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;

@Configuration
public class TokenUtilConfiguration {

    public static final String STS_RS = "sts-rs";
    public static final String STS_WS = "sts-ws";


    @Bean
    public TokenUtil tokenUtil(TokenValidationContextHolder contextHolder) {
        return new TokenUtil(contextHolder, "STS", STS_RS, STS_WS); // TODO
    }
}
