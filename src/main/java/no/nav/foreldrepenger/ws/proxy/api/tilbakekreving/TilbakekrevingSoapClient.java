package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving;

import jakarta.xml.ws.soap.SOAPFaultException;
import no.nav.foreldrepenger.ws.proxy.error.GenerellSoapFaultException;
import no.nav.okonomi.tilbakekrevingservice.KravgrunnlagAnnulerRequest;
import no.nav.okonomi.tilbakekrevingservice.KravgrunnlagAnnulerResponse;
import no.nav.okonomi.tilbakekrevingservice.KravgrunnlagHentDetaljRequest;
import no.nav.okonomi.tilbakekrevingservice.KravgrunnlagHentDetaljResponse;
import no.nav.okonomi.tilbakekrevingservice.TilbakekrevingPortType;
import no.nav.okonomi.tilbakekrevingservice.TilbakekrevingsvedtakRequest;
import no.nav.okonomi.tilbakekrevingservice.TilbakekrevingsvedtakResponse;
import org.springframework.stereotype.Component;

@Component
public class TilbakekrevingSoapClient {

    private final TilbakekrevingPortType klient;

    public TilbakekrevingSoapClient(TilbakekrevingPortType klient) {
        this.klient = klient;
    }

    public KravgrunnlagHentDetaljResponse kravgrunnlagHentDetalj(KravgrunnlagHentDetaljRequest request) {
        try {
            return klient.kravgrunnlagHentDetalj(request);
        } catch (SOAPFaultException e) {
            throw new GenerellSoapFaultException("Henting av kravgrunnlag feilet for TilbakekrevingServiceV1", e);
        }
    }

    public TilbakekrevingsvedtakResponse iverksettTilbakekrevingsvedtak(TilbakekrevingsvedtakRequest request) {
        try {
            return klient.tilbakekrevingsvedtak(request);
        } catch (SOAPFaultException e) {
            throw new GenerellSoapFaultException("F-942048: Iverksetting av tilbakekrevingsvedtak feilet med følgende SOAP feil:", e);
        }
    }

    public KravgrunnlagAnnulerResponse kravgrunnlagAnnuler(KravgrunnlagAnnulerRequest request) {
        try {
            return klient.kravgrunnlagAnnuler(request);
        } catch (SOAPFaultException e) {
            throw new GenerellSoapFaultException("F-942048: Annullering av kravgrunnlag feilet med en generell SOAP feil:", e);
        }
    }
}
