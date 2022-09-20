package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.dto.Kravgrunnlag431Dto;
import no.nav.tilbakekreving.kravgrunnlag.detalj.v1.DetaljertKravgrunnlagDto;


public class HentKravgrunnlagMapper {

    private static final Logger LOG = LoggerFactory.getLogger(HentKravgrunnlagMapper.class);


    public static Kravgrunnlag431Dto mapTilDto(DetaljertKravgrunnlagDto dto) {
//        Kravgrunnlag431Dto kravgrunnlag431 = formKravgrunnlag431(dto);
//        LOG.info("Referanse etter mapping: {}", kravgrunnlag431.getReferanse());
//        for (DetaljertKravgrunnlagPeriodeDto periodeDto : dto.getTilbakekrevingsPeriode()) {
//            KravgrunnlagPeriode432 kravgrunnlagPeriode432 = formKravgrunnlagPeriode432(kravgrunnlag431, periodeDto);
//            for (DetaljertKravgrunnlagBelopDto postering : periodeDto.getTilbakekrevingsBelop()) {
//                KravgrunnlagBelop433 kravgrunnlagBelop433 = formKravgrunnlagBelop433(kravgrunnlagPeriode432, postering);
//                if (!erPosteringenPostitivYtel(kravgrunnlagBelop433)) {
//                    kravgrunnlagPeriode432.leggTilBeløp(kravgrunnlagBelop433);
//                }
//            }
//            kravgrunnlag431.leggTilPeriode(kravgrunnlagPeriode432);
//        }
        return null;
    }

//    private static Kravgrunnlag431Dto formKravgrunnlag431(DetaljertKravgrunnlagDto dto) {
//        GjelderType gjelderType = GjelderType.fraKode(dto.getTypeGjelderId().value());
//        GjelderType utbetalingGjelderType = GjelderType.fraKode(dto.getTypeUtbetId().value());
//        return Kravgrunnlag431.builder().medVedtakId(dto.getVedtakId().longValue())
//                .medKravStatusKode(KravStatusKode.fraKode(trimTrailingSpaces(dto.getKodeStatusKrav())))
//                .medFagomraadeKode(FagOmrådeKode.fraKode(trimTrailingSpaces(dto.getKodeFagomraade().trim())))
//                .medFagSystemId(trimTrailingSpaces(dto.getFagsystemId()))
//                .medVedtakFagSystemDato(konverter(dto.getDatoVedtakFagsystem()))
//                .medOmgjortVedtakId(dto.getVedtakIdOmgjort() != null ? dto.getVedtakIdOmgjort().longValue() : null)
////                .medGjelderVedtakId(hentAktoerId(gjelderType, dto.getVedtakGjelderId())) // TODO: Må gjøres av fptilbake!
//                .medGjelderType(gjelderType)
////                .medUtbetalesTilId(hentAktoerId(utbetalingGjelderType, dto.getUtbetalesTilId())) // TODO: Må gjøres av fptilbake!
//                .medUtbetIdType(utbetalingGjelderType)
//                .medHjemmelKode(dto.getKodeHjemmel())
//                .medBeregnesRenter(dto.getRenterBeregnes() != null ? dto.getRenterBeregnes().value() : null)
//                .medAnsvarligEnhet(trimTrailingSpaces(dto.getEnhetAnsvarlig()))
//                .medBostedEnhet(trimTrailingSpaces(dto.getEnhetBosted()))
//                .medBehandlendeEnhet(trimTrailingSpaces(dto.getEnhetBehandl()))
//                .medFeltKontroll(dto.getKontrollfelt())
//                .medSaksBehId(trimTrailingSpaces(dto.getSaksbehId()))
//                .medReferanse(new Henvisning(dto.getReferanse()))
//                .medEksternKravgrunnlagId(String.valueOf(dto.getKravgrunnlagId()))
//                .build();
//    }
//
//
//
//    private static KravgrunnlagPeriode432 formKravgrunnlagPeriode432(Kravgrunnlag431 kravgrunnlag431, DetaljertKravgrunnlagPeriodeDto dto) {
//        LocalDate fom = konverter(dto.getPeriode().getFom());
//        LocalDate tom = konverter(dto.getPeriode().getTom());
//        return KravgrunnlagPeriode432.builder()
//                .medPeriode(Periode.of(fom, tom))
//                .medBeløpSkattMnd(dto.getBelopSkattMnd())
//                .medKravgrunnlag431(kravgrunnlag431)
//                .build();
//    }
//
//    private static KravgrunnlagBelop433 formKravgrunnlagBelop433(KravgrunnlagPeriode432 kravgrunnlagPeriode432, DetaljertKravgrunnlagBelopDto dto) {
//        KlasseType type = map(dto.getTypeKlasse());
//        return KravgrunnlagBelop433.builder()
//                .medKlasseType(type)
//                .medKlasseKode(finnKlasseKode(dto.getKodeKlasse(), type))
//                .medOpprUtbetBelop(dto.getBelopOpprUtbet())
//                .medNyBelop(dto.getBelopNy())
//                .medTilbakekrevesBelop(dto.getBelopTilbakekreves())
//                .medUinnkrevdBelop(dto.getBelopUinnkrevd())
//                .medSkattProsent(dto.getSkattProsent())
//                .medResultatKode(dto.getKodeResultat())
//                .medÅrsakKode(dto.getKodeAArsak())
//                .medSkyldKode(dto.getKodeSkyld())
//                .medKravgrunnlagPeriode432(kravgrunnlagPeriode432)
//                .build();
//    }
//
//    private static LocalDate konverter(XMLGregorianCalendar dato) {
//        return DateUtil.convertToLocalDate(dato);
//    }
//
//    private static KlasseType map(TypeKlasseDto typeKlasse) {
//        return switch (typeKlasse) {
//            case FEIL -> KlasseType.FEIL;
//            case JUST -> KlasseType.JUST;
//            case SKAT -> KlasseType.SKAT;
//            case TREK -> KlasseType.TREK;
//            case YTEL -> KlasseType.YTEL;
//            default -> throw new IllegalArgumentException("Ukjent klassetype: " + typeKlasse);
//        };
//    }
//
//    private static String finnKlasseKode(String klasseKode, KlasseType klasseType) {
//        if (KlasseType.TREK.equals(klasseType) || KlasseType.SKAT.equals(klasseType)) {
//            return klasseKode;
//        }
//        return KlasseKode.fraKode(klasseKode).getKode();
//    }
//
//    private static String trimTrailingSpaces(String field) {
//        return field.trim();
//    }
//
//    private static boolean erPosteringenPostitivYtel(KravgrunnlagBelop433 belop433) {
//        return belop433.getKlasseType().equals(KlasseType.YTEL) && belop433.getNyBelop().compareTo(belop433.getOpprUtbetBelop()) > 0;
//    }

}
