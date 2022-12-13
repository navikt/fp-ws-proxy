package no.nav.foreldrepenger.ws.proxy.error;

import static java.util.Collections.emptyList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
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
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error.UkjentFeilFraOSException;
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
        return logAndRespond(SERVICE_UNAVAILABLE, e, req);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleSikkerhetsbegrensingException(SikkerhetsbegrensingException e, WebRequest req) {
        return logAndRespond(UNAUTHORIZED, e, req);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleUgyldigInputException(UgyldigInputException e, WebRequest req) {
        return logAndRespond(BAD_REQUEST, e, req);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleMangledeKravgrunnlagException(MangledeKravgrunnlagException e, WebRequest req) {
        return logAndRespond(NOT_FOUND, e, req);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleKravgrunnlagErSperretException(KravgrunnlagErSperretException e, WebRequest req) {
        return logAndRespond(NON_AUTHORITATIVE_INFORMATION, e, req); // TODO: 203??
    }


    @ExceptionHandler
    public ResponseEntity<Object> handleUkjentFeilFraOSException(UkjentFeilFraOSException e, WebRequest req) {
        return logAndRespond(INTERNAL_SERVER_ERROR, e, req);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleFinnesIkkeException(FinnesIkkeException e, WebRequest req) {
        return logAndRespond(NOT_FOUND, e, req);
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
        return logAndRespond(UNAUTHORIZED, e, req);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleUnauthenticatedOIDCException(JwtTokenValidatorException e, WebRequest req) {
        Collection<FeltFeilDto> feilene = new ArrayList<>();
        feilene.add(new FeltFeilDto("Token utløper", e.getExpiryDate().toString()));
        return logAndRespond(FORBIDDEN, e, req, feilene);
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
        return logAndRespond(UNPROCESSABLE_ENTITY, e, request, feilene);
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
        return logAndRespond(UNPROCESSABLE_ENTITY, e, req, feilene);
    }

    private String getFeltNavn(Path propertyPath) {
        return propertyPath instanceof PathImpl pi ? pi.getLeafNode().toString() : null;
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleUncaughtExceptions(Exception e, WebRequest req) {
        return logAndRespond(INTERNAL_SERVER_ERROR, e, req);
    }

    private ResponseEntity<Object> logAndRespond(HttpStatus status, Exception e, WebRequest req) {
        return logAndRespond(status, e, req, emptyList());
    }

    private ResponseEntity<Object> logAndRespond(HttpStatus status, Exception e, WebRequest req, Collection<FeltFeilDto> feltFeil) {
        logException(status, e, req);
        var errorBody = new FeilDto(tilFeilType(status, e), e.getMessage(), feltFeil);
        return handleExceptionInternal(e, errorBody, new HttpHeaders(), status, req);
    }

    private FeilType tilFeilType(HttpStatus status, Exception e) {
        if (e instanceof UkjentFeilFraOSException) {
            return FeilType.KRAVGRUNNLAG_UKJENT_FEIL;
        }
        if (e instanceof OppdragNedetidException) {
            return FeilType.OPPDRAG_FORVENTET_NEDETID;
        }
        return switch (status) {
            case FORBIDDEN -> FeilType.MANGLER_TILGANG_FEIL;
            case NOT_FOUND, NO_CONTENT -> FeilType.TOMT_RESULTAT_FEIL;
            case NON_AUTHORITATIVE_INFORMATION -> FeilType.KRAVGRUNNLAG_SPERRET;
            default -> FeilType.GENERELL_FEIL;
        };
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
