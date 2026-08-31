package no.nav.foreldrepenger.ws.proxy.config;

import no.nav.foreldrepenger.ws.proxy.api.arena.ArenaController;
import no.nav.foreldrepenger.ws.proxy.api.arena.ArenaSoapClient;
import no.nav.foreldrepenger.ws.proxy.config.security.JwtDecoderConfiguration;
import no.nav.foreldrepenger.ws.proxy.config.security.RestAuthenticationEntryPoint;
import no.nav.foreldrepenger.ws.proxy.config.security.SecurityConfiguration;
import no.nav.security.mock.oauth2.MockOAuth2Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ArenaController.class)
@Import({
    ArenaController.class,
    SecurityConfiguration.class,
    JacksonConfiguration.class,
    RestAuthenticationEntryPoint.class,
    JwtDecoderConfiguration.class
})
@EnableConfigurationProperties(OAuth2ResourceServerProperties.class)
class SecurityConfigurationMvcTest {

    private static final String ISSUER_ID = "azuread";
    private static final String SUBJECT = "subject";
    private static final String AUDIENCE = "test-audience";
    private static final String IDTYP = "idtyp";
    private static final String INVALID_AUDIENCE = "invalid-audience";
    private static final String ISSUER_URI_PROPERTY = "spring.security.oauth2.resourceserver.jwt.issuer-uri";
    private static final String AUDIENCES_PROPERTY = "spring.security.oauth2.resourceserver.jwt.audiences";

    private static final MockOAuth2Server mockOauth2Server = new MockOAuth2Server();

    private static final String GYLDIG_ARENA_REQUEST = """
        {"ident":"12345678910"}
        """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArenaSoapClient arenaSoapClient;


    @BeforeAll
    static void setUp() {
        mockOauth2Server.start();
    }

    @AfterAll
    static void tearDown() {
        mockOauth2Server.shutdown();
    }


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add(ISSUER_URI_PROPERTY, mockOauth2Server.issuerUrl(ISSUER_ID)::toString);
        registry.add(AUDIENCES_PROPERTY, () -> AUDIENCE);
    }

    @Test
    void ingenTokenGirUnauthorizedMedFeilDtoBody() throws Exception {
        mockMvc.perform(post("/arena").contentType(MediaType.APPLICATION_JSON).content(GYLDIG_ARENA_REQUEST))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.type").value("MANGLER_TILGANG_FEIL"));
    }

    @Test
    void ugyldigAudGirUnauthorized() throws Exception {
        var ugyldigToken = jwt(INVALID_AUDIENCE, Map.of(IDTYP, "app"));
        mockMvc.perform(post("/arena").headers(headers -> headers.setBearerAuth(ugyldigToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content(GYLDIG_ARENA_REQUEST))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void ugyldigIdtypGirUnauthorized() throws Exception {
        var ugyldigToken = jwt(AUDIENCE, Map.of(IDTYP, "invalid"));
        mockMvc.perform(post("/arena").headers(headers -> headers.setBearerAuth(ugyldigToken))
            .contentType(MediaType.APPLICATION_JSON)
            .content(GYLDIG_ARENA_REQUEST))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void gyldigAutentisertTokenGirOk() throws Exception {
        when(arenaSoapClient.finnMeldekortUtbetalingsgrunnlagListe(org.mockito.ArgumentMatchers.any())).thenReturn(null);

        var gyldigToken = jwt(AUDIENCE, Map.of(IDTYP, "app"));

        mockMvc.perform(post("/arena").headers(headers -> headers.setBearerAuth(gyldigToken))
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

    private static String jwt(String audience, Map<String, String> claims) {
        return mockOauth2Server.issueToken(ISSUER_ID, SUBJECT, audience, claims).serialize();
    }

    @SpringBootConfiguration
    static class SimplifiedTestApp { }

}
