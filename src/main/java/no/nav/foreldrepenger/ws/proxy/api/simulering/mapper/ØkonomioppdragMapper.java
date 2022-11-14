package no.nav.foreldrepenger.ws.proxy.api.simulering.mapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import no.nav.foreldrepenger.integrasjon.økonomistøtte.oppdrag.Attestant180;
import no.nav.foreldrepenger.integrasjon.økonomistøtte.oppdrag.Avstemming115;
import no.nav.foreldrepenger.integrasjon.økonomistøtte.oppdrag.Grad170;
import no.nav.foreldrepenger.integrasjon.økonomistøtte.oppdrag.ObjectFactory;
import no.nav.foreldrepenger.integrasjon.økonomistøtte.oppdrag.Ompostering116;
import no.nav.foreldrepenger.integrasjon.økonomistøtte.oppdrag.Oppdrag;
import no.nav.foreldrepenger.integrasjon.økonomistøtte.oppdrag.OppdragSkjemaConstants;
import no.nav.foreldrepenger.integrasjon.økonomistøtte.oppdrag.OppdragsEnhet120;
import no.nav.foreldrepenger.integrasjon.økonomistøtte.oppdrag.OppdragsLinje150;
import no.nav.foreldrepenger.integrasjon.økonomistøtte.oppdrag.TfradragTillegg;
import no.nav.foreldrepenger.integrasjon.økonomistøtte.oppdrag.TkodeStatusLinje;
import no.nav.foreldrepenger.kontrakter.simulering.request.KodeFagområde;
import no.nav.foreldrepenger.kontrakter.simulering.request.Ompostering116Dto;
import no.nav.foreldrepenger.kontrakter.simulering.request.Oppdrag110Dto;
import no.nav.foreldrepenger.kontrakter.simulering.request.OppdragskontrollDto;
import no.nav.foreldrepenger.kontrakter.simulering.request.Oppdragslinje150Dto;
import no.nav.foreldrepenger.kontrakter.simulering.request.Refusjonsinfo156Dto;
import no.nav.foreldrepenger.kontrakter.simulering.request.UtbetalingsgradDto;
import no.nav.foreldrepenger.ws.proxy.util.DateUtil;
import no.nav.foreldrepenger.ws.proxy.util.JaxbHelper;
import no.nav.vedtak.exception.TekniskException;

public class ØkonomioppdragMapper {

    private static final Logger LOG = LoggerFactory.getLogger(ØkonomioppdragMapper.class);

    private static final String TYPE_ENHET = "BOS";
    private static final String ENHET = "8020";
    private static final LocalDate DATO_ENHET_FOM = LocalDate.of(1900, 1, 1);
    private static final String FRADRAG_TILLEGG = "T";
    private static final String BRUK_KJOREPLAN = "N";
    private static final String TYPE_GRAD = "UFOR";
    private static final String KODE_AKSJON = "1";
    private static final String UTBET_FREKVENS = "MND";
    private static final LocalDate DATO_OPPDRAG_GJELDER_FOM = LocalDate.of(2000, 1, 1);

    private final ObjectFactory objectFactory = new ObjectFactory();

    public ØkonomioppdragMapper() {}

    public List<String> generateOppdragXML(OppdragskontrollDto oppdragskontrollTilSimulering) {
        var behandlingsId = oppdragskontrollTilSimulering.behandlingId();
        return oppdragskontrollTilSimulering.oppdrag().stream()
            .map(oppdrag110Dto -> mapVedtaksDataToOppdrag(oppdrag110Dto, behandlingsId))
            .map(oppdrag -> tilXml(behandlingsId, oppdrag))
            .toList();
    }

    Oppdrag mapVedtaksDataToOppdrag(Oppdrag110Dto okoOppdrag110, Long behandlingId) {
        final var oppdrag = objectFactory.createOppdrag();
        oppdrag.setOppdrag110(mapOppdrag110(okoOppdrag110, behandlingId));
        return oppdrag;
    }

    private String tilXml(Long behandlingsId, Oppdrag oppdrag) {
        try {
            LOG.debug("Oppretter oppdrag XML for behandling: {} og fagsystem: {}", behandlingsId, oppdrag.getOppdrag110().getFagsystemId());
            return JaxbHelper.marshalAndValidateJaxb(OppdragSkjemaConstants.JAXB_CLASS, oppdrag, OppdragSkjemaConstants.XSD_LOCATION);
        } catch (JAXBException | SAXException e) {
            throw new TekniskException("FP-536167",
                String.format("Kan ikke konvertere oppdrag med id %s. Problemer ved generering av xml", oppdrag.getOppdrag110().getOppdragsId()),
                e);
        }
    }

    private no.nav.foreldrepenger.integrasjon.økonomistøtte.oppdrag.Oppdrag110 mapOppdrag110(Oppdrag110Dto okoOppdrag110, Long behandlingId) {
        final var oppdrag110 = objectFactory.createOppdrag110();
        var kodeFagområde = okoOppdrag110.kodeFagomrade();

        oppdrag110.setKodeAksjon(KODE_AKSJON);
        oppdrag110.setKodeEndring(okoOppdrag110.kodeEndring().name());
        oppdrag110.setKodeFagomraade(kodeFagområde.name());
        oppdrag110.setFagsystemId(String.valueOf(okoOppdrag110.fagsystemId()));
        oppdrag110.setUtbetFrekvens(UTBET_FREKVENS);
        oppdrag110.setOppdragGjelderId(okoOppdrag110.oppdragGjelderId());
        oppdrag110.setSaksbehId(String.valueOf(okoOppdrag110.saksbehId()));
        oppdrag110.setAvstemming115(mapAvstemming115(okoOppdrag110.nøkkelAvstemming()));

        oppdrag110.getOppdragsEnhet120().add(mapOppdragsEnhet120());
        oppdrag110.getOppdragsLinje150().addAll(mapOppdragsLinje150(okoOppdrag110.oppdragslinje150Liste(), kodeFagområde, okoOppdrag110.saksbehId(), behandlingId));
        oppdrag110.setDatoOppdragGjelderFom(toXmlGregCal(DATO_OPPDRAG_GJELDER_FOM));

        var optOmpostering116 = okoOppdrag110.ompostering116();
        if (optOmpostering116 != null) {
            oppdrag110.setOmpostering116(mapOmpostering116(optOmpostering116, oppdrag110.getSaksbehId()));
        }
        return oppdrag110;
    }


    private Ompostering116 mapOmpostering116(Ompostering116Dto okoOmpostering116, String saksbehandlerId) {
        var ompostering116 = objectFactory.createOmpostering116();
        ompostering116.setOmPostering(Boolean.TRUE.equals(okoOmpostering116.omPostering()) ? "J" : "N");
        ompostering116.setDatoOmposterFom(toXmlGregCal(okoOmpostering116.datoOmposterFom()));
        ompostering116.setSaksbehId(saksbehandlerId);
        ompostering116.setTidspktReg(okoOmpostering116.tidspktReg());
        return ompostering116;
    }

    private Avstemming115 mapAvstemming115(String nøkkelAvstemming) {
        final var avstemming115 = objectFactory.createAvstemming115();
        avstemming115.setKodeKomponent(ØkonomiKodekomponent.VLFP.name());
        avstemming115.setNokkelAvstemming(nøkkelAvstemming);
        avstemming115.setTidspktMelding(nøkkelAvstemming); // TODO: Getter for tidspunkt i fpsak returner nøkkelAvstemming
        return avstemming115;
    }

    private OppdragsEnhet120 mapOppdragsEnhet120() {
        final var oppdragsEnhet120 = objectFactory.createOppdragsEnhet120();
        oppdragsEnhet120.setTypeEnhet(TYPE_ENHET);
        oppdragsEnhet120.setEnhet(ENHET);
        oppdragsEnhet120.setDatoEnhetFom(toXmlGregCal(DATO_ENHET_FOM));

        return oppdragsEnhet120;
    }

    private List<OppdragsLinje150> mapOppdragsLinje150(List<Oppdragslinje150Dto> okoOppdrlinje150Liste, KodeFagområde kodeFagområde, String saksbehId, Long behandlingId) {
        List<OppdragsLinje150> oppdragsLinje150Liste = new ArrayList<>();
        for (var okoOppdrlinje150 : okoOppdrlinje150Liste) {
            var oppdragsLinje150 = objectFactory.createOppdragsLinje150();
            oppdragsLinje150.setKodeEndringLinje(okoOppdrlinje150.kodeEndringLinje().name());
            if (okoOppdrlinje150.gjelderOpphør()) {
                oppdragsLinje150.setKodeStatusLinje(TkodeStatusLinje.fromValue(okoOppdrlinje150.kodeStatusLinje().name()));
            }
            if (okoOppdrlinje150.datoStatusFom() != null) {
                oppdragsLinje150.setDatoStatusFom(toXmlGregCal(okoOppdrlinje150.datoStatusFom()));
            }
            oppdragsLinje150.setVedtakId(okoOppdrlinje150.vedtakId());
            oppdragsLinje150.setDelytelseId(String.valueOf(okoOppdrlinje150.delytelseId()));
            oppdragsLinje150.setKodeKlassifik(okoOppdrlinje150.kodeKlassifik().getKode());
            oppdragsLinje150.setDatoVedtakFom(toXmlGregCal(okoOppdrlinje150.getDatoVedtakFom()));
            oppdragsLinje150.setDatoVedtakTom(toXmlGregCal(okoOppdrlinje150.getDatoVedtakTom()));
            oppdragsLinje150.setSats(BigDecimal.valueOf(okoOppdrlinje150.sats().verdi()));
            oppdragsLinje150.setFradragTillegg(TfradragTillegg.fromValue(FRADRAG_TILLEGG));
            oppdragsLinje150.setTypeSats(okoOppdrlinje150.typeSats().name());
            oppdragsLinje150.setBrukKjoreplan(BRUK_KJOREPLAN);
            oppdragsLinje150.setSaksbehId(saksbehId);
            oppdragsLinje150.setUtbetalesTilId(okoOppdrlinje150.utbetalesTilId());
            oppdragsLinje150.setHenvisning(String.valueOf(behandlingId));
            if (okoOppdrlinje150.refFagsystemId() != null) {
                oppdragsLinje150.setRefFagsystemId(String.valueOf(okoOppdrlinje150.refFagsystemId()));
            }
            if (okoOppdrlinje150.refDelytelseId() != null) {
                oppdragsLinje150.setRefDelytelseId(String.valueOf(okoOppdrlinje150.refDelytelseId()));
            }
            oppdragsLinje150.getAttestant180().add(mapAttestant180(saksbehId));

            if (!kodeFagområde.gjelderEngangsstønad()) {
                if (null != okoOppdrlinje150.utbetalingsgrad()) {
                    oppdragsLinje150.getGrad170().add(mapGrad170(okoOppdrlinje150.utbetalingsgrad()));
                }
                if (kodeFagområde.gjelderRefusjonTilArbeidsgiver()) {
                    oppdragsLinje150.setRefusjonsinfo156(mapRefusjonInfo156(okoOppdrlinje150.refusjonsinfo156()));
                }
            }

            oppdragsLinje150Liste.add(oppdragsLinje150);
        }
        return oppdragsLinje150Liste.stream()
            .sorted(Comparator.comparing(opp150 -> Long.parseLong(opp150.getDelytelseId())))
            .collect(Collectors.toList());
    }

    private no.nav.foreldrepenger.integrasjon.økonomistøtte.oppdrag.Refusjonsinfo156 mapRefusjonInfo156(Refusjonsinfo156Dto okoRefusjonsInfo156) {
        final var refusjonsinfo156 =
            objectFactory.createRefusjonsinfo156();

        refusjonsinfo156.setMaksDato(toXmlGregCal(okoRefusjonsInfo156.maksDato()));
        refusjonsinfo156.setDatoFom(toXmlGregCal(okoRefusjonsInfo156.datoFom()));
        refusjonsinfo156.setRefunderesId(okoRefusjonsInfo156.refunderesId());

        return refusjonsinfo156;
    }

    private Grad170 mapGrad170(UtbetalingsgradDto okoUtbetalingsgrad) {
        final var grad170 = objectFactory.createGrad170();

        grad170.setGrad(BigInteger.valueOf(okoUtbetalingsgrad.verdi()));
        grad170.setTypeGrad(TYPE_GRAD);

        return grad170;
    }

    private Attestant180 mapAttestant180(String saksbehId) {
        final var attestant180 =
            objectFactory.createAttestant180();

        attestant180.setAttestantId(saksbehId);

        return attestant180;
    }

    private XMLGregorianCalendar toXmlGregCal(LocalDate dato) {
        return dato != null ? DateUtil.convertToXMLGregorianCalendarRemoveTimezone(dato) : null;
    }
}
