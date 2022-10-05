package no.nav.foreldrepenger.ws.proxy.api.arena.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;

@Builder
public record MeldekortUtbetalingsgrunnlagMeldekort(BigDecimal beløp,
                                                    BigDecimal dagsats,
                                                    LocalDate meldekortFom,
                                                    LocalDate meldekortTom,
                                                    BigDecimal utbetalingsgrad) {

}
