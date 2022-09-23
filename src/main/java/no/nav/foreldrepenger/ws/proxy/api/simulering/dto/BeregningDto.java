package no.nav.foreldrepenger.ws.proxy.api.simulering.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

public record BeregningDto(String gjelderId,
                           String gjelderNavn,
                           String datoBeregnet,
                           String kodeFaggruppe,
                           BigDecimal belop,
                           List<BeregningsPeriodeDto> beregningsPeriode) {

    public BeregningDto() {
        this(null,null,null,null,null,null);
    }

    @JsonCreator
    public BeregningDto(String gjelderId, String gjelderNavn, String datoBeregnet, String kodeFaggruppe, BigDecimal belop, List<BeregningsPeriodeDto> beregningsPeriode) {
        this.gjelderId = gjelderId;
        this.gjelderNavn = gjelderNavn;
        this.datoBeregnet = datoBeregnet;
        this.kodeFaggruppe = kodeFaggruppe;
        this.belop = belop;
        this.beregningsPeriode = beregningsPeriode;
    }
}
