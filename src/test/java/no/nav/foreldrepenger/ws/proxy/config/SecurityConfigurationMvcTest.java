package no.nav.foreldrepenger.ws.proxy.config;

import no.nav.foreldrepenger.ws.proxy.api.arena.ArenaController;
import no.nav.foreldrepenger.ws.proxy.api.arena.ArenaSoapClient;
import no.nav.foreldrepenger.ws.proxy.config.security.SecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
    controllers = ArenaController.class,
    properties = {
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost/issuer",
        "spring.security.oauth2.resourceserver.jwt.audiences=test-audience"
    }
)
@Import({
    ArenaController.class,
    SecurityConfiguration.class,
    JacksonConfiguration.class
})
@EnableConfigurationProperties(OAuth2ResourceServerProperties.class)
@ImportAutoConfiguration(OAuth2ResourceServerAutoConfiguration.class)
class SecurityConfigurationMvcTest {

    private static final String TOKEN = "token";

    private static final String GYLDIG_ARENA_REQUEST = """
        {"ident":"12345678910"}
        """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArenaSoapClient arenaSoapClient;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void protectedEndpointKreverToken() throws Exception {
        mockMvc.perform(post("/arena").contentType(MediaType.APPLICATION_JSON).content(GYLDIG_ARENA_REQUEST))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void manglendeSystemRoleGirForbidden() throws Exception {
        for (var roles : List.of(List.<String>of(), List.of("tilfeldig-verdi"))) {
            mockJwtDecodingWithClaimRoles(roles);

            mockMvc.perform(post("/arena").header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content(GYLDIG_ARENA_REQUEST))
                .andExpect(status().isForbidden());
        }
    }

    @Test
    void gyldigSystemRoleGirOk() throws Exception {
        when(arenaSoapClient.finnMeldekortUtbetalingsgrunnlagListe(org.mockito.ArgumentMatchers.any())).thenReturn(null);
        mockJwtDecodingWithClaimRoles(List.of("access_as_application"));

        mockMvc.perform(post("/arena").header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(GYLDIG_ARENA_REQUEST))
            .andExpect(status().isOk());
    }

    @Test
    void actuatorHealthKanNaesUtenToken() throws Exception {
        // Ingen actuator-infrastruktur er lastet i denne slice-testen, så en treffende rute vil
        // ikke finnes (404) - poenget er å bevise at forespørselen IKKE stoppes av
        // sikkerhetsfilterkjeden (401/403) slik den ville blitt for enhver annen sti.
        var response = mockMvc.perform(get("/actuator/health")).andReturn().getResponse();

        assertThat(response.getStatus()).isNotIn(401, 403);
    }

    private void mockJwtDecodingWithClaimRoles(List<String> roles) {
        when(jwtDecoder.decode(TOKEN)).thenReturn(Jwt.withTokenValue(TOKEN)
            .header("alg", "none")
            .claim("roles", roles)
            .build());
    }

    @SpringBootConfiguration
    static class SimplifiedTestApp { }

}
