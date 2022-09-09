package no.nav.foreldrepenger.ws.proxy.error;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String msg) {
        this(msg, null);
    }

    public NotFoundException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    public NotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
