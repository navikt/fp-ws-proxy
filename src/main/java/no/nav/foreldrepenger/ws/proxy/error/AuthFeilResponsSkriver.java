package no.nav.foreldrepenger.ws.proxy.error;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import tools.jackson.databind.json.JsonMapper;

import no.nav.foreldrepenger.kontrakter.fpwsproxy.error.FeilDto;

/**
 * Felles skriving av {@link FeilDto} som JSON fra sikkerhetsfilterkjeden. Denne kjøres FØR
 * @ControllerAdvice er i bildet, så feilkroppen må bygges og skrives eksplisitt her for å
 * bevare ekstern feilkontrakt mot konsumentene.
 */
public final class AuthFeilResponsSkriver {

    private AuthFeilResponsSkriver() {
    }

    public static void skriv(HttpServletResponse response, JsonMapper jsonMapper, HttpStatus status, FeilDto feilDto) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        jsonMapper.writeValue(response.getOutputStream(), feilDto);
    }
}
