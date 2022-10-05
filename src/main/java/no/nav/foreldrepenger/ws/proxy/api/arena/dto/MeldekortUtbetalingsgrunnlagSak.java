package no.nav.foreldrepenger.ws.proxy.api.arena.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;

@Builder
public record MeldekortUtbetalingsgrunnlagSak(Fagsystem kilde,
                                              LocalDate kravMottattDato,
                                              List<MeldekortUtbetalingsgrunnlagMeldekort> meldekortene,
                                              String sakStatus,
                                              String saksnummer,
                                              YtelseStatus tilstand,
                                              YtelseType type,
                                              String vedtakStatus,
                                              Beløp vedtaksDagsats,
                                              LocalDate vedtaksPeriodeFom,
                                              LocalDate vedtaksPeriodeTom,
                                              LocalDate vedtattDato) {
}
