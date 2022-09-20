package no.nav.foreldrepenger.ws.proxy.api.arena.dto;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;

public enum YtelseStatus {
    OPPRETTET("OPPR"),
    UNDER_BEHANDLING("UBEH"),
    LØPENDE("LOP"),
    AVSLUTTET("AVSLU"),
    UDEFINERT("-"),
    ;

    @JsonValue
    private final String kode;

    YtelseStatus() {
        this(null);
    }

    YtelseStatus(String kode) {
        this.kode = Optional.ofNullable(kode).orElse(name());
    }

    public String getKode() {
        return kode;
    }
}
