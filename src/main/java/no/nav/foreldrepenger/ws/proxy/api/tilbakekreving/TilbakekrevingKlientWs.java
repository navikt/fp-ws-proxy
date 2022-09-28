package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving;

import org.springframework.stereotype.Component;

import no.nav.okonomi.tilbakekrevingservice.KravgrunnlagAnnulerRequest;
import no.nav.okonomi.tilbakekrevingservice.KravgrunnlagAnnulerResponse;
import no.nav.okonomi.tilbakekrevingservice.KravgrunnlagHentDetaljRequest;
import no.nav.okonomi.tilbakekrevingservice.KravgrunnlagHentDetaljResponse;
import no.nav.okonomi.tilbakekrevingservice.TilbakekrevingPortType;
import no.nav.okonomi.tilbakekrevingservice.TilbakekrevingsvedtakRequest;
import no.nav.okonomi.tilbakekrevingservice.TilbakekrevingsvedtakResponse;

@Component
class TilbakekrevingKlientWs {

    private final TilbakekrevingPortType klient;

    public TilbakekrevingKlientWs(TilbakekrevingPortType klient) {
        this.klient = klient;
    }

    public TilbakekrevingsvedtakResponse tilbakekrevingsvedtak(TilbakekrevingsvedtakRequest request) {
        return klient.tilbakekrevingsvedtak(request); // TODO: Egen definer response dto!
    }

    public KravgrunnlagHentDetaljResponse kravgrunnlagHentDetalj(KravgrunnlagHentDetaljRequest request) {
        return klient.kravgrunnlagHentDetalj(request);
    }

    public KravgrunnlagAnnulerResponse kravgrunnlagAnnuler(KravgrunnlagAnnulerRequest request) {
        return klient.kravgrunnlagAnnuler(request);
    }
}
