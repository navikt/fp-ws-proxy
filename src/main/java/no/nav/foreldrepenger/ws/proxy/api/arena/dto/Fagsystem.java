package no.nav.foreldrepenger.ws.proxy.api.arena.dto;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Fagsystem {

    BISYS("BISYS"),
    BIDRAGINNKREVING("BIDRAGINNKREVING"),
    FPSAK("FPSAK"),
    FPABAKUS("FPABAKUS"),
    K9SAK("K9SAK"),
    VLSP("VLSP"),
    TPS("TPS"),
    JOARK("JOARK"),
    INFOTRYGD("INFOTRYGD"),
    ARENA("ARENA"),
    INNTEKT("INNTEKT"),
    MEDL("MEDL"),
    GOSYS("GOSYS"),
    GRISEN("GRISEN"),
    GSAK("GSAK"),
    HJE_HEL_ORT("HJE_HEL_ORT"),
    ENHETSREGISTERET("ENHETSREGISTERET"),
    AAREGISTERET("AAREGISTERET"),
    PESYS("PESYS"),
    SKANNING("SKANNING"),
    VENTELONN("VENTELONN"),
    UNNTAK("UNNTAK"),
    ØKONOMI("ØKONOMI"),
    ØVRIG("ØVRIG"),
    UDEFINERT("-"),
    ;


    @JsonValue
    private final String kode;

    Fagsystem() {
        this(null);
    }

    Fagsystem(String kode) {
        this.kode = Optional.ofNullable(kode).orElse(name());
    }

    public String getKode() {
        return kode;
    }
}
