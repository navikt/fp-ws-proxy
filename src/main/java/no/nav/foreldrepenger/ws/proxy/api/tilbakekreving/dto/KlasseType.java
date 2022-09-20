package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.dto;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;

public enum KlasseType {

    FEIL("FEIL"),
    JUST("JUST"),
    SKAT("SKAT"),
    TREK("TREK"),
    YTEL("YTEL"),
    ;

    @JsonValue
    private final String kode;

    KlasseType() {
        this(null);
    }

    KlasseType(String kode) {
        this.kode = Optional.ofNullable(kode).orElse(name());
    }

    public String getKode() {
        return kode;
    }
}
