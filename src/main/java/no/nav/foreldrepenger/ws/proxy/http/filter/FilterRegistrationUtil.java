package no.nav.foreldrepenger.ws.proxy.http.filter;

import java.util.List;

final class FilterRegistrationUtil {

    private static final String ALWAYS = "/*";

    private FilterRegistrationUtil() {
    }

    static List<String> always() {
        return List.of(ALWAYS);
    }

}
