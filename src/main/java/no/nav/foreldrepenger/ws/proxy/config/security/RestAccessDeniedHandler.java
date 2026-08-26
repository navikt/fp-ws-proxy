package no.nav.foreldrepenger.ws.proxy.config.security;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import no.nav.foreldrepenger.ws.proxy.error.AuthFeilResponsSkriver;
import no.nav.foreldrepenger.ws.proxy.error.GenerellExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import tools.jackson.databind.json.JsonMapper;

import no.nav.foreldrepenger.kontrakter.fpwsproxy.error.FeilDto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.error.FeilType;

/**
 * Fanger opp eventuelle AccessDeniedException som Spring Security selv avviser requesten med
 * (f.eks. fra authorizeHttpRequests-regler). @PreAuthorize-avslag fra kontrollerne skjer inne i
 * MVC-dispatchen og fanges normalt av {@link GenerellExceptionHandler} sin
 * AccessDeniedException-håndtering - denne handleren dekker resten av filterkjeden slik at
 * kontrakten (403 + FeilDto) holder uansett hvor avslaget skjer.
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final JsonMapper jsonMapper;

    public RestAccessDeniedHandler(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        var feilDto = new FeilDto(FeilType.MANGLER_TILGANG_FEIL, accessDeniedException.getMessage(), List.of());
        AuthFeilResponsSkriver.skriv(response, jsonMapper, HttpStatus.FORBIDDEN, feilDto);
    }
}
