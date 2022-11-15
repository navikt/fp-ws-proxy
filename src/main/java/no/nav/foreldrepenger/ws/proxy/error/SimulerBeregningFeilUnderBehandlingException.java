package no.nav.foreldrepenger.ws.proxy.error;

import no.nav.system.os.eksponering.simulerfpservicewsbinding.SimulerBeregningFeilUnderBehandling;
import no.nav.system.os.tjenester.simulerfpservice.feil.FeilUnderBehandling;

public class SimulerBeregningFeilUnderBehandlingException extends RuntimeException {

    public SimulerBeregningFeilUnderBehandlingException(SimulerBeregningFeilUnderBehandling simulerBeregningFeilUnderBehandling) {
        super(mapFeilUnderBehandlingTilKlarTekst(simulerBeregningFeilUnderBehandling.getFaultInfo()), simulerBeregningFeilUnderBehandling);
    }

    private static String mapFeilUnderBehandlingTilKlarTekst(FeilUnderBehandling fault) {
        if (fault == null) {
            return "Simulering feilet. Feil under behandling.";
        }
        return String.format("Simulering feilet. Mottok feilmelding fra oppdragsystemet: source='%s' type='%s' message='%s' rootcause='%s' timestamp='%s'",
                fault.getErrorSource(), fault.getErrorType(), fault.getErrorMessage(), fault.getRootCause(), fault.getDateTimeStamp());
    }
}
