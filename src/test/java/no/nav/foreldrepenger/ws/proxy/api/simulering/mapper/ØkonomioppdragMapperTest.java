package no.nav.foreldrepenger.ws.proxy.api.simulering.mapper;

import static no.nav.foreldrepenger.kontrakter.simulering.request.KodeFagområde.FP;
import static no.nav.foreldrepenger.kontrakter.simulering.request.KodeFagområde.FPREF;
import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.ØkonomistøtteUtils.tilSpesialkodetDatoOgKlokkeslett;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.integrasjon.økonomistøtte.oppdrag.Oppdrag;
import no.nav.foreldrepenger.integrasjon.økonomistøtte.oppdrag.OppdragsLinje150;
import no.nav.foreldrepenger.integrasjon.økonomistøtte.oppdrag.TfradragTillegg;
import no.nav.foreldrepenger.integrasjon.økonomistøtte.oppdrag.TkodeStatusLinje;
import no.nav.foreldrepenger.kontrakter.simulering.request.KodeEndring;
import no.nav.foreldrepenger.kontrakter.simulering.request.KodeEndringLinje;
import no.nav.foreldrepenger.kontrakter.simulering.request.KodeFagområde;
import no.nav.foreldrepenger.kontrakter.simulering.request.KodeKlassifik;
import no.nav.foreldrepenger.kontrakter.simulering.request.KodeStatusLinje;
import no.nav.foreldrepenger.kontrakter.simulering.request.LukketPeriode;
import no.nav.foreldrepenger.kontrakter.simulering.request.Ompostering116Dto;
import no.nav.foreldrepenger.kontrakter.simulering.request.Oppdrag110Dto;
import no.nav.foreldrepenger.kontrakter.simulering.request.OppdragskontrollDto;
import no.nav.foreldrepenger.kontrakter.simulering.request.Oppdragslinje150Dto;
import no.nav.foreldrepenger.kontrakter.simulering.request.Refusjonsinfo156Dto;
import no.nav.foreldrepenger.kontrakter.simulering.request.SatsDto;
import no.nav.foreldrepenger.kontrakter.simulering.request.TypeSats;
import no.nav.foreldrepenger.kontrakter.simulering.request.UtbetalingsgradDto;
import no.nav.foreldrepenger.ws.proxy.util.DateUtil;

public class ØkonomioppdragMapperTest {
    private static final String REFUNDERES_ID = "123456789";
    private static final Long BEHANDLING_ID = 154L;

    private static final String TYPE_ENHET = "BOS";
    private static final String ENHET = "8020";
    private static final LocalDate DATO_ENHET_FOM = LocalDate.of(1900, 1, 1);
    private static final String FRADRAG_TILLEGG = "T";
    private static final String BRUK_KJØREPLAN = "N";
    private static final String TYPE_GRAD = "UFOR";
    private static final String KODE_AKSJON = "1";
    private static final String UTBET_FREKVENS = "MND";

    private ØkonomioppdragMapper økonomioppdragMapper;

    @BeforeEach
    public void setup() {
        økonomioppdragMapper = new ØkonomioppdragMapper();
    }

    @Test
    void testMapVedtaksDataToOppdragES() {
        var oppdrag110 = opprettOppdrag110(false);
        verifyMapVedtaksDataToOppdrag(oppdrag110, false);
    }

    @Test
    void testMapVedtaksDataToOppdragFP() {
        var oppdrag110 = opprettOppdrag110(true);
        verifyMapVedtaksDataToOppdrag(oppdrag110, true);
    }

    @Test
    void testMapVedtaksDataToOppdragFPNårOpp150IkkeErSortert() {
        //Arrange
        var oppdrag110List = opprettOppdrag110(true, false, false);
        List<Oppdrag> oppdragGenerertList = new ArrayList<>();

        //Act
        oppdrag110List.forEach(opp110 ->
            oppdragGenerertList.add(økonomioppdragMapper.mapVedtaksDataToOppdrag(opp110, BEHANDLING_ID)));

        //Assert
        for (var i = 0; i < oppdrag110List.size(); i++) {
            var oppdrag110Generert = oppdragGenerertList.get(i).getOppdrag110();
            var oppdrag110 = oppdrag110List.get(i);
            var delytelseIdFraOpp150GenerertList = oppdrag110Generert.getOppdragsLinje150()
                .stream()
                .map(OppdragsLinje150::getDelytelseId)
                .collect(Collectors.toList());
            var ikkeSortertDelytelseIdFraOpp150List = oppdrag110.oppdragslinje150Liste()
                .stream()
                .map(Oppdragslinje150Dto::delytelseId)
                .map(Object::toString)
                .collect(Collectors.toList());

            assertThat(delytelseIdFraOpp150GenerertList).isNotEqualTo(ikkeSortertDelytelseIdFraOpp150List);

            var sortertDelytelseIdFraOpp150List = ikkeSortertDelytelseIdFraOpp150List.stream()
                .sorted(Comparator.comparing(Long::parseLong))
                .collect(Collectors.toList());

            assertThat(delytelseIdFraOpp150GenerertList).isEqualTo(sortertDelytelseIdFraOpp150List);
        }
    }

    @Test
    void generer_xml_for_oppdrag110_ES() {
        var oppdragskontroll = new OppdragskontrollDto(BEHANDLING_ID, opprettOppdrag110(false));
        var oppdragXmlListe = økonomioppdragMapper.generateOppdragXML(oppdragskontroll);
        assertThat(oppdragXmlListe).hasSize(1);
        assertThat(oppdragXmlListe.get(0)).isNotNull();
    }

    @Test
    void generer_xml_for_oppdrag110_FP() {
        var oppdragskontroll = new OppdragskontrollDto(BEHANDLING_ID, opprettOppdrag110(true));
        var oppdragXmlListe = økonomioppdragMapper.generateOppdragXML(oppdragskontroll);
        assertThat(oppdragXmlListe).hasSize(2);
        assertThat(oppdragXmlListe.get(0)).isNotNull();
        assertThat(oppdragXmlListe.get(1)).isNotNull();
    }

    @Test
    public void mapperOmpostering116() {
        var oppdrag110List = opprettOppdrag110(true, true, true);
        List<Oppdrag> oppdragGenerertList = new ArrayList<>();

        //Act
        oppdrag110List.forEach(opp110 ->
            oppdragGenerertList.add(økonomioppdragMapper.mapVedtaksDataToOppdrag(opp110, BEHANDLING_ID)));

        var oppdrag = oppdragGenerertList.stream().filter(o -> o.getOppdrag110().getKodeFagomraade().equals("FP")).findFirst();
        assertThat(oppdrag).isPresent();

        var ompostering116 = oppdrag.get().getOppdrag110().getOmpostering116();
        assertThat(ompostering116).isNotNull();
        assertThat(ompostering116.getOmPostering()).isEqualTo("J");
    }


    //    ---------------------------------------------

    private List<Oppdrag110Dto> opprettOppdrag110(boolean gjelderFP) {
        return opprettOppdrag110(gjelderFP, true, false);
    }


    private List<Oppdrag110Dto> opprettOppdrag110(boolean gjelderFP, boolean erOppdragslinje150Sortert, boolean erOmpostering) {
        return buildOppdrag110(gjelderFP, erOppdragslinje150Sortert, erOmpostering);
    }

    private void verifyMapVedtaksDataToOppdrag(List<Oppdrag110Dto> oppdrag110Liste, boolean gjelderFP) {

        for (var oppdrag110 : oppdrag110Liste) {
            var oppdrag = økonomioppdragMapper.mapVedtaksDataToOppdrag(oppdrag110, BEHANDLING_ID);

            var oppdrag110Generert = oppdrag.getOppdrag110();
            assertThat(oppdrag110Generert.getKodeAksjon()).isEqualTo(KODE_AKSJON);
            assertThat(oppdrag110Generert.getKodeEndring()).isEqualTo(oppdrag110.kodeEndring().name());
            assertThat(oppdrag110Generert.getKodeFagomraade()).isEqualTo(oppdrag110.kodeFagomrade().name());
            assertThat(oppdrag110Generert.getUtbetFrekvens()).isEqualTo(UTBET_FREKVENS);

            var avstemming115Generert = oppdrag110Generert.getAvstemming115();
            assertThat(avstemming115Generert.getKodeKomponent()).isEqualTo(ØkonomiKodekomponent.VLFP.name());
            assertThat(avstemming115Generert.getNokkelAvstemming()).isEqualTo(oppdrag110.nøkkelAvstemming());

            var oppdragsEnhet120Generert = oppdrag110Generert.getOppdragsEnhet120().get(0);

            assertThat(oppdragsEnhet120Generert.getTypeEnhet()).isEqualTo(TYPE_ENHET);
            assertThat(oppdragsEnhet120Generert.getEnhet()).isEqualTo(ENHET);
            assertThat(oppdragsEnhet120Generert.getDatoEnhetFom()).isEqualTo(DateUtil.convertToXMLGregorianCalendarRemoveTimezone(DATO_ENHET_FOM));

            var oppdragsLinje150GenerertListe = oppdrag110Generert.getOppdragsLinje150();

            var ix = 0;
            for (var oppdragsLinje150Generert : oppdragsLinje150GenerertListe) {
                var oppdragslinje150 = oppdrag110.oppdragslinje150Liste().get(ix);
                assertThat(oppdragsLinje150Generert.getKodeEndringLinje()).isEqualTo(oppdragslinje150.kodeEndringLinje().name());
                assertThat(oppdragsLinje150Generert.getKodeStatusLinje()).isEqualTo(TkodeStatusLinje.fromValue(oppdragslinje150.kodeStatusLinje().name()));
                assertThat(oppdragsLinje150Generert.getDatoStatusFom()).isEqualTo(DateUtil.convertToXMLGregorianCalendarRemoveTimezone(oppdragslinje150.datoStatusFom()));
                assertThat(oppdragsLinje150Generert.getVedtakId()).isEqualTo(String.valueOf(oppdragslinje150.vedtakId()));
                assertThat(oppdragsLinje150Generert.getDelytelseId()).isEqualTo(String.valueOf(oppdragslinje150.delytelseId()));
                assertThat(oppdragsLinje150Generert.getKodeKlassifik()).isEqualTo(oppdragslinje150.kodeKlassifik().getKode());
                assertThat(oppdragsLinje150Generert.getDatoVedtakFom()).isEqualTo(DateUtil.convertToXMLGregorianCalendarRemoveTimezone(oppdragslinje150.getDatoVedtakFom()));
                assertThat(oppdragsLinje150Generert.getDatoVedtakTom()).isEqualTo(DateUtil.convertToXMLGregorianCalendarRemoveTimezone(oppdragslinje150.getDatoVedtakTom()));
                assertThat(oppdragsLinje150Generert.getSats()).isEqualTo(BigDecimal.valueOf(oppdragslinje150.sats().verdi()));
                assertThat(oppdragsLinje150Generert.getFradragTillegg()).isEqualTo(TfradragTillegg.fromValue(FRADRAG_TILLEGG));
                assertThat(oppdragsLinje150Generert.getTypeSats()).isEqualTo(oppdragslinje150.typeSats().name());
                assertThat(oppdragsLinje150Generert.getBrukKjoreplan()).isEqualTo(BRUK_KJØREPLAN);
                assertThat(oppdragsLinje150Generert.getSaksbehId()).isEqualTo(oppdrag110.saksbehId());
                assertThat(oppdragsLinje150Generert.getUtbetalesTilId()).isEqualTo(oppdragslinje150.utbetalesTilId());
                assertThat(oppdragsLinje150Generert.getHenvisning()).isEqualTo(String.valueOf(BEHANDLING_ID));
                if (!gjelderFP) {
                    assertThat(oppdragsLinje150Generert.getRefFagsystemId()).isNull();
                    assertThat(oppdragsLinje150Generert.getRefDelytelseId()).isNull();
                }

                var attestant180GenerertListe = oppdragsLinje150Generert.getAttestant180();
                for (var attestant180Generert : attestant180GenerertListe) {
                    assertThat(attestant180Generert.getAttestantId()).isEqualTo(oppdrag110.saksbehId());
                }

                if (gjelderFP) {
                    var grad170GenerertListe = oppdragsLinje150Generert.getGrad170();
                    for (var grad170Generert : grad170GenerertListe) {
                        var utbetalingsgrad = oppdragslinje150.utbetalingsgrad();
                        assertThat(grad170Generert.getGrad()).isEqualTo(BigInteger.valueOf(utbetalingsgrad.verdi()));
                        assertThat(grad170Generert.getTypeGrad()).isEqualTo(TYPE_GRAD);
                    }

                    var refusjonsinfo156Generert = oppdragsLinje150Generert.getRefusjonsinfo156();
                    var refusjonsinfo156Opt = Optional.ofNullable(oppdragslinje150.refusjonsinfo156());
                    refusjonsinfo156Opt.ifPresent(refusjonsinfo156 -> {
                        assertThat(refusjonsinfo156Generert.getRefunderesId()).isEqualTo(refusjonsinfo156.refunderesId());
                        assertThat(refusjonsinfo156Generert.getDatoFom()).isEqualTo(DateUtil.convertToXMLGregorianCalendarRemoveTimezone(refusjonsinfo156.datoFom()));
                        assertThat(refusjonsinfo156Generert.getMaksDato()).isEqualTo(DateUtil.convertToXMLGregorianCalendarRemoveTimezone(refusjonsinfo156.maksDato()));
                    });
                }
                ix++;
            }
        }
    }


    private Refusjonsinfo156Dto buildRefusjonsinfo156() {
        return new Refusjonsinfo156Dto(
            LocalDate.now(),
            REFUNDERES_ID,
            LocalDate.now()
        );
    }

    private List<Long> opprettDelytelseIdList(boolean gjelderFP, boolean erOppdragslinje150Sortert) {
        if (!gjelderFP) {
            return Collections.singletonList(1L);
        }
        return erOppdragslinje150Sortert ? List.of(1L, 2L) : List.of(2L, 1L);
    }

    private List<Oppdragslinje150Dto> buildOppdragslinje150(KodeFagområde kodeFagområde, List<Long> delytelseIdList, boolean gjelderFP) {
        List<Oppdragslinje150Dto> opp150Liste = new ArrayList<>();
        for (var delytelseId : delytelseIdList) {
            var oppdragslinje150Dto = oppdragslinje150Dto(delytelseId,
                gjelderFP ? finnKodeKlassifikVerdi(kodeFagområde) : KodeKlassifik.ES_FØDSEL,
                null,
                gjelderFP ? TypeSats.DAG : TypeSats.ENG,
                gjelderFP && kodeFagområde.equals(FPREF));
            opp150Liste.add(oppdragslinje150Dto);
        }
        if (gjelderFP) {
            var oppdragslinje150Feriepenger =  oppdragslinje150Dto(3L,
                kodeFagområde.equals(FP) ? KodeKlassifik.FERIEPENGER_BRUKER : KodeKlassifik.FPF_FERIEPENGER_AG,
                UtbetalingsgradDto.valueOf(100),
                TypeSats.ENG,
                kodeFagområde.equals(FPREF)
            );
            opp150Liste.add(oppdragslinje150Feriepenger);
        }
        return opp150Liste;
    }

    private Oppdragslinje150Dto oppdragslinje150Dto(Long delytelseId, KodeKlassifik kodeKlassifik, UtbetalingsgradDto utbetalingsgrad, TypeSats typeSats, boolean refusjon) {
        return new Oppdragslinje150Dto(
            KodeEndringLinje.ENDR,
            "345",
            delytelseId,
            kodeKlassifik,
            new LukketPeriode(LocalDate.now(), LocalDate.now()),
            SatsDto.valueOf(1122L),
            typeSats,
            utbetalingsgrad,
            KodeStatusLinje.OPPH,
            LocalDate.now(),
            "123456789",
            null,
            null,
            refusjon ? buildRefusjonsinfo156() : null);
    }


    private KodeKlassifik finnKodeKlassifikVerdi(KodeFagområde kodeFagområde) {
        if (kodeFagområde.equals(KodeFagområde.FP)) {
            return KodeKlassifik.FPF_ARBEIDSTAKER;
        }
        return KodeKlassifik.FPF_REFUSJON_AG;
    }


    private List<Oppdrag110Dto> buildOppdrag110(boolean gjelderFP, boolean erOppdragslinje150Sortert, boolean erOmpostering) {
        List<Oppdrag110Dto> oppdrag110Liste = new ArrayList<>();
        var kodeFagområde1 = gjelderFP ? FP : KodeFagområde.REFUTG;
        var oppdrag110_1 = new Oppdrag110Dto(
            KodeEndring.NY,
            kodeFagområde1,
            44L,
            "12345678901",
            "J5624215",
            tilSpesialkodetDatoOgKlokkeslett(LocalDateTime.now()),
            erOmpostering ? new Ompostering116Dto(true, LocalDate.now(), tilSpesialkodetDatoOgKlokkeslett(LocalDateTime.now())) : null,
            buildOppdragslinje150(kodeFagområde1, opprettDelytelseIdList(gjelderFP, erOppdragslinje150Sortert), gjelderFP)
        );
        oppdrag110Liste.add(oppdrag110_1);

        if (gjelderFP) {
            var kodefagområde2 = KodeFagområde.REFUTG;
            var oppdrag110_2 = new Oppdrag110Dto(
                KodeEndring.NY,
                kodefagområde2,
                55L,
                "12345678901",
                "J5624215",
                tilSpesialkodetDatoOgKlokkeslett(LocalDateTime.now()),
                null,
                buildOppdragslinje150(kodefagområde2, opprettDelytelseIdList(gjelderFP, erOppdragslinje150Sortert), gjelderFP)
            );
            oppdrag110Liste.add(oppdrag110_2);
        }

        return oppdrag110Liste;
    }

}
