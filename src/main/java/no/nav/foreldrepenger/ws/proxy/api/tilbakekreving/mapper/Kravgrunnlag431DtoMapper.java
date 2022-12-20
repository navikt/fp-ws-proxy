package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.mapper;

import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;

import java.time.LocalDate;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.kravgrunnlag.respons.FagOmrådeKode;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.kravgrunnlag.respons.GjelderType;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.kravgrunnlag.respons.KlasseType;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.kravgrunnlag.respons.KravStatusKode;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.kravgrunnlag.respons.Kravgrunnlag431Dto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.kravgrunnlag.respons.KravgrunnlagBelop433Dto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.kravgrunnlag.respons.KravgrunnlagPeriode432Dto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.kravgrunnlag.respons.Periode;
import no.nav.foreldrepenger.ws.proxy.util.DateUtil;
import no.nav.tilbakekreving.kravgrunnlag.detalj.v1.DetaljertKravgrunnlagBelopDto;
import no.nav.tilbakekreving.kravgrunnlag.detalj.v1.DetaljertKravgrunnlagDto;
import no.nav.tilbakekreving.kravgrunnlag.detalj.v1.DetaljertKravgrunnlagPeriodeDto;
import no.nav.tilbakekreving.typer.v1.TypeKlasseDto;

public class Kravgrunnlag431DtoMapper {

    private Kravgrunnlag431DtoMapper() {
    }

    public static Kravgrunnlag431Dto tilDto(DetaljertKravgrunnlagDto dto) {
        return formKravgrunnlag431(dto);
    }

    private static Kravgrunnlag431Dto formKravgrunnlag431(DetaljertKravgrunnlagDto dto) {
        var gjelderType = GjelderType.valueOf(dto.getTypeGjelderId().value());
        var utbetalingGjelderType = GjelderType.valueOf(dto.getTypeUtbetId().value());
        return new Kravgrunnlag431Dto.Builder()
                .vedtakId(dto.getVedtakId().longValue())
                .kravStatusKode(KravStatusKode.valueOf(trimTrailingSpaces(dto.getKodeStatusKrav())))
                .fagOmrådeKode(FagOmrådeKode.valueOf(trimTrailingSpaces(dto.getKodeFagomraade().trim())))
                .fagSystemId(trimTrailingSpaces(dto.getFagsystemId()))
                .vedtakFagSystemDato(konverter(dto.getDatoVedtakFagsystem()))
                .omgjortVedtakId(dto.getVedtakIdOmgjort() != null ? dto.getVedtakIdOmgjort().longValue() : null)
                .gjelderVedtakId(dto.getVedtakGjelderId())
                .gjelderType(gjelderType)
                .utbetalesTilId(dto.getUtbetalesTilId())
                .utbetGjelderType(utbetalingGjelderType)
                .hjemmelKode(dto.getKodeHjemmel())
                .beregnesRenter(dto.getRenterBeregnes() != null ? dto.getRenterBeregnes().value() : null)
                .ansvarligEnhet(trimTrailingSpaces(dto.getEnhetAnsvarlig()))
                .bostedEnhet(trimTrailingSpaces(dto.getEnhetBosted()))
                .behandlendeEnhet(trimTrailingSpaces(dto.getEnhetBehandl()))
                .kontrollFelt(dto.getKontrollfelt())
                .saksBehId(trimTrailingSpaces(dto.getSaksbehId()))
                .referanse(dto.getReferanse())
                .eksternKravgrunnlagId(String.valueOf(dto.getKravgrunnlagId()))
                .perioder(formKravgrunnlagPeriode432Liste(dto))
                .build();
    }

    private static List<KravgrunnlagPeriode432Dto> formKravgrunnlagPeriode432Liste(DetaljertKravgrunnlagDto detaljertKravgrunnlagDto) {
        return safeStream(detaljertKravgrunnlagDto.getTilbakekrevingsPeriode())
            .map(Kravgrunnlag431DtoMapper::formKravgrunnlagPeriode432)
            .toList();
    }

    private static KravgrunnlagPeriode432Dto formKravgrunnlagPeriode432(DetaljertKravgrunnlagPeriodeDto detaljertKravgrunnlagPeriodeDto) {
        var fom = konverter(detaljertKravgrunnlagPeriodeDto.getPeriode().getFom());
        var tom = konverter(detaljertKravgrunnlagPeriodeDto.getPeriode().getTom());
        return new KravgrunnlagPeriode432Dto.Builder()
                .periode(new Periode(fom, tom))
                .beløpSkattMnd(detaljertKravgrunnlagPeriodeDto.getBelopSkattMnd())
                .kravgrunnlagBeloper433(formKravgrunnlagBelop433Liste(detaljertKravgrunnlagPeriodeDto))
                .build();
    }

    private static List<KravgrunnlagBelop433Dto> formKravgrunnlagBelop433Liste(DetaljertKravgrunnlagPeriodeDto detaljertKravgrunnlagPeriodeDto) {
        return safeStream(detaljertKravgrunnlagPeriodeDto.getTilbakekrevingsBelop())
            .map(Kravgrunnlag431DtoMapper::formKravgrunnlagBelop433)
            .toList();
    }

    private static KravgrunnlagBelop433Dto formKravgrunnlagBelop433(DetaljertKravgrunnlagBelopDto dto) {
        return new KravgrunnlagBelop433Dto.Builder()
                .klasseType(map(dto.getTypeKlasse()))
                .klasseKode(dto.getKodeKlasse())
                .opprUtbetBelop(dto.getBelopOpprUtbet())
                .nyBelop(dto.getBelopNy())
                .tilbakekrevesBelop(dto.getBelopTilbakekreves())
                .uinnkrevdBelop(dto.getBelopUinnkrevd())
                .skattProsent(dto.getSkattProsent())
                .resultatKode(dto.getKodeResultat())
                .årsakKode(dto.getKodeAArsak())
                .skyldKode(dto.getKodeSkyld())
                .build();
    }


    private static KlasseType map(TypeKlasseDto typeKlasse) {
        return switch (typeKlasse) {
            case FEIL -> KlasseType.FEIL;
            case JUST -> KlasseType.JUST;
            case SKAT -> KlasseType.SKAT;
            case TREK -> KlasseType.TREK;
            case YTEL -> KlasseType.YTEL;
        };
    }

    private static LocalDate konverter(XMLGregorianCalendar dato) {
        return DateUtil.convertToLocalDate(dato);
    }

    private static String trimTrailingSpaces(String field) {
        return field.trim();
    }
}
