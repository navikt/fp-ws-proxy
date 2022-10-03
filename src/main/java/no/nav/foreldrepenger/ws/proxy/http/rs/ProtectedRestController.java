package no.nav.foreldrepenger.ws.proxy.http.rs;

import static no.nav.foreldrepenger.ws.proxy.config.TokenUtilConfiguration.STS_RS;

import org.springframework.core.annotation.AliasFor;

@no.nav.security.token.support.spring.ProtectedRestController(issuer = STS_RS, claimMap = {})
public @interface ProtectedRestController {
    @AliasFor(annotation = no.nav.security.token.support.spring.ProtectedRestController.class, attribute = "value")
    String[] value() default {};
}
