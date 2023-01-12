package no.nav.foreldrepenger.ws.proxy.http.filter;

import static no.nav.foreldrepenger.common.util.Constants.NAV_CALL_ID;
import static no.nav.foreldrepenger.common.util.Constants.NAV_CALL_ID2;
import static no.nav.foreldrepenger.common.util.Constants.NAV_CONSUMER_ID;
import static no.nav.foreldrepenger.common.util.MDCUtil.toMDC;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import no.nav.foreldrepenger.common.util.CallIdGenerator;

/**
 * Brukes ved innkommende requester for å hente ut headere fra request og sette tilsvarende MDC verdier
 *  -   Nav-Consumer-Id
 *  -   Nav-CallId
 */
@Component
public class HeadersToMDCFilterBean extends GenericFilterBean {

    private static final Logger LOG = LoggerFactory.getLogger(HeadersToMDCFilterBean.class);

    private final CallIdGenerator generator = new CallIdGenerator();
    private final String applicationName;

    public HeadersToMDCFilterBean(@Value("${spring.application.name}") String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        putValues(HttpServletRequest.class.cast(request));
        chain.doFilter(request, response);
    }

    private void putValues(HttpServletRequest request) {
        try {
            toMDC(NAV_CONSUMER_ID, getConsumerId(request), applicationName);
            toMDC(NAV_CALL_ID, getCallIdFraRequest(request), generator.create());
        } catch (Exception e) {
            LOG.warn("Noe gikk feil ved propagering av header-verdier for request {}, MDC-verdier er inkomplette",
                    request.getRequestURL(), e);
        }
    }

    private static String getConsumerId(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(NAV_CONSUMER_ID)) // Native client sender over denne, men
            .orElseGet(() -> request.getHeader("Nav-ConsumerId"));  // Nav-ConsumerId logges av: fpoppdrag, fpabakus, fptilbake/k9-tilbake
    }

    private static String getCallIdFraRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Nav-Callid")) // Nav-Callid: Settes av felles Native Rest klient.
            .orElseGet(() -> Optional.ofNullable(request.getHeader(NAV_CALL_ID)) // getHeader() skal være case insensitve, men i tilfelle
            .orElseGet(() -> request.getHeader(NAV_CALL_ID2)));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [generator=" + generator + ", applicationName=" + applicationName + "]";
    }

}
