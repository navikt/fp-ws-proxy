package no.nav.foreldrepenger.ws.proxy.error;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import no.nav.foreldrepenger.common.util.TokenUtil;
import no.nav.security.token.support.core.exceptions.JwtTokenValidatorException;
import no.nav.security.token.support.spring.validation.interceptor.JwtTokenUnauthorizedException;
import no.nav.vedtak.exception.IntegrasjonException;

@ControllerAdvice
public class FpWsProxyExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(FpWsProxyExceptionHandler.class);

    private final TokenUtil tokenUtil;

    public FpWsProxyExceptionHandler(TokenUtil tokenUtil) {
        this.tokenUtil = tokenUtil;
    }

    /**
     * Handle custom exceptions
     */
    @ExceptionHandler
    public ResponseEntity<Object> handleIntegrasjonException(IntegrasjonException e, WebRequest req) {
        return logAndRespond(UNPROCESSABLE_ENTITY, e, req);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleJwtUnauthorizedException(JwtTokenUnauthorizedException e, WebRequest req) {
        return logAndRespond(UNAUTHORIZED, e, req);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleUnauthenticatedOIDCException(JwtTokenValidatorException e, WebRequest req) {
        return logAndRespond(FORBIDDEN, e, req, "Token utløper " + e.getExpiryDate());
    }



    /**
     * Handle predefined java exceptions
     */

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return logAndRespond(UNPROCESSABLE_ENTITY, e, request, validationErrors(e));
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleHttpStatusCodeException(HttpStatusCodeException e, WebRequest request) {
        return logAndRespond(e.getStatusCode(), e, request);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleValidationException(ConstraintViolationException e, WebRequest req) {
        return logAndRespond(UNPROCESSABLE_ENTITY, e, req);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleUncaughtExceptions(Exception e, WebRequest req) {
        return logAndRespond(INTERNAL_SERVER_ERROR, e, req);
    }


    private ResponseEntity<Object> logAndRespond(HttpStatus status, Exception e, WebRequest req, Object... messages) {
        return logAndRespond(status, e, req, List.of(messages));
    }

    private ResponseEntity<Object> logAndRespond(HttpStatus status, Exception e, WebRequest req, List<Object> messages) {
        var apiError = new ApiError(status, e, messages);
        var path = fullPathTilKaltEndepunkt(req);
        if (tokenUtil.erAutentisert() && !tokenUtil.erUtløpt()) {
            LOG.warn("[{}] {} {}", path, status, apiError.messages(), e);
        } else {
            LOG.warn("[{}] {} {}", path, status, apiError.messages(),e);
        }
        return handleExceptionInternal(e, apiError, new HttpHeaders(), status, req);
    }

    private static List<String> validationErrors(MethodArgumentNotValidException e) {
        return e.getBindingResult().getFieldErrors()
                .stream()
                .map(FpWsProxyExceptionHandler::errorMessage)
                .toList();
    }

    private static String errorMessage(FieldError error) {
        return error.getField() + " " + error.getDefaultMessage();
    }

    private static String fullPathTilKaltEndepunkt(WebRequest req) {
        try {
            return ((ServletWebRequest)req).getRequest().getRequestURI();
        } catch (Exception e) {
            return req.getContextPath();
        }
    }
}
