package no.nav.foreldrepenger.ws.proxy.api.arena.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import no.nav.foreldrepenger.kontrakter.arena.request.ArenaRequestDto;
import no.nav.foreldrepenger.kontrakter.arena.respons.BeløpDto;
import no.nav.foreldrepenger.kontrakter.arena.respons.FagsystemDto;
import no.nav.foreldrepenger.kontrakter.arena.respons.MeldekortUtbetalingsgrunnlagMeldekortDto;
import no.nav.foreldrepenger.kontrakter.arena.respons.MeldekortUtbetalingsgrunnlagSakDto;
import no.nav.foreldrepenger.kontrakter.arena.respons.YtelseStatusDto;
import no.nav.foreldrepenger.kontrakter.arena.respons.YtelseTypeDto;
import no.nav.foreldrepenger.ws.proxy.util.DateUtil;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.Meldekort;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.ObjectFactory;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.Sak;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.Vedtak;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.meldinger.FinnMeldekortUtbetalingsgrunnlagListeRequest;

public class ArenaMapperWS {

    private ArenaMapperWS() {
    }

    private static final ObjectFactory objectFactory = new ObjectFactory();

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

    public static List<MeldekortUtbetalingsgrunnlagSakDto> oversettArenaSak(Sak sak) {
        List<MeldekortUtbetalingsgrunnlagSakDto> vedtakene = new ArrayList<>();
        if (sak.getVedtakListe().isEmpty()) {
            vedtakene.add(oversettArenaUtenVedtak(sak));
        }
        for (Vedtak vedtak : sak.getVedtakListe()) {
            vedtakene.add(oversettArenaVedtakMeldekort(sak, vedtak));
        }
        return vedtakene;
    }

    private static MeldekortUtbetalingsgrunnlagSakDto oversettArenaVedtakMeldekort(Sak sak, Vedtak vedtak) {
        List<MeldekortUtbetalingsgrunnlagMeldekortDto> meldekortList = new ArrayList<>();
        for (Meldekort meldekort : vedtak.getMeldekortListe()) {
            meldekortList.add(oversettArenaMeldekort(meldekort));
        }
        return new MeldekortUtbetalingsgrunnlagSakDto.Builder()
            .type(oversettType(sak))
            .tilstand(oversettTilstand(sak, vedtak))
            .kilde(FagsystemDto.ARENA)
            .kravMottattDato(oversettDatoNullable(vedtak.getDatoKravMottatt()))
            .saksnummer(sak.getFagsystemSakId())
            .sakStatus(sak.getSaksstatus().getValue())
            .vedtakStatus(vedtak.getVedtaksstatus().getValue())
            .vedtattDato(oversettDatoNullable(vedtak.getVedtaksdato()))
            .vedtaksPeriodeFom(oversettDatoNullable(vedtak.getVedtaksperiode().getFom()))
            .vedtaksPeriodeTom(oversettDatoNullable(vedtak.getVedtaksperiode().getTom()))
            .vedtaksDagsats(new BeløpDto(BigDecimal.valueOf(vedtak.getDagsats())))
            .meldekortene(meldekortList)
            .build();

    }

    private static MeldekortUtbetalingsgrunnlagSakDto oversettArenaUtenVedtak(Sak sak) {
        var sakBuilder = new MeldekortUtbetalingsgrunnlagSakDto.Builder()
            .type(oversettType(sak))
            .tilstand(oversettTilstandUtenVedtak(sak))
            .kilde(FagsystemDto.ARENA)
            .saksnummer(sak.getFagsystemSakId())
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

    private static YtelseTypeDto oversettType(Sak sak) {
        if (YtelseTypeDto.AAP.name().equals(sak.getTema().getValue())) {
            return YtelseTypeDto.AAP;
        } else if (YtelseTypeDto.DAG.name().equals(sak.getTema().getValue())) {
            return YtelseTypeDto.DAG;
        } else {
            return null;
        }
    }

    private static YtelseStatusDto oversettTilstand(Sak sak, Vedtak vedtak) {
        YtelseStatusDto statusVedtak = RelatertYtelseStatusReverse.reverseMap(vedtak.getVedtaksstatus().getValue());
        if (YtelseStatusDto.UBEH.equals(statusVedtak) &&
            YtelseStatusDto.AVSLU.equals(RelatertYtelseStatusReverse.reverseMap(sak.getSaksstatus().getValue()))) {
            return YtelseStatusDto.AVSLU;
        }
        return statusVedtak;
    }

    private static YtelseStatusDto oversettTilstandUtenVedtak(Sak sak) {
        return RelatertYtelseStatusReverse.reverseMap(sak.getSaksstatus().getValue());
    }

    private static MeldekortUtbetalingsgrunnlagMeldekortDto oversettArenaMeldekort(Meldekort meldekort) {
        return new MeldekortUtbetalingsgrunnlagMeldekortDto.Builder()
            .meldekortFom(oversettDatoNullable(meldekort.getMeldekortperiode().getFom()))
            .meldekortTom(oversettDatoNullable(meldekort.getMeldekortperiode().getTom()))
            .dagsats(BigDecimal.valueOf(meldekort.getDagsats()))
            .beløp(BigDecimal.valueOf(meldekort.getBeloep()))
            .utbetalingsgrad(BigDecimal.valueOf(meldekort.getUtbetalingsgrad()))
            .build();
    }

}
