package no.nav.foreldrepenger.ws.proxy.api.arena.dto;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;

public enum YtelseType {

    /** Folketrygdloven K4 ytelser. */
    DAGPENGER("DAG"),

    /** Ny ytelse for kompenasasjon for koronatiltak for Selvstendig næringsdrivende og Frilansere (Anmodning 10). */
    FRISINN("FRISINN"),

    /** Folketrygdloven K8 ytelser. */
    SYKEPENGER("SP"),

    /** Folketrygdloven K9 ytelser. */
    PLEIEPENGER_SYKT_BARN("PSB"),
    PLEIEPENGER_NÆRSTÅENDE("PPN"),
    OMSORGSPENGER("OMP"),
    OPPLÆRINGSPENGER("OLP"),

    /** @deprecated Legacy infotrygd K9 ytelse type (må tolkes sammen med TemaUnderkategori). */
    PÅRØRENDESYKDOM("PS"),

    /** Folketrygdloven K11 ytelser. */
    ARBEIDSAVKLARINGSPENGER("AAP"),

    /** Folketrygdloven K14 ytelser. */
    ENGANGSTØNAD("ES"),
    FORELDREPENGER("FP"),
    SVANGERSKAPSPENGER("SVP"),

    /** Folketrygdloven K15 ytelser. */
    ENSLIG_FORSØRGER("EF"),

    UDEFINERT("-"),
    ;

    @JsonValue
    private final String kode;

    YtelseType() {
        this(null);
    }

    YtelseType(String kode) {
        this.kode = Optional.ofNullable(kode).orElse(name());
    }

    public String getKode() {
        return kode;
    }
}
