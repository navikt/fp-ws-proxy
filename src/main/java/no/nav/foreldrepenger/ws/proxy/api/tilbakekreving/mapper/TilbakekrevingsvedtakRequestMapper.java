package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.mapper;

import java.math.BigInteger;
import java.time.LocalDate;

import javax.xml.datatype.XMLGregorianCalendar;

import no.nav.foreldrepenger.kontrakter.tilbakekreving.iverksett.TilbakekrevingVedtakDTO;
import no.nav.foreldrepenger.kontrakter.tilbakekreving.iverksett.TilbakekrevingsbelopDTO;
import no.nav.foreldrepenger.kontrakter.tilbakekreving.iverksett.TilbakekrevingsperiodeDTO;
import no.nav.foreldrepenger.kontrakter.tilbakekreving.kravgrunnlag.respons.Periode;
import no.nav.foreldrepenger.ws.proxy.util.DateUtil;
import no.nav.okonomi.tilbakekrevingservice.TilbakekrevingsvedtakRequest;
import no.nav.tilbakekreving.tilbakekrevingsvedtak.vedtak.v1.TilbakekrevingsbelopDto;
import no.nav.tilbakekreving.tilbakekrevingsvedtak.vedtak.v1.TilbakekrevingsperiodeDto;
import no.nav.tilbakekreving.tilbakekrevingsvedtak.vedtak.v1.TilbakekrevingsvedtakDto;
import no.nav.tilbakekreving.typer.v1.PeriodeDto;

public class TilbakekrevingsvedtakRequestMapper {

    private TilbakekrevingsvedtakRequestMapper() {
        //hindrer instansiering
    }

    public static TilbakekrevingsvedtakRequest tilTilbakekrevingsvedtakRequest(TilbakekrevingVedtakDTO tilbakekrevingVedtakDto) {
        var request = new TilbakekrevingsvedtakRequest();
        request.setTilbakekrevingsvedtak(tilTilbakekrevingsvedtakDto(tilbakekrevingVedtakDto));
        return request;
    }

    private static TilbakekrevingsvedtakDto tilTilbakekrevingsvedtakDto(TilbakekrevingVedtakDTO tilbakekrevingVedtakDto) {
        var tilbakekrevingsvedtakXML = new TilbakekrevingsvedtakDto();
        tilbakekrevingsvedtakXML.setKodeAksjon(tilbakekrevingVedtakDto.kodeAksjon());
        tilbakekrevingsvedtakXML.setVedtakId(BigInteger.valueOf(tilbakekrevingVedtakDto.vedtakId()));
        tilbakekrevingsvedtakXML.setDatoVedtakFagsystem(tilVedtakFagsystemDato(tilbakekrevingVedtakDto.datoVedtakFagsystem()));
        tilbakekrevingsvedtakXML.setKodeHjemmel(tilbakekrevingVedtakDto.kodeHjemmel());
        tilbakekrevingsvedtakXML.setEnhetAnsvarlig(tilbakekrevingVedtakDto.enhetAnsvarlig());
        tilbakekrevingsvedtakXML.setKontrollfelt(tilbakekrevingVedtakDto.kontrollfelt());
        tilbakekrevingsvedtakXML.setSaksbehId(tilbakekrevingVedtakDto.saksbehId());
        for (var tilbakekrevingPeriode : tilbakekrevingVedtakDto.tilbakekrevingsperiode()) {
            tilbakekrevingsvedtakXML.getTilbakekrevingsperiode().add(tilTilbakekrevingsperiodeDto(tilbakekrevingPeriode));
        }
        return tilbakekrevingsvedtakXML;
    }

    private static XMLGregorianCalendar tilVedtakFagsystemDato(LocalDate datoVedtakFagsystem) {
        if (datoVedtakFagsystem == null) {
            return DateUtil.convertToXMLGregorianCalendar(LocalDate.now());
        }
        return DateUtil.convertToXMLGregorianCalendar(datoVedtakFagsystem);
    }

    private static TilbakekrevingsperiodeDto tilTilbakekrevingsperiodeDto(TilbakekrevingsperiodeDTO tilbakekrevingPeriode) {
        var dto = new TilbakekrevingsperiodeDto();
        dto.setPeriode(lagPeriodeDto(tilbakekrevingPeriode.periode()));
        dto.setBelopRenter(tilbakekrevingPeriode.belopRenter());
        tilbakekrevingPeriode.tilbakekrevingsbelop().forEach(b -> dto.getTilbakekrevingsbelop().add(tilTilbakekrevingsbelopDto(b)));
        return dto;
    }

    private static TilbakekrevingsbelopDto tilTilbakekrevingsbelopDto(TilbakekrevingsbelopDTO b) {
        var dto = new TilbakekrevingsbelopDto();
        dto.setKodeKlasse(b.kodeKlasse());
        dto.setBelopTilbakekreves(b.belopTilbakekreves());
        dto.setBelopUinnkrevd(b.belopUinnkrevd());
        dto.setBelopOpprUtbet(b.belopOpprUtbet());
        dto.setBelopNy(b.belopNy());
        dto.setBelopSkatt(b.belopSkatt());
        dto.setKodeResultat(b.kodeResultat());
        dto.setKodeAarsak(b.kodeAarsak());
        dto.setKodeSkyld(b.kodeSkyld());
        return dto;
    }

    private static PeriodeDto lagPeriodeDto(Periode periode) {
        var periodeDto = new PeriodeDto();
        periodeDto.setFom(DateUtil.convertToXMLGregorianCalendar(periode.fom()));
        periodeDto.setTom(DateUtil.convertToXMLGregorianCalendar(periode.tom()));
        return periodeDto;
    }

}
