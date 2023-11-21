package no.nav.foreldrepenger.ws.proxy.api.simulering.mapper;

import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.KodeFagområde;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.KodeKlassifik;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.KodeStatusLinje;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.Oppdrag110Dto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.OppdragskontrollDto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.Oppdragslinje150Dto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.TypeSats;
import no.nav.system.os.entiteter.oppdragskjema.Attestant;
import no.nav.system.os.entiteter.oppdragskjema.Oppdragslinje;
import no.nav.system.os.entiteter.typer.simpletypes.FradragTillegg;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.Oppdrag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.OppdragMapper.DATE_TIME_FORMATTER;
import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.OppdragTestdataGenerator.lagOppdrag110Dto;
import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.OppdragTestdataGenerator.lagOppdragESEnkel;
import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.OppdragTestdataGenerator.lagOppdragFPEnkel;
import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.OppdragTestdataGenerator.lagOppdragMedOmposteringEnkel;
import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.OppdragTestdataGenerator.oppdragslinje150Dto;
import static org.assertj.core.api.Assertions.assertThat;

public class OppdragMapperTest {

    @Test
    void test_skalMappeFPOppdrag110TilOppdrag() {
        var oppdrag110Dto = lagOppdragFPEnkel(LocalDate.now().minusMonths(2), LocalDate.now().minusMonths(1), false);

        var oppdragXML = OppdragMapper.mapTilSimuleringOppdrag(oppdrag110Dto, "1000202");

        verifiserAtMappingIkkeMisterNoeData(oppdrag110Dto, oppdragXML);

    }

    @Test
    void test_skalMappeFPOppdrag110MedOmpostering116TilOppdrag() {
        var oppdrag110Dto = lagOppdragMedOmposteringEnkel(KodeFagområde.FP, LocalDate.now().minusMonths(2), LocalDate.now().minusMonths(1));
        var oppdragskontroll = new OppdragskontrollDto("1000202", List.of(oppdrag110Dto));

        var oppdragXML = OppdragMapper.mapTilSimuleringOppdrag(oppdrag110Dto, oppdragskontroll.behandlingId());

        verifiserAtMappingIkkeMisterNoeData(oppdrag110Dto, oppdragXML);
    }

    @Test
    void testMapVedtaksDataToOppdragES() {
        var oppdrag110Dto = lagOppdragESEnkel(LocalDate.now().minusMonths(2), LocalDate.now().minusMonths(1));
        var oppdragskontroll = new OppdragskontrollDto("1000202", List.of(oppdrag110Dto));

        var oppdragXML = OppdragMapper.mapTilSimuleringOppdrag(oppdrag110Dto, oppdragskontroll.behandlingId());

        verifiserAtMappingIkkeMisterNoeData(oppdrag110Dto, oppdragXML);
    }

    @Test
    void testMapVedtaksDataToOppdragFPNårOpp150IkkeErSortert() {
        var startTidspunkt = LocalDate.now().minusMonths(2);
        var oppdragslinje150_1 = oppdragslinje150Dto(
            startTidspunkt, startTidspunkt.plusDays(5),
            KodeKlassifik.FPF_FRILANSER,
            TypeSats.DAG,
            "1",
            1234,
            null
            );
        var oppdragslinje150_2 = oppdragslinje150Dto(
            startTidspunkt.plusWeeks(1), startTidspunkt.plusMonths(1),
            KodeKlassifik.FPF_FRILANSER,
            TypeSats.DAG,
            "2",
            324,
            null
            );
        var oppdrag110DtoUsortert = lagOppdrag110Dto(KodeFagområde.FP, null, List.of(oppdragslinje150_2, oppdragslinje150_1));
        var oppdragXMLUsortertInput = OppdragMapper.mapTilSimuleringOppdrag(oppdrag110DtoUsortert, "1234");

        // Assert
        var delytelseIdFraOpp150GenerertListXML = oppdragXMLUsortertInput.getOppdragslinje()
            .stream()
            .map(Oppdragslinje::getDelytelseId)
            .collect(Collectors.toList());
        var ikkeSortertDelytelseIdFraOpp150List = oppdrag110DtoUsortert.oppdragslinje150Liste()
            .stream()
            .map(Oppdragslinje150Dto::delytelseId)
            .map(Object::toString)
            .collect(Collectors.toList());

        assertThat(delytelseIdFraOpp150GenerertListXML).isNotEqualTo(ikkeSortertDelytelseIdFraOpp150List);

        var sortertDelytelseIdFraOpp150List = ikkeSortertDelytelseIdFraOpp150List.stream()
            .sorted(Comparator.comparing(Long::parseLong))
            .collect(Collectors.toList());

        assertThat(delytelseIdFraOpp150GenerertListXML).isEqualTo(sortertDelytelseIdFraOpp150List);
    }





    public static void verifiserAtMappingIkkeMisterNoeData(Oppdrag110Dto oppdrag110Dto, Oppdrag oppdrag110XML) {
        assertThat(oppdrag110Dto.kodeEndring().name()).isEqualTo(oppdrag110XML.getKodeEndring());
        var kodeFagområde = oppdrag110Dto.kodeFagomrade();
        assertThat(kodeFagområde.name()).isEqualTo(oppdrag110XML.getKodeFagomraade());
        assertThat(oppdrag110Dto.fagsystemId()).hasToString(oppdrag110XML.getFagsystemId());
        assertThat(oppdrag110Dto.oppdragGjelderId()).isEqualTo(oppdrag110XML.getOppdragGjelderId());
        assertThat(oppdrag110Dto.saksbehId()).isEqualTo(oppdrag110XML.getSaksbehId());
        assertThat(oppdrag110XML.getUtbetFrekvens()).isEqualTo("MND");
        if (oppdrag110Dto.ompostering116() == null) {
            assertThat(oppdrag110XML.getOmpostering()).isNull();
        } else {
            assertThat(oppdrag110Dto.ompostering116()).isNotNull();
            assertThat(oppdrag110XML.getOmpostering()).isNotNull();

            assertThat(oppdrag110Dto.ompostering116().omPostering()).isFalse();
            assertThat(oppdrag110XML.getOmpostering().getOmPostering()).isEqualTo("N");

            assertThat(oppdrag110Dto.saksbehId()).isEqualTo(oppdrag110XML.getOmpostering().getSaksbehId());
            assertThat(oppdrag110Dto.ompostering116().datoOmposterFom().format(DATE_TIME_FORMATTER))
                .isEqualTo(oppdrag110XML.getOmpostering().getDatoOmposterFom())
                .isNotNull();

            assertThat(oppdrag110Dto.ompostering116().tidspktReg())
                .isEqualTo(oppdrag110XML.getOmpostering().getTidspktReg())
                .isNotNull();
        }

        var oppdragslinje150Dtoer = oppdrag110Dto.oppdragslinje150Liste();
        var oppdragslinjeXMLer = oppdrag110XML.getOppdragslinje();
        assertThat(oppdragslinje150Dtoer)
            .hasSameSizeAs(oppdragslinjeXMLer)
            .hasSizeGreaterThan(0);

        for (int i = 0; i < oppdragslinje150Dtoer.size(); i++) {
            var oppdragslinje150Dto = oppdragslinje150Dtoer.get(i);
            var oppdragslinjeXML = oppdragslinjeXMLer.get(i);
            assertThat(oppdragslinje150Dto.kodeEndringLinje().name()).isEqualTo(oppdragslinjeXML.getKodeEndringLinje());
            assertThat(oppdragslinje150Dto.vedtakId()).isEqualTo(oppdragslinjeXML.getVedtakId());
            assertThat(oppdragslinje150Dto.delytelseId()).hasToString(oppdragslinjeXML.getDelytelseId());
            assertThat(oppdragslinje150Dto.kodeKlassifik().getKode()).isEqualTo(oppdragslinjeXML.getKodeKlassifik());
            assertThat(oppdragslinje150Dto.getDatoVedtakFom().format(DATE_TIME_FORMATTER)).isEqualTo(oppdragslinjeXML.getDatoVedtakFom());
            assertThat(oppdragslinje150Dto.getDatoVedtakTom().format(DATE_TIME_FORMATTER)).isEqualTo(oppdragslinjeXML.getDatoVedtakTom());
            assertThat(oppdragslinje150Dto.sats().verdi()).isEqualTo(oppdragslinjeXML.getSats().intValue());
            assertThat(oppdragslinje150Dto.typeSats().name()).isEqualTo(oppdragslinjeXML.getTypeSats());
            assertThat(oppdragslinje150Dto.utbetalesTilId())
                .isEqualTo(oppdragslinjeXML.getUtbetalesTilId())
                .isNotNull();

            assertThat(oppdragslinjeXML.getAttestant()).hasSize(1)
                .extracting(Attestant::getAttestantId)
                .containsExactly(oppdrag110Dto.saksbehId());
            assertThat(oppdragslinjeXML.getFradragTillegg()).isEqualTo(FradragTillegg.T);
            assertThat(oppdragslinjeXML.getBrukKjoreplan()).isEqualTo("N");



            if (oppdragslinje150Dto.kodeStatusLinje() == null) {
                assertThat(oppdragslinjeXML.getKodeStatusLinje()).isNull();
            } else {
                assertThat(oppdragslinje150Dto.kodeStatusLinje().name())
                    .isEqualTo(oppdragslinjeXML.getKodeStatusLinje().name())
                    .isEqualTo(KodeStatusLinje.OPPH.name());
            }

            if (oppdragslinje150Dto.datoStatusFom() == null) {
                assertThat(oppdragslinjeXML.getDatoStatusFom()).isNull();
            } else {
                assertThat(oppdragslinje150Dto.datoStatusFom().format(DATE_TIME_FORMATTER))
                    .isEqualTo(oppdragslinjeXML.getDatoStatusFom());
            }

            if (oppdragslinje150Dto.refDelytelseId() == null) {
                assertThat(oppdragslinjeXML.getRefDelytelseId()).isNull();
            } else {
                assertThat(oppdragslinje150Dto.refDelytelseId())
                    .hasToString(oppdragslinjeXML.getRefDelytelseId());
            }

            if (oppdragslinje150Dto.refFagsystemId() == null) {
                assertThat(oppdragslinjeXML.getRefFagsystemId()).isNull();
            } else {
                assertThat(oppdragslinje150Dto.refFagsystemId())
                    .hasToString(oppdragslinjeXML.getRefFagsystemId());
            }

            if (kodeFagområde.gjelderForeldrepenger()) {
                assertThat(oppdragslinjeXML.getGrad()).hasSize(1);
                assertThat(oppdragslinje150Dto.utbetalingsgrad().verdi()).isEqualTo(oppdragslinjeXML.getGrad().get(0).getGrad().intValue());

                if (!kodeFagområde.gjelderRefusjonTilArbeidsgiver() && oppdragslinje150Dto.refusjonsinfo156() == null) {
                    assertThat(oppdragslinjeXML.getRefusjonsInfo()).isNull();
                } else {
                    var refusjonsinfo156Dto = oppdragslinje150Dto.refusjonsinfo156();
                    var refusjonsInfoXML = oppdragslinjeXML.getRefusjonsInfo();
                    assertThat(refusjonsinfo156Dto.refunderesId()).isEqualTo(refusjonsInfoXML.getRefunderesId());
                    assertThat(refusjonsinfo156Dto.datoFom().format(DATE_TIME_FORMATTER)).isEqualTo(refusjonsInfoXML.getDatoFom());
                    assertThat(refusjonsinfo156Dto.maksDato().format(DATE_TIME_FORMATTER)).isEqualTo(refusjonsInfoXML.getMaksDato());
                }
            }
        }
    }
}
