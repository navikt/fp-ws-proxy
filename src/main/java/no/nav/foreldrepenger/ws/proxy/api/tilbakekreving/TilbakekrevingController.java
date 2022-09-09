package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving;

import static no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.TilbakekrevingController.TILBAKEKREVING_PATH;
import static no.nav.foreldrepenger.ws.proxy.config.TokenUtilConfiguration.STS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;

import no.nav.security.token.support.spring.ProtectedRestController;

@ProtectedRestController(issuer = STS, value = TILBAKEKREVING_PATH)
class TilbakekrevingController {

    private static final Logger LOG = LoggerFactory.getLogger(TilbakekrevingController.class);
    public static final String TILBAKEKREVING_PATH = "/tilbakekreving";

    private final TilbakekrevingTjeneste tilbakekrevingTjeneste;

    public TilbakekrevingController(TilbakekrevingTjeneste tilbakekrevingTjeneste) {
        this.tilbakekrevingTjeneste = tilbakekrevingTjeneste;
    }

    @PostMapping
    public void sendTilTilbakekreving(TilbakekrevingDto tilbakekrevingDto) {
        LOG.info("Sender request til tilbakekreving hos økonomi");
        var simuleringWSRequest = TilbakekrevingWSMapper.tilWS(tilbakekrevingDto);
        tilbakekrevingTjeneste.sendWSRequest(simuleringWSRequest);
    }
}
