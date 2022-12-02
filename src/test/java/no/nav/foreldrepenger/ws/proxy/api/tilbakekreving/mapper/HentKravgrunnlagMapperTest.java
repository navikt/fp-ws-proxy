package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import no.nav.foreldrepenger.kontrakter.tilbakekreving.kravgrunnlag.respons.GjelderType;
import no.nav.foreldrepenger.kontrakter.tilbakekreving.kravgrunnlag.respons.Kravgrunnlag431Dto;
import no.nav.foreldrepenger.ws.proxy.util.DateUtil;
import no.nav.tilbakekreving.kravgrunnlag.detalj.v1.DetaljertKravgrunnlagBelopDto;
import no.nav.tilbakekreving.kravgrunnlag.detalj.v1.DetaljertKravgrunnlagDto;
import no.nav.tilbakekreving.kravgrunnlag.detalj.v1.DetaljertKravgrunnlagPeriodeDto;
import no.nav.tilbakekreving.typer.v1.JaNeiDto;
import no.nav.tilbakekreving.typer.v1.PeriodeDto;
import no.nav.tilbakekreving.typer.v1.TypeGjelderDto;
import no.nav.tilbakekreving.typer.v1.TypeKlasseDto;

class HentKravgrunnlagMapperTest {

    private static final String ENHET = "8020";


    @Test
    void skal_mapTilDomene_fraHentgrunnlagrespons() {
        var detaljertKravgrunnlagDto = hentGrunnlag();
        var kravgrunnlag431 = HentKravgrunnlagMapper.mapTilDto(detaljertKravgrunnlagDto);
        verifiserAtMappingIkkeMisterNoeData(kravgrunnlag431, detaljertKravgrunnlagDto);
    }

    @Test
    void skal_sende_ned_alle_posteringer_selv_om_positiv_ytelse() {
        var feilPostering = hentBeløp(BigDecimal.valueOf(1794), BigDecimal.ZERO, BigDecimal.ZERO, TypeKlasseDto.FEIL);
        var ytelPostering = hentBeløp(BigDecimal.ZERO, BigDecimal.valueOf(1794), BigDecimal.valueOf(3270), TypeKlasseDto.YTEL);
        var positivYtelPostering = hentBeløp(BigDecimal.valueOf(3930), BigDecimal.ZERO, BigDecimal.valueOf(2454), TypeKlasseDto.YTEL);

        var kravgrunnlagPeriode = new DetaljertKravgrunnlagPeriodeDto();
        kravgrunnlagPeriode.setPeriode(periode(LocalDate.of(2019, 8, 9), LocalDate.of(2019, 8, 16)));
        kravgrunnlagPeriode.setBelopSkattMnd(BigDecimal.valueOf(2104));
        kravgrunnlagPeriode.getTilbakekrevingsBelop().add(feilPostering);
        kravgrunnlagPeriode.getTilbakekrevingsBelop().add(ytelPostering);
        kravgrunnlagPeriode.getTilbakekrevingsBelop().add(positivYtelPostering);

        var detaljertKravgrunnlagDto = hentGrunnlag();
        detaljertKravgrunnlagDto.getTilbakekrevingsPeriode().clear();
        detaljertKravgrunnlagDto.getTilbakekrevingsPeriode().add(kravgrunnlagPeriode);

        // Act
        var kravgrunnlag431 = HentKravgrunnlagMapper.mapTilDto(detaljertKravgrunnlagDto);

        // Verify
        verifiserAtMappingIkkeMisterNoeData(kravgrunnlag431, detaljertKravgrunnlagDto);
    }

    private static PeriodeDto periode(LocalDate fom, LocalDate tom) {
        var periode = new PeriodeDto();
        periode.setFom(konvertDato(fom));
        periode.setTom(konvertDato(tom));
        return periode;
    }

    private static void verifiserAtMappingIkkeMisterNoeData(Kravgrunnlag431Dto kravgrunnlag431Dto, DetaljertKravgrunnlagDto detaljertKravgrunnlagDto) {
        assertThat(kravgrunnlag431Dto.eksternKravgrunnlagId()).isEqualTo(String.valueOf(detaljertKravgrunnlagDto.getKravgrunnlagId()));
        assertThat(kravgrunnlag431Dto.vedtakId()).isEqualTo(detaljertKravgrunnlagDto.getVedtakId().longValue());
        assertThat(kravgrunnlag431Dto.kravStatusKode().name()).isEqualTo(detaljertKravgrunnlagDto.getKodeStatusKrav());
        assertThat(kravgrunnlag431Dto.fagOmrådeKode().name()).isEqualTo(detaljertKravgrunnlagDto.getKodeFagomraade());
        assertThat(kravgrunnlag431Dto.fagSystemId()).isEqualTo(detaljertKravgrunnlagDto.getFagsystemId());
        assertThat(kravgrunnlag431Dto.vedtakFagSystemDato()).isEqualTo(DateUtil.convertToLocalDate(detaljertKravgrunnlagDto.getDatoVedtakFagsystem()));
        assertThat(kravgrunnlag431Dto.omgjortVedtakId()).isEqualTo(detaljertKravgrunnlagDto.getVedtakIdOmgjort().longValue());
        assertThat(kravgrunnlag431Dto.gjelderVedtakId()).isEqualTo(detaljertKravgrunnlagDto.getVedtakGjelderId());
        assertThat(kravgrunnlag431Dto.gjelderType().name())
            .isEqualTo(detaljertKravgrunnlagDto.getTypeGjelderId().name())
            .isEqualTo(GjelderType.PERSON.name());
        assertThat(kravgrunnlag431Dto.hjemmelKode()).isEqualTo(detaljertKravgrunnlagDto.getKodeHjemmel());
        assertThat(kravgrunnlag431Dto.beregnesRenter()).isEqualTo(detaljertKravgrunnlagDto.getRenterBeregnes().value());
        assertThat(kravgrunnlag431Dto.ansvarligEnhet())
            .isEqualTo(detaljertKravgrunnlagDto.getEnhetAnsvarlig())
            .isEqualTo(ENHET);
        assertThat(kravgrunnlag431Dto.beregnesRenter()).isEqualTo(detaljertKravgrunnlagDto.getRenterBeregnes().value());
        assertThat(kravgrunnlag431Dto.bostedEnhet()).isEqualTo(detaljertKravgrunnlagDto.getEnhetBosted());
        assertThat(kravgrunnlag431Dto.behandlendeEnhet()).isEqualTo(detaljertKravgrunnlagDto.getEnhetBehandl());
        assertThat(kravgrunnlag431Dto.kontrollFelt()).isEqualTo(detaljertKravgrunnlagDto.getKontrollfelt());
        assertThat(kravgrunnlag431Dto.saksBehId()).isEqualTo(detaljertKravgrunnlagDto.getSaksbehId());
        assertThat(kravgrunnlag431Dto.referanse()).isEqualTo(detaljertKravgrunnlagDto.getReferanse());

        // Verifiser kravgrunnlagPerioder432
        var kravgrunnlagPerioder432DtoListe = kravgrunnlag431Dto.perioder();
        var tilbakekrevingsPeriodeListe = detaljertKravgrunnlagDto.getTilbakekrevingsPeriode();
        assertThat(kravgrunnlagPerioder432DtoListe)
            .hasSameSizeAs(tilbakekrevingsPeriodeListe)
            .hasSizeGreaterThan(0);
        for (int i = 0; i < tilbakekrevingsPeriodeListe.size(); i++) {
            var kravgrunnlagPeriode432Dto = kravgrunnlagPerioder432DtoListe.get(i);
            var detaljertKravgrunnlagPeriodeDto = tilbakekrevingsPeriodeListe.get(i);
            assertThat(kravgrunnlagPeriode432Dto.periode().fom()).isEqualTo(DateUtil.convertToLocalDate(detaljertKravgrunnlagPeriodeDto.getPeriode().getFom()));
            assertThat(kravgrunnlagPeriode432Dto.periode().tom()).isEqualTo(DateUtil.convertToLocalDate(detaljertKravgrunnlagPeriodeDto.getPeriode().getTom()));
            assertThat(kravgrunnlagPeriode432Dto.beløpSkattMnd()).isEqualTo(detaljertKravgrunnlagPeriodeDto.getBelopSkattMnd()).isNotNull();

            // Verifiser KravgrunnlagBelop433
            var kravgrunnlagBelop433DtoListe = kravgrunnlagPeriode432Dto.kravgrunnlagBeloper433();
            var tilbakekrevingsBelopListe = detaljertKravgrunnlagPeriodeDto.getTilbakekrevingsBelop();
            assertThat(kravgrunnlagBelop433DtoListe)
                .hasSameSizeAs(tilbakekrevingsBelopListe)
                .hasSizeGreaterThan(0);
            for (int j = 0; j < tilbakekrevingsBelopListe.size(); j++) {
                var kravgrunnlagBelop433Dto = kravgrunnlagBelop433DtoListe.get(j);
                var detaljertKravgrunnlagBelopDto = tilbakekrevingsBelopListe.get(j);
                assertThat(kravgrunnlagBelop433Dto.klasseKode()).isEqualTo(detaljertKravgrunnlagBelopDto.getKodeKlasse());
                assertThat(kravgrunnlagBelop433Dto.klasseType().name()).isEqualTo(detaljertKravgrunnlagBelopDto.getTypeKlasse().name());
                assertThat(kravgrunnlagBelop433Dto.opprUtbetBelop())
                    .isEqualTo(detaljertKravgrunnlagBelopDto.getBelopOpprUtbet())
                    .isNotNull();
                assertThat(kravgrunnlagBelop433Dto.nyBelop())
                    .isEqualTo(detaljertKravgrunnlagBelopDto.getBelopNy())
                    .isNotNull();
                assertThat(kravgrunnlagBelop433Dto.tilbakekrevesBelop())
                    .isEqualTo(detaljertKravgrunnlagBelopDto.getBelopTilbakekreves())
                    .isNotNull();
                assertThat(kravgrunnlagBelop433Dto.uinnkrevdBelop())
                    .isEqualTo(detaljertKravgrunnlagBelopDto.getBelopUinnkrevd())
                    .isNotNull();
                assertThat(kravgrunnlagBelop433Dto.skattProsent())
                    .isEqualTo(detaljertKravgrunnlagBelopDto.getSkattProsent())
                    .isNotNull();
                assertThat(kravgrunnlagBelop433Dto.resultatKode()).isEqualTo(detaljertKravgrunnlagBelopDto.getKodeResultat());
                assertThat(kravgrunnlagBelop433Dto.årsakKode()).isEqualTo(detaljertKravgrunnlagBelopDto.getKodeAArsak());
                assertThat(kravgrunnlagBelop433Dto.skyldKode()).isEqualTo(detaljertKravgrunnlagBelopDto.getKodeSkyld());
            }

        }


    }


    private static DetaljertKravgrunnlagDto hentGrunnlag() {
        DetaljertKravgrunnlagDto detaljertKravgrunnlag = new DetaljertKravgrunnlagDto();
        detaljertKravgrunnlag.setVedtakId(BigInteger.valueOf(207406));
        detaljertKravgrunnlag.setKravgrunnlagId(BigInteger.valueOf(152806));
        detaljertKravgrunnlag.setDatoVedtakFagsystem(konvertDato(LocalDate.of(2019, 3, 14)));
        detaljertKravgrunnlag.setEnhetAnsvarlig(ENHET);
        detaljertKravgrunnlag.setFagsystemId("10000000000000000");
        detaljertKravgrunnlag.setKodeFagomraade("FP");
        detaljertKravgrunnlag.setKodeHjemmel("1234239042304");
        detaljertKravgrunnlag.setKontrollfelt("42354353453454");
        detaljertKravgrunnlag.setReferanse("1");
        detaljertKravgrunnlag.setRenterBeregnes(JaNeiDto.N);
        detaljertKravgrunnlag.setSaksbehId("Z9901136");
        detaljertKravgrunnlag.setUtbetalesTilId("12345678901");
        detaljertKravgrunnlag.setEnhetBehandl(ENHET);
        detaljertKravgrunnlag.setEnhetBosted(ENHET);
        detaljertKravgrunnlag.setKodeStatusKrav("BEHA");
        detaljertKravgrunnlag.setTypeGjelderId(TypeGjelderDto.PERSON);
        detaljertKravgrunnlag.setTypeUtbetId(TypeGjelderDto.PERSON);
        detaljertKravgrunnlag.setVedtakGjelderId("12345678901");
        detaljertKravgrunnlag.setVedtakIdOmgjort(BigInteger.valueOf(207407));
        detaljertKravgrunnlag.getTilbakekrevingsPeriode().addAll(hentPerioder());

        return detaljertKravgrunnlag;
    }

    private static List<DetaljertKravgrunnlagPeriodeDto> hentPerioder() {
        DetaljertKravgrunnlagPeriodeDto kravgrunnlagPeriode1 = new DetaljertKravgrunnlagPeriodeDto();
        PeriodeDto periode = new PeriodeDto();
        periode.setFom(konvertDato(LocalDate.of(2016, 3, 16)));
        periode.setTom(konvertDato(LocalDate.of(2016, 3, 31)));
        kravgrunnlagPeriode1.setPeriode(periode);
        kravgrunnlagPeriode1.setBelopSkattMnd(BigDecimal.valueOf(600.00));
        kravgrunnlagPeriode1.getTilbakekrevingsBelop().add(hentBeløp(BigDecimal.valueOf(6000.00), BigDecimal.ZERO, BigDecimal.ZERO, TypeKlasseDto.FEIL));
        kravgrunnlagPeriode1.getTilbakekrevingsBelop().add(hentBeløp(BigDecimal.ZERO, BigDecimal.valueOf(6000.00), BigDecimal.valueOf(6000.00), TypeKlasseDto.YTEL));

        DetaljertKravgrunnlagPeriodeDto kravgrunnlagPeriode2 = new DetaljertKravgrunnlagPeriodeDto();
        periode = new PeriodeDto();
        periode.setFom(konvertDato(LocalDate.of(2016, 4, 01)));
        periode.setTom(konvertDato(LocalDate.of(2016, 4, 30)));
        kravgrunnlagPeriode2.setPeriode(periode);
        kravgrunnlagPeriode2.setBelopSkattMnd(BigDecimal.valueOf(300.00));
        kravgrunnlagPeriode2.getTilbakekrevingsBelop().add(hentBeløp(BigDecimal.valueOf(3000.00), BigDecimal.ZERO, BigDecimal.ZERO, TypeKlasseDto.FEIL));
        kravgrunnlagPeriode2.getTilbakekrevingsBelop().add(hentBeløp(BigDecimal.ZERO, BigDecimal.valueOf(3000.00), BigDecimal.valueOf(3000.00), TypeKlasseDto.YTEL));

        DetaljertKravgrunnlagPeriodeDto kravgrunnlagPeriode3 = new DetaljertKravgrunnlagPeriodeDto();
        periode = new PeriodeDto();
        periode.setFom(konvertDato(LocalDate.of(2016, 5, 1)));
        periode.setTom(konvertDato(LocalDate.of(2016, 5, 26)));
        kravgrunnlagPeriode3.setPeriode(periode);
        kravgrunnlagPeriode3.setBelopSkattMnd(BigDecimal.valueOf(2100.00));
        kravgrunnlagPeriode3.getTilbakekrevingsBelop().add(hentBeløp(BigDecimal.valueOf(21000.00), BigDecimal.ZERO, BigDecimal.ZERO, TypeKlasseDto.FEIL));
        kravgrunnlagPeriode3.getTilbakekrevingsBelop().add(hentBeløp(BigDecimal.ZERO, BigDecimal.valueOf(21000.00), BigDecimal.valueOf(21000.00), TypeKlasseDto.YTEL));

        return Lists.newArrayList(kravgrunnlagPeriode1, kravgrunnlagPeriode2, kravgrunnlagPeriode3);
    }

    private static DetaljertKravgrunnlagBelopDto hentBeløp(BigDecimal nyBeløp, BigDecimal tilbakekrevesBeløp, BigDecimal opprUtbetBeløp, TypeKlasseDto typeKlasse) {
        DetaljertKravgrunnlagBelopDto detaljertKravgrunnlagBelop = new DetaljertKravgrunnlagBelopDto();
        detaljertKravgrunnlagBelop.setTypeKlasse(typeKlasse);
        detaljertKravgrunnlagBelop.setBelopNy(nyBeløp);
        detaljertKravgrunnlagBelop.setBelopOpprUtbet(opprUtbetBeløp);
        detaljertKravgrunnlagBelop.setBelopTilbakekreves(tilbakekrevesBeløp);
        detaljertKravgrunnlagBelop.setBelopUinnkrevd(BigDecimal.ZERO);
        detaljertKravgrunnlagBelop.setKodeKlasse("FPATAL");
        detaljertKravgrunnlagBelop.setSkattProsent(BigDecimal.valueOf(10.0000));

        return detaljertKravgrunnlagBelop;
    }

    private static XMLGregorianCalendar konvertDato(LocalDate localDate) {
        return DateUtil.convertToXMLGregorianCalendar(localDate);
    }
}
