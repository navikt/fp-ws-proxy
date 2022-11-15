package no.nav.foreldrepenger.ws.proxy.error;

public class UgyldigInputException extends RuntimeException {

    public UgyldigInputException(String message, String ekstraInfo, Exception e) {
        super(tilMessage(message, ekstraInfo), e);
    }

    private static String tilMessage(String message, String ekstraInfo) {
        if (ekstraInfo != null) {
            return message + ekstraInfo;
        }
        return message;
    }
}
