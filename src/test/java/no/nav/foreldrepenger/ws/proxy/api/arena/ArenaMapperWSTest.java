package no.nav.foreldrepenger.ws.proxy.api.arena;

import static no.nav.foreldrepenger.kontrakter.fpwsproxy.arena.respons.YtelseTypeDto.DAG;
import static no.nav.foreldrepenger.ws.proxy.util.DateUtil.convertToLocalDate;
import static no.nav.foreldrepenger.ws.proxy.util.DateUtil.convertToXMLGregorianCalendar;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.kontrakter.fpwsproxy.arena.request.ArenaRequestDto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.arena.respons.MeldekortUtbetalingsgrunnlagSakDto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.arena.respons.YtelseStatusDto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.arena.respons.YtelseTypeDto;
import no.nav.foreldrepenger.ws.proxy.api.arena.mapper.ArenaMapperWS;
import no.nav.foreldrepenger.ws.proxy.api.arena.mapper.RelatertYtelseStatus;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.Bruker;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.Kodeverdi;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.Meldekort;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.Periode;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.Sak;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.Saksstatuser;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.Tema;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.Vedtak;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.Vedtaksstatuser;

class ArenaMapperWSTest {

    @Test
    void verifiserAtDataIkkeForsvinnerIMappingTilXMLRequest() {
        var arenaRequestDto = lagArenaRequestDto();
        var finnMeldekortUtbetalingsgrunnlagListeRequest = ArenaMapperWS.tilWSRequest(arenaRequestDto);

        var ident = finnMeldekortUtbetalingsgrunnlagListeRequest.getIdent();
        assertThat(ident)
            .isInstanceOf(Bruker.class)
            .extracting(aktoer -> ((Bruker) aktoer))
            .extracting(Bruker::getIdent)
            .isEqualTo(arenaRequestDto.ident());

        var periode = finnMeldekortUtbetalingsgrunnlagListeRequest.getPeriode();
        assertThat(convertToLocalDate(periode.getFom())).isEqualTo(arenaRequestDto.fom());
        assertThat(convertToLocalDate(periode.getTom())).isEqualTo(arenaRequestDto.tom());

        assertThat(finnMeldekortUtbetalingsgrunnlagListeRequest.getTemaListe())
            .extracting(Kodeverdi::getValue)
            .containsExactly("AAP", "DAG");

    }


    @Test
    void mapGyldigXMLResponsTilMeldekortUtbetalingsgrunnlag() {
        var sak = new Sak();
        LocalDate fomVedtak1 = LocalDate.now().minusMonths(5);
        LocalDate tomVedtak1 = LocalDate.now().minusMonths(2);
        var vedtak1 = lagVedtak(fomVedtak1, tomVedtak1);
        LocalDate fomVedtak2 = LocalDate.now().minusMonths(1);
        LocalDate tomVedtak2 = LocalDate.now().minusDays(1);
        var vedtak2 = lagVedtak(fomVedtak2, tomVedtak2);
        sak.getVedtakListe().add(vedtak1);
        sak.getVedtakListe().add(vedtak2);
        var fnrFAKE = "123456789";
        sak.setFagsystemSakId(fnrFAKE);

        var ytelseStatus = YtelseStatusDto.LOP;
        var saksstatus = lagSakstatus(ytelseStatus);
        sak.setSaksstatus(saksstatus);

        var ytelsetype = DAG;
        var tema = lagTemaFraYtelsetype(ytelsetype);
        sak.setTema(tema);

        var meldekortUtbetalingsgrunnlagSak = ArenaMapperWS.oversettArenaSak(sak);
        assertThat(meldekortUtbetalingsgrunnlagSak)
            .hasSize(2);
        assertThat(meldekortUtbetalingsgrunnlagSak)
            .extracting(MeldekortUtbetalingsgrunnlagSakDto::type)
            .containsOnly(ytelsetype);
        assertThat(meldekortUtbetalingsgrunnlagSak)
            .extracting(MeldekortUtbetalingsgrunnlagSakDto::saksnummer)
            .containsOnly(fnrFAKE);
        assertThat(meldekortUtbetalingsgrunnlagSak)
            .extracting(MeldekortUtbetalingsgrunnlagSakDto::sakStatus)
            .containsOnly(ytelseStatus.name());
        assertThat(meldekortUtbetalingsgrunnlagSak)
            .extracting(MeldekortUtbetalingsgrunnlagSakDto::vedtakStatus)
            .containsOnly(RelatertYtelseStatus.AVSLU.getKode());

        assertThat(meldekortUtbetalingsgrunnlagSak)
            .extracting(MeldekortUtbetalingsgrunnlagSakDto::vedtaksPeriodeFom)
            .containsExactly(fomVedtak1, fomVedtak2);
        assertThat(meldekortUtbetalingsgrunnlagSak)
            .extracting(MeldekortUtbetalingsgrunnlagSakDto::vedtaksPeriodeTom)
            .containsExactly(tomVedtak1, tomVedtak2);

        assertThat(meldekortUtbetalingsgrunnlagSak)
            .extracting(MeldekortUtbetalingsgrunnlagSakDto::vedtattDato)
            .containsExactly(fomVedtak1, fomVedtak2);
        assertThat(meldekortUtbetalingsgrunnlagSak)
            .extracting(MeldekortUtbetalingsgrunnlagSakDto::kravMottattDato)
            .containsExactly(fomVedtak1, fomVedtak2);

    }

    protected static ArenaRequestDto lagArenaRequestDto() {
        var fnr = "11111122222";
        var fom = LocalDate.now().minusMonths(2);
        var tom = LocalDate.now();
        return new ArenaRequestDto(fnr, fom, tom);
    }

    protected static Sak lagSak() {
        var sak = new Sak();
        LocalDate fomVedtak1 = LocalDate.now().minusMonths(5);
        LocalDate tomVedtak1 = LocalDate.now().minusMonths(2);
        var vedtak1 = lagVedtak(fomVedtak1, tomVedtak1);
        LocalDate fomVedtak2 = LocalDate.now().minusMonths(1);
        LocalDate tomVedtak2 = LocalDate.now().minusDays(1);
        var vedtak2 = lagVedtak(fomVedtak2, tomVedtak2);
        sak.getVedtakListe().add(vedtak1);
        sak.getVedtakListe().add(vedtak2);
        var fnrFAKE = "123456789";
        sak.setFagsystemSakId(fnrFAKE);

        var ytelseStatus = YtelseStatusDto.LOP;
        var saksstatus = lagSakstatus(ytelseStatus);
        sak.setSaksstatus(saksstatus);

        var ytelsetype = DAG;
        var tema = lagTemaFraYtelsetype(ytelsetype);
        sak.setTema(tema);
        return sak;
    }

    protected static Saksstatuser lagSakstatus(YtelseStatusDto sakstatus) {
        var saksstatus = new Saksstatuser();
        saksstatus.setValue(sakstatus.name());
        return saksstatus;
    }

    protected static Tema lagTemaFraYtelsetype(YtelseTypeDto ytelsetype) {
        var tema = new Tema();
        tema.setValue(ytelsetype.name());
        return tema;
    }

    protected static Vedtak lagVedtak(LocalDate fom, LocalDate tom) {
        var vedtak1 = new Vedtak();
        var meldekort = new Meldekort();
        var periode = new Periode();
        var fomXMLGreg = convertToXMLGregorianCalendar(fom);
        var tomXMLGreg = convertToXMLGregorianCalendar(tom);
        periode.setFom(fomXMLGreg);
        periode.setTom(tomXMLGreg);
        meldekort.setMeldekortperiode(periode);
        int dagsats = 2_500;
        meldekort.setDagsats(dagsats);
        meldekort.setBeloep(3_000);
        meldekort.setUtbetalingsgrad(100);
        vedtak1.getMeldekortListe().add(meldekort);
        vedtak1.setVedtaksperiode(periode);
        var vedtakstatus = new Vedtaksstatuser();
        vedtakstatus.setValue(RelatertYtelseStatus.AVSLU.getKode());
        vedtak1.setVedtaksstatus(vedtakstatus);
        vedtak1.setVedtaksdato(fomXMLGreg);
        vedtak1.setDatoKravMottatt(fomXMLGreg);
        vedtak1.setDagsats(dagsats);
        return vedtak1;
    }

}
