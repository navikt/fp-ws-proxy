package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.dto;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;

public enum KodeResultat {

    FORELDET("FORELDET"),
    FEILREGISTRERT("FEILREGISTRERT"),
    INGEN_TILBAKEKREVING("INGEN_TILBAKEKREV"),
    DELVIS_TILBAKEKREVING("DELVIS_TILBAKEKREV"),
    FULL_TILBAKEKREVING("FULL_TILBAKEKREV"),
    ;

    @JsonValue
    private final String kode;

    KodeResultat() {
        this(null);
    }

    KodeResultat(String kode) {
        this.kode = Optional.ofNullable(kode).orElse(name());
    }

    public String getKode() {
        return kode;
    }
}
