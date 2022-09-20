package no.nav.foreldrepenger.ws.proxy.sts;

import static no.nav.foreldrepenger.common.util.TokenUtil.BEARER;

public interface SystemTokenTjeneste {

    SystemToken getSystemToken();

    default String bearerToken() {
        return BEARER + getSystemToken().getToken();
    }

}
