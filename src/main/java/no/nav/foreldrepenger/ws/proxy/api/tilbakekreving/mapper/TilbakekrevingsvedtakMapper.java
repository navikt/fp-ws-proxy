package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.mapper;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.kontrakter.tilbakekreving.kravgrunnlag.request.KodeAksjon;
import no.nav.foreldrepenger.kontrakter.tilbakekreving.kravgrunnlag.respons.KlasseType;
import no.nav.foreldrepenger.kontrakter.tilbakekreving.kravgrunnlag.respons.Kravgrunnlag431Dto;
import no.nav.foreldrepenger.kontrakter.tilbakekreving.kravgrunnlag.respons.Periode;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.dto.TilbakekrevingBeløp;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.dto.TilbakekrevingPeriodeDto;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.dto.TilbakekrevingVedtakDto;
import no.nav.foreldrepenger.ws.proxy.util.DateUtil;
import no.nav.tilbakekreving.tilbakekrevingsvedtak.vedtak.v1.TilbakekrevingsbelopDto;
import no.nav.tilbakekreving.tilbakekrevingsvedtak.vedtak.v1.TilbakekrevingsperiodeDto;
import no.nav.tilbakekreving.tilbakekrevingsvedtak.vedtak.v1.TilbakekrevingsvedtakDto;
import no.nav.tilbakekreving.typer.v1.PeriodeDto;

public class TilbakekrevingsvedtakMapper {

    private TilbakekrevingsvedtakMapper() {
        //hindrer instansiering
    }


    public static TilbakekrevingsvedtakDto tilDto(TilbakekrevingVedtakDto tilbakekrevingDto) {
        return tilDto(tilbakekrevingDto.kravgrunnlag(), tilbakekrevingDto.tilbakekrevingPerioder(), tilbakekrevingDto.saksbehandlerid());

    }

    private static TilbakekrevingsvedtakDto tilDto(Kravgrunnlag431Dto kravgrunnlag, List<TilbakekrevingPeriodeDto> tilbakekrevingPerioder, String saksbehandlerid) {
        TilbakekrevingsvedtakDto tilbakekrevingsvedtak = TilbakekrevingsvedtakMapper.tilDto(kravgrunnlag, saksbehandlerid);
        for (TilbakekrevingPeriodeDto tilbakekrevingPeriode : tilbakekrevingPerioder) {
            tilbakekrevingsvedtak.getTilbakekrevingsperiode().add(TilbakekrevingsvedtakMapper.tilDto(tilbakekrevingPeriode));
        }
        return tilbakekrevingsvedtak;
    }

    private static TilbakekrevingsvedtakDto tilDto(Kravgrunnlag431Dto kravgrunnlag, String saksbehandlerid) {
        TilbakekrevingsvedtakDto tilbakekrevingsvedtak = new TilbakekrevingsvedtakDto();
        tilbakekrevingsvedtak.setKodeAksjon(KodeAksjon.FATTE_VEDTAK.getKode()); // fast verdi, Fatte Vedtak(8)
        tilbakekrevingsvedtak.setVedtakId(BigInteger.valueOf(kravgrunnlag.vedtakId()));
        LocalDate vedtakFagsystemDato = kravgrunnlag.vedtakFagSystemDato();
        if (vedtakFagsystemDato == null) {
            vedtakFagsystemDato = LocalDate.now();
        }
        tilbakekrevingsvedtak.setDatoVedtakFagsystem(DateUtil.convertToXMLGregorianCalendar(vedtakFagsystemDato));
        tilbakekrevingsvedtak.setKodeHjemmel("22-15"); // fast verdi
        tilbakekrevingsvedtak.setEnhetAnsvarlig(kravgrunnlag.ansvarligEnhet());
        tilbakekrevingsvedtak.setKontrollfelt(kravgrunnlag.kontrollFelt());
        tilbakekrevingsvedtak.setSaksbehId(saksbehandlerid);
        return tilbakekrevingsvedtak;
    }

    private static TilbakekrevingsperiodeDto tilDto(TilbakekrevingPeriodeDto tilbakekrevingPeriode) {
        TilbakekrevingsperiodeDto dto = new TilbakekrevingsperiodeDto();
        PeriodeDto periodeDto = lagPeriodeDto(tilbakekrevingPeriode.periode());
        dto.setPeriode(periodeDto);
        dto.setBelopRenter(tilbakekrevingPeriode.renter());
        tilbakekrevingPeriode.beløp().forEach(
                b -> dto.getTilbakekrevingsbelop().add(lagDto(b)));
        return dto;
    }

    private static TilbakekrevingsbelopDto lagDto(TilbakekrevingBeløp b) {
        TilbakekrevingsbelopDto dto = new TilbakekrevingsbelopDto();
        dto.setKodeKlasse(b.klassekode());
        dto.setBelopTilbakekreves(b.tilbakekrevBeløp());
        dto.setBelopUinnkrevd(b.uinnkrevdBeløp());
        dto.setBelopOpprUtbet(b.utbetaltBeløp());
        dto.setBelopNy(b.nyttBeløp());
        dto.setBelopSkatt(b.skattBeløp());
        if (KlasseType.YTEL.equals(b.klasseType())) {
            dto.setKodeResultat(b.kodeResultat().getKode());
            dto.setKodeAarsak("ANNET"); // fast verdi
            dto.setKodeSkyld("IKKE_FORDELT"); // fast verdi
        }
        //FIXME setKlasseType mangler
        return dto;
    }

    private static PeriodeDto lagPeriodeDto(Periode periode) {
        PeriodeDto periodeDto = new PeriodeDto();
        periodeDto.setFom(DateUtil.convertToXMLGregorianCalendar(periode.fom()));
        periodeDto.setTom(DateUtil.convertToXMLGregorianCalendar(periode.tom()));
        return periodeDto;
    }

}
