package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.dto;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;

public enum KravStatusKode {
    ANNULERT("ANNU"),
    ANNULLERT_OMG("ANOM"),
    AVSLUTTET("AVSL"),
    BEHANDLET("BEHA"),
    ENDRET("ENDR"),
    FEIL("FEIL"),
    MANUELL("MANU"),
    NYTT("NY"),
    SPERRET("SPER"),
    ;

    @JsonValue
    private final String kode;

    KravStatusKode() {
        this(null);
    }

    KravStatusKode(String kode) {
        this.kode = Optional.ofNullable(kode).orElse(name());
    }

    public String getKode() {
        return kode;
    }
}
