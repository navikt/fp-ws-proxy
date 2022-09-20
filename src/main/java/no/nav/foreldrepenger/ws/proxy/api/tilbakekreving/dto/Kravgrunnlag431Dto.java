package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record Kravgrunnlag431Dto(Long id,
                                 String eksternKravgrunnlagId,
                                 Long vedtakId,
                                 KravStatusKode kravStatusKode,
                                 FagOmrådeKode fagOmrådeKode,
                                 String fagSystemId,
                                 LocalDate vedtakFagSystemDato,
                                 Long omgjortVedtakId,
                                 String gjelderVedtakId,
                                 GjelderType gjelderType,
                                 String utbetalesTilId,
                                 GjelderType utbetGjelderType,
                                 String hjemmelKode,
                                 String beregnesRenter,
                                 String ansvarligEnhet,
                                 String bostedEnhet,
                                 String behandlendeEnhet,
                                 String kontrollFelt,
                                 String saksBehId,
                                 Henvisning referanse,
                                 String opprettetAv,
                                 LocalDateTime opprettetTidspunkt,
                                 String endretAv,
                                 LocalDateTime endretTidspunkt) {

}
