package no.nav.foreldrepenger.ws.proxy.config.security;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import no.nav.foreldrepenger.ws.proxy.error.AuthFeilResponsSkriver;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import tools.jackson.databind.json.JsonMapper;

import no.nav.foreldrepenger.kontrakter.fpwsproxy.error.FeilDto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.error.FeilType;

/**
 * Erstatter token-support sin håndtering av JwtTokenUnauthorizedException. Manglende eller
 * ugyldig bearer-token (inkludert et utløpt token, som tidligere ga 403 med detaljer om
 * utløpsdato) normaliseres nå til 401 uten ekstra feltfeil - i tråd med vanlig OAuth2-semantikk
 * der 403 er reservert for autentiserte-men-ikke-autoriserte kall.
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final JsonMapper jsonMapper;

    public RestAuthenticationEntryPoint(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        var feilDto = new FeilDto(FeilType.MANGLER_TILGANG_FEIL, authException.getMessage(), List.of());
        AuthFeilResponsSkriver.skriv(response, jsonMapper, HttpStatus.UNAUTHORIZED, feilDto);
    }
}
