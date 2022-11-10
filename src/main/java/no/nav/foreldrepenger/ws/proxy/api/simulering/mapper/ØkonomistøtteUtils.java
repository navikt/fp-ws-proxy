package no.nav.foreldrepenger.ws.proxy.api.simulering.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ØkonomistøtteUtils {

    private static final String PATTERN = "yyyy-MM-dd-HH.mm.ss.SSS";

    private ØkonomistøtteUtils() {
        // skjul public constructor
    }

    /**
     * Formats the given LocalDateTime into a String with the given pattern yyyy-MM-dd-HH.mm.ss.SSS
     * @param dt - the object to transform
     * @return a formated string.
     */
    public static String tilSpesialkodetDatoOgKlokkeslett(LocalDateTime dt) {
        if (dt == null) {
            return null;
        }

        var dtf = DateTimeFormatter.ofPattern(PATTERN);
        return dt.format(dtf);
    }
}
