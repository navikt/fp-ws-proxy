package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import com.google.common.collect.Lists;

import no.nav.foreldrepenger.ws.proxy.api.SyntetiskTestData;
import no.nav.foreldrepenger.ws.proxy.util.DateUtil;
import no.nav.tilbakekreving.kravgrunnlag.detalj.v1.DetaljertKravgrunnlagBelopDto;
import no.nav.tilbakekreving.kravgrunnlag.detalj.v1.DetaljertKravgrunnlagDto;
import no.nav.tilbakekreving.kravgrunnlag.detalj.v1.DetaljertKravgrunnlagPeriodeDto;
import no.nav.tilbakekreving.typer.v1.JaNeiDto;
import no.nav.tilbakekreving.typer.v1.PeriodeDto;
import no.nav.tilbakekreving.typer.v1.TypeGjelderDto;
import no.nav.tilbakekreving.typer.v1.TypeKlasseDto;

public class KravgrunnlagTestDataBuilder {

    private static final String ENHET = "8020";

    public static DetaljertKravgrunnlagDto hentGrunnlag() {
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
        detaljertKravgrunnlag.setSaksbehId("W123456");
        detaljertKravgrunnlag.setUtbetalesTilId(SyntetiskTestData.SYNTETISK_FNR);
        detaljertKravgrunnlag.setEnhetBehandl(ENHET);
        detaljertKravgrunnlag.setEnhetBosted(ENHET);
        detaljertKravgrunnlag.setKodeStatusKrav("BEHA");
        detaljertKravgrunnlag.setTypeGjelderId(TypeGjelderDto.PERSON);
        detaljertKravgrunnlag.setTypeUtbetId(TypeGjelderDto.PERSON);
        detaljertKravgrunnlag.setVedtakGjelderId(SyntetiskTestData.SYNTETISK_FNR);
        detaljertKravgrunnlag.setVedtakIdOmgjort(BigInteger.valueOf(207407));
        detaljertKravgrunnlag.getTilbakekrevingsPeriode().addAll(hentPerioder());

        return detaljertKravgrunnlag;
    }

    public static List<DetaljertKravgrunnlagPeriodeDto> hentPerioder() {
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

    public static DetaljertKravgrunnlagBelopDto hentBeløp(BigDecimal nyBeløp, BigDecimal tilbakekrevesBeløp, BigDecimal opprUtbetBeløp, TypeKlasseDto typeKlasse) {
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

    public static PeriodeDto periode(LocalDate fom, LocalDate tom) {
        var periode = new PeriodeDto();
        periode.setFom(konvertDato(fom));
        periode.setTom(konvertDato(tom));
        return periode;
    }

    private static XMLGregorianCalendar konvertDato(LocalDate localDate) {
        return DateUtil.convertToXMLGregorianCalendar(localDate);
    }
}
