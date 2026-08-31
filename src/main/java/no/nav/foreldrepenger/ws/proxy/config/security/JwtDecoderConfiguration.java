package no.nav.foreldrepenger.ws.proxy.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.time.Duration;
import java.util.List;

@Configuration(proxyBeanMethods = false)
public class JwtDecoderConfiguration {

    private static final String CLAIM_IDTYP = "idtyp";
    private static final String CLAIM_IDTYP_CC_VALUE = "app";


    @Bean
    public JwtDecoder jwtDecoder(@Value("${entra.proxy:#{null}}") URI webproxyUri, OAuth2ResourceServerProperties properties) {
        var issuerUri = properties.getJwt().getIssuerUri();
        Assert.hasText(issuerUri, "Mangler config-verdi for 'spring.security.oauth2.resourceserver.jwt.issuer-uri'!");

        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(4));

        if (webproxyUri != null) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(webproxyUri.getHost(), webproxyUri.getPort()));
            requestFactory.setProxy(proxy);
        }

        var restTemplate = new RestTemplate(requestFactory);

        var jwtDecoder = NimbusJwtDecoder.withIssuerLocation(issuerUri)
            .restOperations(restTemplate)
            .build();

        var audiences = properties.getJwt().getAudiences();
        Assert.isTrue(!audiences.isEmpty(), "Mangler config-verdi for 'spring.security.oauth2.resourceserver.jwt.audiences'!");

        var audienceValidator = new JwtClaimValidator<List<String>>(JwtClaimNames.AUD, aud -> aud.stream().anyMatch(audiences::contains));
        var defaultValidators = JwtValidators.createDefaultWithIssuer(issuerUri);
        var clientCredentialsOnlyValidator = new JwtClaimValidator<String>(CLAIM_IDTYP, CLAIM_IDTYP_CC_VALUE::equals);
        var clockSkewValidator = new JwtTimestampValidator(Duration.ofSeconds(30));
        var combinedValidator = new DelegatingOAuth2TokenValidator<>(defaultValidators, clientCredentialsOnlyValidator, clockSkewValidator, audienceValidator);
        jwtDecoder.setJwtValidator(combinedValidator);

        return jwtDecoder;
    }
}
