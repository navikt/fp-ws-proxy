package no.nav.foreldrepenger.ws.proxy.error;

public class FinnesIkkeException extends RuntimeException {

    public FinnesIkkeException(String message, String ekstraInfo, Exception e) {
        super(tilMessage(message, ekstraInfo), e);
    }

    private static String tilMessage(String message, String ekstraInfo) {
        if (ekstraInfo != null) {
            return message + ekstraInfo;
        }
        return message;
    }
}
