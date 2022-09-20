package no.nav.foreldrepenger.ws.proxy.sts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class STSTjeneste implements SystemTokenTjeneste {
    private static final Logger LOG = LoggerFactory.getLogger(STSTjeneste.class);
    private final STSConnection connection;
    private SystemToken systemToken;

    public STSTjeneste(STSConnection connection) {
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
