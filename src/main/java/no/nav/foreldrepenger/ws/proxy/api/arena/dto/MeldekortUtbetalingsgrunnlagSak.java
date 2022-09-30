package no.nav.foreldrepenger.ws.proxy.api.arena.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import no.nav.foreldrepenger.common.domain.Saksnummer;

@Builder
public record MeldekortUtbetalingsgrunnlagSak(List<MeldekortUtbetalingsgrunnlagMeldekort> meldekortene,
                                              YtelseType type,
                                              YtelseStatus tilstand,
                                              Fagsystem kilde,
                                              Saksnummer saksnummer,
                                              String sakStatus,
                                              String vedtakStatus,
                                              LocalDate kravMottattDato,
                                              LocalDate vedtattDato,
                                              LocalDate vedtaksPeriodeFom,
                                              LocalDate vedtaksPeriodeTom,
                                              Beløp vedtaksDagsats) {
}
