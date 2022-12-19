package no.nav.foreldrepenger.ws.proxy.error;

import static java.util.Collections.emptyList;
import static no.nav.foreldrepenger.ws.proxy.error.FeilType.GENERELL_FEIL;
import static no.nav.foreldrepenger.ws.proxy.error.FeilType.KRAVGRUNNLAG_MANGLER;
import static no.nav.foreldrepenger.ws.proxy.error.FeilType.KRAVGRUNNLAG_SPERRET;
import static no.nav.foreldrepenger.ws.proxy.error.FeilType.KVITTERING_UKJENT_FEIL;
import static no.nav.foreldrepenger.ws.proxy.error.FeilType.MANGLER_TILGANG_FEIL;
import static no.nav.foreldrepenger.ws.proxy.error.FeilType.OPPDRAG_FORVENTET_NEDETID;
import static no.nav.foreldrepenger.ws.proxy.error.FeilType.TOMT_RESULTAT_FEIL;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.GONE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NON_AUTHORITATIVE_INFORMATION;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import java.util.ArrayList;
import java.util.Collection;

import javax.validation.ConstraintViolationException;
import javax.validation.Path;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import no.nav.foreldrepenger.ws.proxy.api.simulering.error.OppdragNedetidException;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error.KravgrunnlagErSperretException;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error.MangledeKravgrunnlagException;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error.UkjentFeilIKvitteringFraOSException;
import no.nav.security.token.support.core.exceptions.JwtTokenValidatorException;
import no.nav.security.token.support.spring.validation.interceptor.JwtTokenUnauthorizedException;

@ControllerAdvice
public class GenerellExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GenerellExceptionHandler.class);

    /**
     * Håndtering av ulike custom SOAP exceptions
     */
    @ExceptionHandler
    public ResponseEntity<Object> handleOppdragNedetidException(OppdragNedetidException e, WebRequest req) {
        return logAndRespond(SERVICE_UNAVAILABLE, OPPDRAG_FORVENTET_NEDETID, e, req);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleSikkerhetsbegrensingException(SikkerhetsbegrensingException e, WebRequest req) {
        return logAndRespond(UNAUTHORIZED, MANGLER_TILGANG_FEIL, e, req);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleUgyldigInputException(UgyldigInputException e, WebRequest req) {
        return logAndRespond(BAD_REQUEST, e, req);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleMangledeKravgrunnlagException(MangledeKravgrunnlagException e, WebRequest req) {
        return logAndRespond(GONE, KRAVGRUNNLAG_MANGLER, e, req);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleKravgrunnlagErSperretException(KravgrunnlagErSperretException e, WebRequest req) {
        return logAndRespond(NON_AUTHORITATIVE_INFORMATION, KRAVGRUNNLAG_SPERRET, e, req);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleUkjentFeilFraOSException(UkjentFeilIKvitteringFraOSException e, WebRequest req) {
        return logAndRespond(INTERNAL_SERVER_ERROR, KVITTERING_UKJENT_FEIL, e, req);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleFinnesIkkeException(FinnesIkkeException e, WebRequest req) {
        return logAndRespond(NOT_FOUND, TOMT_RESULTAT_FEIL, e, req);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleGeneralOrUncaughtSoapExceptions(GenerellSoapFaultException e, WebRequest req) {
        return logAndRespond(INTERNAL_SERVER_ERROR, e, req);
    }

    /**
     * Håndtering av ulike custom REST exceptions
     */
    @ExceptionHandler
    public ResponseEntity<Object> handleJwtUnauthorizedException(JwtTokenUnauthorizedException e, WebRequest req) {
        return logAndRespond(UNAUTHORIZED, MANGLER_TILGANG_FEIL, e, req);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleUnauthenticatedOIDCException(JwtTokenValidatorException e, WebRequest req) {
        Collection<FeltFeilDto> feilene = new ArrayList<>();
        feilene.add(new FeltFeilDto("Token utløper", e.getExpiryDate().toString()));
        return logAndRespond(FORBIDDEN, MANGLER_TILGANG_FEIL, e, req, feilene);
    }


    /**
     * Håndtering av generelle REST exceptions
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Collection<FeltFeilDto> feilene = new ArrayList<>();
        for (var fieldError : e.getBindingResult().getFieldErrors()) {
            feilene.add(new FeltFeilDto(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return logAndRespond(UNPROCESSABLE_ENTITY, GENERELL_FEIL, e, request, feilene);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleHttpStatusCodeException(HttpStatusCodeException e, WebRequest request) {
        return logAndRespond(e.getStatusCode(), e, request);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleValidationException(ConstraintViolationException e, WebRequest req) {
        Collection<FeltFeilDto> feilene = new ArrayList<>();
        for (var constraintViolation : e.getConstraintViolations()) {
            var feltNavn = getFeltNavn(constraintViolation.getPropertyPath());
            feilene.add(new FeltFeilDto(feltNavn, constraintViolation.getMessage()));
        }
        return logAndRespond(UNPROCESSABLE_ENTITY, GENERELL_FEIL, e, req, feilene);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleUncaughtExceptions(Exception e, WebRequest req) {
        return logAndRespond(INTERNAL_SERVER_ERROR, e, req);
    }

    private String getFeltNavn(Path propertyPath) {
        return propertyPath instanceof PathImpl pi ? pi.getLeafNode().toString() : null;
    }

    private ResponseEntity<Object> logAndRespond(HttpStatus status, Exception e, WebRequest req) {
        return logAndRespond(status, GENERELL_FEIL, e, req, emptyList());
    }

    private ResponseEntity<Object> logAndRespond(HttpStatus status, FeilType feilType, Exception e, WebRequest req) {
        return logAndRespond(status, feilType, e, req, emptyList());
    }

    private ResponseEntity<Object> logAndRespond(HttpStatus status, FeilType feilType, Exception e, WebRequest req, Collection<FeltFeilDto> feltFeil) {
        logException(status, e, req);
        var errorBody = new FeilDto(feilType, e.getMessage(), feltFeil);
        return handleExceptionInternal(e, errorBody, new HttpHeaders(), status, req);
    }

    private static void logException(HttpStatus status, Exception e, WebRequest req) {
        var path = fullPathTilKaltEndepunkt(req);
        if (loggExceptionPåInfoNivå(e)) {
            LOG.info("[{}] {} {}", path, status, e.getMessage(), e);
        } else {
            LOG.warn("[{}] {} {}", path, status, e.getMessage(), e);
        }
    }

    private static boolean loggExceptionPåInfoNivå(Exception e) {
        if (e instanceof KravgrunnlagErSperretException) return true;
        if (e instanceof MangledeKravgrunnlagException) return true;
        if (e instanceof OppdragNedetidException) return true;
        return false;
    }

    private static String fullPathTilKaltEndepunkt(WebRequest req) {
        try {
            return ((ServletWebRequest)req).getRequest().getRequestURI();
        } catch (Exception e) {
            return req.getContextPath();
        }
    }
}
