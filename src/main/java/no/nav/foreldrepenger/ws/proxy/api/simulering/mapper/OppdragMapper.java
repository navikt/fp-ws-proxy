package no.nav.foreldrepenger.ws.proxy.api.simulering.mapper;

import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.KodeFagområde;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.Ompostering116Dto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.Oppdrag110Dto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.Oppdragslinje150Dto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.Refusjonsinfo156Dto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.UtbetalingsgradDto;
import no.nav.system.os.entiteter.oppdragskjema.Attestant;
import no.nav.system.os.entiteter.oppdragskjema.Enhet;
import no.nav.system.os.entiteter.oppdragskjema.Grad;
import no.nav.system.os.entiteter.oppdragskjema.Ompostering;
import no.nav.system.os.entiteter.oppdragskjema.RefusjonsInfo;
import no.nav.system.os.entiteter.typer.simpletypes.FradragTillegg;
import no.nav.system.os.entiteter.typer.simpletypes.KodeStatusLinje;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.ObjectFactory;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.Oppdrag;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.Oppdragslinje;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;

public class OppdragMapper {
    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final LocalDate DATO_OPPDRAG_GJELDER_FOM = LocalDate.of(2000, 1, 1);
    private static final String FRADRAG_TILLEGG = "T";
    private static final String BRUK_KJOREPLAN = "N";
    private static final String TYPE_GRAD = "UFOR";
    private static final String UTBET_FREKVENS = "MND";
    private static final Enhet DEFAULT_ENHET = oppdragsEnhet120();

    private OppdragMapper() {
        // skal ikke kunne instansiere klassen
    }


    /**
     * Mapper oppdrag-110 sendt fra FPSAK til oppdrag som FPOPPDRAG sender til økonomi.
     *
     * @param oppdrag110
     * @return
     */
    public static Oppdrag mapTilSimuleringOppdrag(Oppdrag110Dto oppdrag110, String behandlingId) {
        var oppdrag = new ObjectFactory().createOppdrag();
        oppdrag.setKodeEndring(oppdrag110.kodeEndring().name());
        oppdrag.setKodeFagomraade(oppdrag110.kodeFagomrade().name());
        oppdrag.setFagsystemId(oppdrag110.fagsystemId());
        oppdrag.setUtbetFrekvens(UTBET_FREKVENS);
        oppdrag.setOppdragGjelderId(oppdrag110.oppdragGjelderId());
        oppdrag.setDatoOppdragGjelderFom(localdateTilString(DATO_OPPDRAG_GJELDER_FOM));
        oppdrag.setSaksbehId(oppdrag110.saksbehId());
        oppdrag.getEnhet().add(DEFAULT_ENHET);
        oppdrag.getOppdragslinje().addAll(mapOppdragslinje150(oppdrag110.oppdragslinje150Liste(),  oppdrag110.kodeFagomrade(), oppdrag110.saksbehId(), behandlingId));
        if (oppdrag110.ompostering116() != null) {
            oppdrag.setOmpostering(mapOmpostering(oppdrag110.ompostering116(), oppdrag110.saksbehId()));
        }
        return oppdrag;
    }

    static Ompostering mapOmpostring(boolean ompostering, String saksbehandlerId, String tidspktReg) {
        var op = new Ompostering();
        op.setOmPostering(Boolean.TRUE.equals(ompostering) ? "J" : "N");
        op.setSaksbehId(saksbehandlerId);
        op.setTidspktReg(tidspktReg);
        return op;
    }

    private static Ompostering mapOmpostering(Ompostering116Dto ompostering116, String saksbehandlerId) {
        var op = mapOmpostring(ompostering116.omPostering(), saksbehandlerId, ompostering116.tidspktReg());
        if (ompostering116.datoOmposterFom() != null) {
            op.setDatoOmposterFom(localdateTilString(ompostering116.datoOmposterFom()));
        }
        return op;
    }

    private static Enhet oppdragsEnhet120() {
        var enhet = new Enhet();
        enhet.setDatoEnhetFom(localdateTilString(LocalDate.of(1900, 1, 1)));
        enhet.setEnhet("8020");
        enhet.setTypeEnhet("BOS");
        return enhet;
    }

    private static List<Oppdragslinje> mapOppdragslinje150(List<Oppdragslinje150Dto> oppdragsLinje150Liste, KodeFagområde kodeFagområde, String saksbehId, String behandlingId) {
        return safeStream(oppdragsLinje150Liste)
            .map(oppdragsLinje150 -> mapOppdragslinje150(oppdragsLinje150, kodeFagområde, saksbehId, behandlingId))
            .sorted(Comparator.comparing(opp150 -> Long.parseLong(opp150.getDelytelseId())))
            .toList();
    }

    private static Oppdragslinje mapOppdragslinje150(Oppdragslinje150Dto oppdragsLinje150, KodeFagområde kodeFagområde, String saksbehId, String behandlingId) {
        var oppdragslinje = new Oppdragslinje();

        // mapper enkeltelementer
        oppdragslinje.setKodeEndringLinje(oppdragsLinje150.kodeEndringLinje().name());
        oppdragslinje.setVedtakId(oppdragsLinje150.vedtakId());
        oppdragslinje.setDelytelseId(oppdragsLinje150.delytelseId());
        oppdragslinje.setKodeKlassifik(oppdragsLinje150.kodeKlassifik().getKode());
        oppdragslinje.setDatoVedtakFom(localdateTilString(oppdragsLinje150.getDatoVedtakFom()));
        oppdragslinje.setDatoVedtakTom(localdateTilString(oppdragsLinje150.getDatoVedtakTom()));
        oppdragslinje.setSats(BigDecimal.valueOf(oppdragsLinje150.sats().verdi()));
        oppdragslinje.setFradragTillegg(FradragTillegg.fromValue(FRADRAG_TILLEGG));
        oppdragslinje.setTypeSats(oppdragsLinje150.typeSats().name());
        oppdragslinje.setBrukKjoreplan(BRUK_KJOREPLAN);
        oppdragslinje.setSaksbehId(saksbehId);
        oppdragslinje.setHenvisning(behandlingId);
        oppdragslinje.setUtbetalesTilId(oppdragsLinje150.utbetalesTilId());
        oppdragslinje.getAttestant().addAll(mapAttestant180(saksbehId));


        if (oppdragsLinje150.refFagsystemId() != null) {
            oppdragslinje.setRefFagsystemId(oppdragsLinje150.refFagsystemId());
        }
        if (oppdragsLinje150.refDelytelseId() != null) {
            oppdragslinje.setRefDelytelseId(oppdragsLinje150.refDelytelseId());
        }
        if (oppdragsLinje150.datoStatusFom() != null) {
            oppdragslinje.setDatoStatusFom(localdateTilString(oppdragsLinje150.datoStatusFom()));
        }
        if (oppdragsLinje150.gjelderOpphør()) {
            oppdragslinje.setKodeStatusLinje(KodeStatusLinje.OPPH);
        }
        if (!kodeFagområde.gjelderEngangsstønad()) {
            if (null != oppdragsLinje150.utbetalingsgrad()) {
                oppdragslinje.getGrad().addAll(mapGrad170(oppdragsLinje150.utbetalingsgrad()));
            }
            if (kodeFagområde.gjelderRefusjonTilArbeidsgiver()) {
                oppdragslinje.setRefusjonsInfo(mapRefusjonsinfo156(oppdragsLinje150.refusjonsinfo156()));
            }
        }
        return oppdragslinje;
    }

    private static RefusjonsInfo mapRefusjonsinfo156(Refusjonsinfo156Dto refusjonsinfo156) {
        var refusjonsInfo = new RefusjonsInfo();
        refusjonsInfo.setMaksDato(localdateTilString(refusjonsinfo156.maksDato()));
        refusjonsInfo.setDatoFom(localdateTilString(refusjonsinfo156.datoFom()));
        refusjonsInfo.setRefunderesId(refusjonsinfo156.refunderesId());
        return refusjonsInfo;
    }

    private static List<Grad> mapGrad170(UtbetalingsgradDto utbetalingsgradDto) {
        var grad = new Grad();
        grad.setGrad(BigInteger.valueOf(utbetalingsgradDto.verdi()));
        grad.setTypeGrad(TYPE_GRAD);
        return List.of(grad);
    }

    private static List<Attestant> mapAttestant180(String saksbehId) {
        var attestant = new Attestant();
        attestant.setAttestantId(saksbehId);
        return List.of(attestant);
    }

    private static String localdateTilString(LocalDate date) {
        return date != null ? date.format(DATE_TIME_FORMATTER) : null;
    }
}
