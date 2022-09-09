package no.nav.foreldrepenger.ws.proxy.error;

import java.time.LocalDateTime;

public class TokenExpiredException extends UnauthenticatedException {

    public TokenExpiredException(LocalDateTime expDate, Throwable cause) {
        super(expDate, cause);
    }

}
