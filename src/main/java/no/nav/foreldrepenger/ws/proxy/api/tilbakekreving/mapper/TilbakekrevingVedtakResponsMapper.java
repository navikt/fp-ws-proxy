package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.mapper;

import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.ws.proxy.util.DateUtil.convertToLocalDate;

import java.util.List;

import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.iverksett.TilbakekrevingVedtakDTO;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.iverksett.TilbakekrevingsbelopDTO;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.iverksett.TilbakekrevingsperiodeDTO;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.kravgrunnlag.respons.Periode;
import no.nav.okonomi.tilbakekrevingservice.TilbakekrevingsvedtakResponse;
import no.nav.tilbakekreving.tilbakekrevingsvedtak.vedtak.v1.TilbakekrevingsbelopDto;
import no.nav.tilbakekreving.tilbakekrevingsvedtak.vedtak.v1.TilbakekrevingsperiodeDto;
import no.nav.tilbakekreving.typer.v1.PeriodeDto;

public class TilbakekrevingVedtakResponsMapper {

    private TilbakekrevingVedtakResponsMapper() {

    }


    public static TilbakekrevingVedtakDTO tilDto(TilbakekrevingsvedtakResponse respons) {
        var tilbakekrevingVedtakXML = respons.getTilbakekrevingsvedtak();
        return new TilbakekrevingVedtakDTO.Builder()
            .kodeAksjon(tilbakekrevingVedtakXML.getKodeAksjon())
            .vedtakId(tilbakekrevingVedtakXML.getVedtakId().longValue())
            .datoVedtakFagsystem(convertToLocalDate(tilbakekrevingVedtakXML.getDatoVedtakFagsystem()))
            .kodeHjemmel(tilbakekrevingVedtakXML.getKodeHjemmel())
            .renterBeregnes(tilbakekrevingVedtakXML.getRenterBeregnes())
            .enhetAnsvarlig(tilbakekrevingVedtakXML.getEnhetAnsvarlig())
            .kontrollfelt(tilbakekrevingVedtakXML.getKontrollfelt())
            .saksbehId(tilbakekrevingVedtakXML.getSaksbehId())
            .tilbakekrevingsperiode(tilTilbakekrevingsperiode(tilbakekrevingVedtakXML.getTilbakekrevingsperiode()))
            .build();
    }

    private static List<TilbakekrevingsperiodeDTO> tilTilbakekrevingsperiode(List<TilbakekrevingsperiodeDto> tilbakekrevingsperiode) {
        return safeStream(tilbakekrevingsperiode)
            .map(TilbakekrevingVedtakResponsMapper::tilTilbakekrevingsperiodeDTO)
            .toList();
    }

    private static TilbakekrevingsperiodeDTO tilTilbakekrevingsperiodeDTO(TilbakekrevingsperiodeDto tilbakekrevingsperiodeDtoXML) {
        return new TilbakekrevingsperiodeDTO.Builder()
            .periode(tilPeriode(tilbakekrevingsperiodeDtoXML.getPeriode()))
            .renterBeregnes(tilbakekrevingsperiodeDtoXML.getRenterBeregnes())
            .belopRenter(tilbakekrevingsperiodeDtoXML.getBelopRenter())
            .tilbakekrevingsbelop(tilTilbakekrevingsbelopDTO(tilbakekrevingsperiodeDtoXML.getTilbakekrevingsbelop()))
            .build();
    }

    private static List<TilbakekrevingsbelopDTO> tilTilbakekrevingsbelopDTO(List<TilbakekrevingsbelopDto> tilbakekrevingsbelop) {
        return safeStream(tilbakekrevingsbelop)
            .map(TilbakekrevingVedtakResponsMapper::tilTilbakekrevingsbelopDTO)
            .toList();
    }

    private static TilbakekrevingsbelopDTO tilTilbakekrevingsbelopDTO(TilbakekrevingsbelopDto tilbakekrevingsbelopXML) {
        return new TilbakekrevingsbelopDTO.Builder()
            .kodeKlasse(tilbakekrevingsbelopXML.getKodeKlasse())
            .belopOpprUtbet(tilbakekrevingsbelopXML.getBelopOpprUtbet())
            .belopNy(tilbakekrevingsbelopXML.getBelopNy())
            .belopTilbakekreves(tilbakekrevingsbelopXML.getBelopTilbakekreves())
            .belopUinnkrevd(tilbakekrevingsbelopXML.getBelopUinnkrevd())
            .belopSkatt(tilbakekrevingsbelopXML.getBelopSkatt())
            .kodeResultat(tilbakekrevingsbelopXML.getKodeResultat())
            .kodeAarsak(tilbakekrevingsbelopXML.getKodeAarsak())
            .kodeSkyld(tilbakekrevingsbelopXML.getKodeSkyld())
            .build();
    }


    private static Periode tilPeriode(PeriodeDto periode) {
        return new Periode(convertToLocalDate(periode.getFom()), convertToLocalDate(periode.getTom()));
    }
}
