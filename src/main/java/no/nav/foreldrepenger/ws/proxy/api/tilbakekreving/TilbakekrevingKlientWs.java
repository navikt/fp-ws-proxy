package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving;

import javax.xml.ws.soap.SOAPFaultException;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.ws.proxy.error.GenerellSoapFaultException;
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

    public KravgrunnlagHentDetaljResponse kravgrunnlagHentDetalj(KravgrunnlagHentDetaljRequest request) {
        try {
            return klient.kravgrunnlagHentDetalj(request);
        } catch (SOAPFaultException e) { // NOSONAR
            throw new GenerellSoapFaultException("Henting av kravgrunnlag feilet for TilbakekrevingServiceV1", e);
        }
    }

    public TilbakekrevingsvedtakResponse tilbakekrevingsvedtak(TilbakekrevingsvedtakRequest request) {
        return klient.tilbakekrevingsvedtak(request); // TODO: Egen definer response dto!
    }


    public KravgrunnlagAnnulerResponse kravgrunnlagAnnuler(KravgrunnlagAnnulerRequest request) {
        return klient.kravgrunnlagAnnuler(request);
    }
}
