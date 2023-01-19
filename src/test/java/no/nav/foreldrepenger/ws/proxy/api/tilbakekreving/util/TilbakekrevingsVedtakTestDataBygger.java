package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.util;

import static no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.mapper.TilbakekrevingsvedtakRequestMapper.tilTilbakekrevingsvedtakRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.iverksett.TilbakekrevingVedtakDTO;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.iverksett.TilbakekrevingsbelopDTO;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.iverksett.TilbakekrevingsperiodeDTO;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.kravgrunnlag.respons.Periode;
import no.nav.okonomi.tilbakekrevingservice.TilbakekrevingsvedtakResponse;
import no.nav.tilbakekreving.typer.v1.MmelDto;

public class TilbakekrevingsVedtakTestDataBygger {

    public static TilbakekrevingsvedtakResponse lagTilbakekrevingsvedtakResponsXMLFraRequest(TilbakekrevingVedtakDTO orginalTilbakekrevingVedtakDto, MmelDto kvittering) {
        var tilbakekrevingsvedtakResponse = new TilbakekrevingsvedtakResponse();
        var tilbakekrevingsvedtakRequest = tilTilbakekrevingsvedtakRequest(orginalTilbakekrevingVedtakDto);
        tilbakekrevingsvedtakResponse.setTilbakekrevingsvedtak(tilbakekrevingsvedtakRequest.getTilbakekrevingsvedtak());
        tilbakekrevingsvedtakResponse.setMmel(kvittering);
        return tilbakekrevingsvedtakResponse;
    }

    public static TilbakekrevingVedtakDTO lagTilbakekrevingVedtakDTORequest(List<TilbakekrevingsperiodeDTO> tilbakekrevingsperider) {
        return new TilbakekrevingVedtakDTO.Builder()
            .kodeAksjon("8")
            .vedtakId(10000000L)
            .datoVedtakFagsystem(LocalDate.now())
            .kodeHjemmel("22-15")
            .enhetAnsvarlig("8042")
            .kontrollfelt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss.SSSSSS")))
            .saksbehId("W123456")
            .tilbakekrevingsperiode(tilbakekrevingsperider)
            .build();
    }

    public static TilbakekrevingsperiodeDTO lagTilbakekrevingsperiodeDTO(List<TilbakekrevingsbelopDTO> tilbakekrevingsbelop) {
        return new TilbakekrevingsperiodeDTO.Builder()
            .periode(new Periode(LocalDate.now().minusMonths(4), LocalDate.now()))
            .belopRenter(BigDecimal.TEN)
            .tilbakekrevingsbelop(tilbakekrevingsbelop)
            .build();
    }

    public static TilbakekrevingsbelopDTO lagTilbakekrevingsbelop() {
        return new TilbakekrevingsbelopDTO.Builder()
            .kodeKlasse("kodeklasse")
            .belopOpprUtbet(BigDecimal.valueOf(1))
            .belopNy(BigDecimal.valueOf(2))
            .belopTilbakekreves(BigDecimal.ZERO)
            .belopUinnkrevd(BigDecimal.TEN)
            .belopSkatt(BigDecimal.valueOf(5))
            .kodeResultat("FEILREGISTRERT")
            .kodeAarsak("ANNET")
            .kodeSkyld("IKKE_FORDELT")
            .build();
    }
}
