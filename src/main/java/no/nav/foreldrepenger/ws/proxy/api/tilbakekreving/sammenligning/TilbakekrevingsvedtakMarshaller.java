package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.sammenligning;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import no.nav.foreldrepenger.ws.proxy.error.GenerellSoapFaultException;
import no.nav.okonomi.tilbakekrevingservice.TilbakekrevingsvedtakRequest;

public class TilbakekrevingsvedtakMarshaller {

    private static volatile JAXBContext context;

    private TilbakekrevingsvedtakMarshaller() {
        //hindrer instansiering
    }

    public static String marshall(TilbakekrevingsvedtakRequest request) {
        //HAXX marshalling løses normalt sett ikke slik som dette. Se JaxbHelper for normaltilfeller.
        //HAXX gjør her marshalling uten kobling til skjema, siden skjema som brukes ikke er egnet for å
        //HAXX konvertere til streng. Skjemaet er bare egnet for å bruke mot WS.

        try {
            Marshaller marshaller = getContext().createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(request, stringWriter);
            return stringWriter.toString();
        } catch (JAXBException e) {
            throw new GenerellSoapFaultException("FPT-113616: Kunne ikke marshalle vedtak. Noe gikk galt.", e);
        }
    }

    private static JAXBContext getContext() throws JAXBException {
        if (context == null) {
            context = JAXBContext.newInstance(TilbakekrevingsvedtakRequest.class);
        }
        return context;
    }


}
