package no.nav.foreldrepenger.ws.proxy.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EntraJwtAuthenticationConverterTest {

    private final EntraJwtAuthenticationConverter converter = new EntraJwtAuthenticationConverter();

    @Test
    void accessAsApplicationRoleMappesTilSystemAuthority() {
        var authentication = converter.convert(jwt(List.of("access_as_application")));

        assertThat(authentication).isNotNull();
        assertThat(authentication.getAuthorities())
            .extracting(GrantedAuthority::getAuthority)
            .containsExactly("ROLE_SYSTEM");
    }

    @Test
    void andreRollerGirIkkeSystemAuthority() {
        var authentication = converter.convert(jwt(List.of("some_other_role")));

        assertThat(authentication).isNotNull();
        assertThat(authentication.getAuthorities()).isEmpty();
    }

    @Test
    void manglendeRollerGirIkkeSystemAuthority() {
        var authentication = converter.convert(jwt(null));

        assertThat(authentication).isNotNull();
        assertThat(authentication.getAuthorities()).isEmpty();
    }

    private static Jwt jwt(List<String> roles) {
        var builder = Jwt.withTokenValue("token")
            .header("alg", "none")
            .subject("subject");
        if (roles != null) {
            builder.claim("roles", roles);
        }
        return builder.build();
    }
}
