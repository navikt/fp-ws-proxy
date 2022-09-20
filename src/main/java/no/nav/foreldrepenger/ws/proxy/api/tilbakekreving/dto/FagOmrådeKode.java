package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.dto;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FagOmrådeKode {

    FORELDREPENGER("FP"),
    FORELDREPENGER_ARBEIDSGIVER("FPREF"),
    SYKEPENGER("SP"),
    SYKEPENGER_ARBEIDSGIVER("SPREF"),
    PLEIEPENGER_V1("OOP"),
    PLEIEPENGER_V1_ARBEIDSGIVER("OOPREF"),
    ENGANGSSTØNAD("REFUTG"),
    SVANGERSKAPSPENGER("SVP"),
    SVANGERSKAPSPENGER_ARBEIDSGIVER("SVPREF"),

    //K9
    PLEIEPENGER_SYKT_BARN("PB"),
    PLEIEPENGER_SYKT_BARN_ARBEIDSGIVER("PBREF"),
    PLEIEPENGER_NÆRSTÅENDE("PN"),
    PLEIEPENGER_NÆRSTÅENDE_ARBEIDSGIVER("PNREF"),
    OMSORGSPENGER("OM"),
    OMSORGSPENGER_ARBEIDSGIVER("OMREF"),
    OPPLÆRINGSPENGER("OPP"),
    OPPLÆRINGSPENGER_ARBEIDSGIVER("OPPREF"),
    FRISINN("FRISINN"),
    UDEFINERT("-"),
    ;

    @JsonValue
    private final String kode;

    FagOmrådeKode() {
        this(null);
    }

    FagOmrådeKode(String kode) {
        this.kode = Optional.ofNullable(kode).orElse(name());
    }

    public String getKode() {
        return kode;
    }
}
