package no.nav.foreldrepenger.ws.proxy.api.arena;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.ws.proxy.http.AbstractPingableHealthIndicator;

@Component
public class ArenaHealthIndicator extends AbstractPingableHealthIndicator {
    public ArenaHealthIndicator(ArenaKlientWs klientWs) {
        super(klientWs);
    }
}
