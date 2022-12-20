package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error;

import java.util.Set;

import no.nav.okonomi.tilbakekrevingservice.TilbakekrevingsvedtakResponse;
import no.nav.tilbakekreving.typer.v1.MmelDto;


/**
 * Hvis kvittering er tom eller har alvorlighetsgrad noe annet enn 00 eller 04 -> Generell feil! Feil hardt!
 * Hvis kvittering har alvorlighetsgrad 00 eller 04 -> Sjekk følgende
 *  1) Kodemelding er 'B420010I' -> Finner ikke kravgrunnlag exception
 *  2) Kodemedling er 'B420012I' -> Sperret kravgrunnlag exception
 *  3) Hverken 1) eller 2) men Kodemelding og BeskrMelding inneholder noe info -> Ukjent feil returnert i kvittering.
 *  4) Ellers -> OK
 */
public class ØkonomiKvitteringTolk {

    private static final Set<String> KVITTERING_OK_KODE = Set.of("00", "04");
    public static final String KODE_MELDING_KRAVGRUNNLAG_IKKE_FINNES = "B420010I";
    public static final String KODE_MELDING_KRAVGRUNNLAG_ER_SPERRET = "B420012I";

    ØkonomiKvitteringTolk() {
        // privat construktor
    }

    public static boolean erKvitteringOK(MmelDto kvittering) {
        return kvittering != null && KVITTERING_OK_KODE.contains(kvittering.getAlvorlighetsgrad());
    }

    public static boolean erKvitteringOK(TilbakekrevingsvedtakResponse response) {
        return response != null && erKvitteringOK(response.getMmel());
    }

    public static boolean erKravgrunnlagetIkkeFinnes(MmelDto kvittering) {
        return kvittering != null && KODE_MELDING_KRAVGRUNNLAG_IKKE_FINNES.equals(kvittering.getKodeMelding());
    }

    public static boolean erKravgrunnlagetSperret(MmelDto kvittering) {
        return kvittering != null && KODE_MELDING_KRAVGRUNNLAG_ER_SPERRET.equals(kvittering.getKodeMelding());
    }

    public static boolean erDetUkjentFeilIKvitteringen(MmelDto kvittering) {
        return kvittering != null && !nullOrEmpty(kvittering.getKodeMelding()) && !nullOrEmpty(kvittering.getBeskrMelding());
    }

    private static boolean nullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

}
