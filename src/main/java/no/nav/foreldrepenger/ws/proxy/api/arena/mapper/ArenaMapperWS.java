package no.nav.foreldrepenger.ws.proxy.api.arena.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.ws.proxy.api.arena.dto.ArenaRequestDto;
import no.nav.foreldrepenger.ws.proxy.api.arena.dto.Beløp;
import no.nav.foreldrepenger.ws.proxy.api.arena.dto.Fagsystem;
import no.nav.foreldrepenger.ws.proxy.api.arena.dto.MeldekortUtbetalingsgrunnlagMeldekort;
import no.nav.foreldrepenger.ws.proxy.api.arena.dto.MeldekortUtbetalingsgrunnlagSak;
import no.nav.foreldrepenger.ws.proxy.api.arena.dto.YtelseStatus;
import no.nav.foreldrepenger.ws.proxy.api.arena.dto.YtelseType;
import no.nav.foreldrepenger.xmlutils.DateUtil;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.Meldekort;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.ObjectFactory;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.Sak;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.Vedtak;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.meldinger.FinnMeldekortUtbetalingsgrunnlagListeRequest;

public class ArenaMapperWS {

    private static final Logger LOG = LoggerFactory.getLogger(ArenaMapperWS.class);
    private static final ObjectFactory objectFactory = new ObjectFactory(); // TODO: Kan denne være static?


    public static FinnMeldekortUtbetalingsgrunnlagListeRequest tilWSRequest(ArenaRequestDto arenaDto) {
        var request = new FinnMeldekortUtbetalingsgrunnlagListeRequest();

        var bruker = objectFactory.createBruker();
        bruker.setIdent(arenaDto.ident());
        request.setIdent(bruker);

        var periode = objectFactory.createPeriode();
        periode.setFom(DateUtil.convertToXMLGregorianCalendarRemoveTimezone(arenaDto.fom()));
        if (arenaDto.tom() != null) {
            periode.setTom(DateUtil.convertToXMLGregorianCalendarRemoveTimezone(arenaDto.tom()));
        }
        request.setPeriode(periode);

        var aapTema = objectFactory.createTema();
        aapTema.setValue("AAP");
        request.getTemaListe().add(aapTema);

        var dagTema = objectFactory.createTema();
        dagTema.setValue("DAG");
        request.getTemaListe().add(dagTema);

        return request;
    }

    public static List<MeldekortUtbetalingsgrunnlagSak> oversettArenaSak(Sak sak) {
        List<MeldekortUtbetalingsgrunnlagSak> vedtakene = new ArrayList<>();
        if (sak.getVedtakListe().isEmpty()) {
            vedtakene.add(oversettArenaUtenVedtak(sak));
        }
        for (Vedtak vedtak : sak.getVedtakListe()) {
            vedtakene.add(oversettArenaVedtakMeldekort(sak, vedtak));
        }
        return vedtakene;
    }

    private static MeldekortUtbetalingsgrunnlagSak oversettArenaVedtakMeldekort(Sak sak, Vedtak vedtak) {
        List<MeldekortUtbetalingsgrunnlagMeldekort> meldekortList = new ArrayList<>();
        for (Meldekort meldekort : vedtak.getMeldekortListe()) {
            meldekortList.add(oversettArenaMeldekort(meldekort));
        }
        return MeldekortUtbetalingsgrunnlagSak.builder()
            .type(oversettType(sak))
            .tilstand(oversettTilstand(sak, vedtak))
            .kilde(Fagsystem.ARENA)
            .kravMottattDato(oversettDatoNullable(vedtak.getDatoKravMottatt()))
            .saksnummer(new Saksnummer(sak.getFagsystemSakId()))
            .sakStatus(sak.getSaksstatus().getValue())
            .vedtakStatus(vedtak.getVedtaksstatus().getValue())
            .vedtattDato(oversettDatoNullable(vedtak.getVedtaksdato()))
            .vedtaksPeriodeFom(oversettDatoNullable(vedtak.getVedtaksperiode().getFom()))
            .vedtaksPeriodeTom(oversettDatoNullable(vedtak.getVedtaksperiode().getTom()))
            .vedtaksDagsats(new Beløp(BigDecimal.valueOf(vedtak.getDagsats())))
            .meldekortene(meldekortList)
            .build();

    }

    private static MeldekortUtbetalingsgrunnlagSak oversettArenaUtenVedtak(Sak sak) {
        var sakBuilder = MeldekortUtbetalingsgrunnlagSak.builder()
            .type(oversettType(sak))
            .tilstand(oversettTilstandUtenVedtak(sak))
            .kilde(Fagsystem.ARENA)
            .saksnummer(new Saksnummer(sak.getFagsystemSakId()))
            .sakStatus(sak.getSaksstatus().getValue())
            .kravMottattDato(null)
            .vedtakStatus(null);
        return sakBuilder.build();
    }

    private static LocalDate oversettDatoNullable(XMLGregorianCalendar datoXML) {
        if (datoXML == null) {
            return null;
        }
        return datoXML.toGregorianCalendar().toZonedDateTime().toLocalDate();
    }

    private static YtelseType oversettType(Sak sak) {
        if (YtelseType.ARBEIDSAVKLARINGSPENGER.getKode().equals(sak.getTema().getValue())) {
            return YtelseType.ARBEIDSAVKLARINGSPENGER;
        } else if (YtelseType.DAGPENGER.getKode().equals(sak.getTema().getValue())) {
            return YtelseType.DAGPENGER;
        } else {
            return YtelseType.UDEFINERT;
        }
    }

    private static YtelseStatus oversettTilstand(Sak sak, Vedtak vedtak) {
        YtelseStatus statusVedtak = RelatertYtelseStatusReverse.reverseMap(vedtak.getVedtaksstatus().getValue(), LOG);
        if (YtelseStatus.UNDER_BEHANDLING.equals(statusVedtak) &&
            YtelseStatus.AVSLUTTET.equals(RelatertYtelseStatusReverse.reverseMap(sak.getSaksstatus().getValue(), LOG))) {
            return YtelseStatus.AVSLUTTET;
        }
        return statusVedtak;
    }

    private static YtelseStatus oversettTilstandUtenVedtak(Sak sak) {
        return RelatertYtelseStatusReverse.reverseMap(sak.getSaksstatus().getValue(), LOG);
    }

    private static MeldekortUtbetalingsgrunnlagMeldekort oversettArenaMeldekort(Meldekort meldekort) {
        return MeldekortUtbetalingsgrunnlagMeldekort.builder()
            .meldekortFom(oversettDatoNullable(meldekort.getMeldekortperiode().getFom()))
            .meldekortTom(oversettDatoNullable(meldekort.getMeldekortperiode().getTom()))
            .dagsats(BigDecimal.valueOf(meldekort.getDagsats()))
            .beløp(BigDecimal.valueOf(meldekort.getBeloep()))
            .utbetalingsgrad(BigDecimal.valueOf(meldekort.getUtbetalingsgrad()))
            .build();
    }

}
