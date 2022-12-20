package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error;

import no.nav.tilbakekreving.typer.v1.MmelDto;

public class ØkonomiKvitteringTilStrengMapper {

    private ØkonomiKvitteringTilStrengMapper() {
    }

    public static String formaterKvittering(MmelDto kvittering) {
        if (kvittering == null) {
            return "Mottok ingen kvittering fra OS. Dette bør følges opp!";
        }

        //HAXX ikke bruk dette som mal ved oppsett av deklarative feil
        //.... brukes her siden det er veldig mange parametre som skal logges
        //.... reduserer sjangsen for at parametre stokkes feil ved fremtidig endring
        StringBuilder builder = new StringBuilder();
        addToBuilder(builder, "Alvorlighetsgrad", kvittering.getAlvorlighetsgrad());
        addToBuilder(builder, "KodeMelding", kvittering.getKodeMelding());
        addToBuilder(builder, "ProgramId", kvittering.getProgramId());
        addToBuilder(builder, "SectionNavn", kvittering.getSectionNavn());
        addToBuilder(builder, "SqlKode", kvittering.getSqlKode());
        addToBuilder(builder, "SqlMelding", kvittering.getSqlMelding());
        addToBuilder(builder, "SqlState", kvittering.getSqlState());
        addToBuilder(builder, "SystemId", kvittering.getSystemId());
        addToBuilder(builder, "MqCompletionKode", kvittering.getMqCompletionKode());
        addToBuilder(builder, "MqReasonKode", kvittering.getMqReasonKode());
        addToBuilder(builder, "BeskrMelding", kvittering.getBeskrMelding());
        return builder.toString();
    }

    private static void addToBuilder(StringBuilder builder, String name, String value) {
        if (value != null) {
            builder.append(" ")
                    .append(name)
                    .append("='")
                    .append(value)
                    .append("'");
        }
    }

}
