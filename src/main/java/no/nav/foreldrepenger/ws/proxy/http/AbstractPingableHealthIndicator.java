package no.nav.foreldrepenger.ws.proxy.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

public abstract class AbstractPingableHealthIndicator implements HealthIndicator {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractPingableHealthIndicator.class);

    private final PingEndpointAware pingable;

    protected AbstractPingableHealthIndicator(PingEndpointAware pingable) {
        this.pingable = pingable;
    }

    @Override
    public Health health() {
        try {
            LOG.info("Pinger {} ...", pingable.name());
            pingable.ping();
            LOG.info("Ping {} OK", pingable.name());
            return up();
        } catch (Exception e) {
            return down(e);
        }
    }

    private Health up() {
        return Health.up()
                .build();
    }

    private Health down(Exception e) {
        LOG.warn("Kunne ikke pinge {}", pingable.name(), e);
        return Health.down()
                .withException(e)
                .build();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pingable=" + pingable + "]";
    }
}
