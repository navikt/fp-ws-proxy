package no.nav.foreldrepenger.ws.proxy.http.rs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class STSRSTjeneste implements SystemTokenTjeneste {
    private static final Logger LOG = LoggerFactory.getLogger(STSRSTjeneste.class);
    private final STSRSConnection connection;
    private SystemToken systemToken;

    public STSRSTjeneste(STSRSConnection connection) {
        this.connection = connection;
        LOG.info("System token {}", connection.refresh());
    }

    @Override
    public SystemToken getSystemToken() {
        if (systemToken == null || systemToken.isExpired(connection.getSlack())) {
            systemToken = connection.refresh();
        }
        return systemToken;
    }


}
