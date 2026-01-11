package no.nav.foreldrepenger.ws.proxy.api.simulering.mapper;

import tools.jackson.databind.json.JsonMapper;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.KodeEndringLinje;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.KodeFagområde;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.KodeKlassifik;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.LukketPeriode;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.OppdragskontrollDto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.Oppdragslinje150Dto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.SatsDto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.TypeSats;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.UtbetalingsgradDto;
import no.nav.foreldrepenger.ws.proxy.api.SyntetiskTestData;
import no.nav.foreldrepenger.ws.proxy.config.JacksonConfiguration;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningRequest;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.Oppdragslinje;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.OppdragMapperTest.verifiserAtMappingIkkeMisterNoeData;
import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.OppdragTestdataGenerator.lagOppdrag110Dto;
import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.OppdragTestdataGenerator.lagOppdragFPEnkel;
import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.OppdragTestdataGenerator.lagOppdragMedOmposteringEnkel;
import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.OppdragTestdataGenerator.lagOppdragSVPEnkel;
import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.OppdragTestdataGenerator.lagRefusjon;
import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.YtelseType.FP;
import static org.assertj.core.api.Assertions.assertThat;

class SimuleringRequestMapperTest {

    @Test
    void skal_sortere_etter_løpenummer_på_delytelse_id() {
        var om = new JacksonConfiguration().customObjectmapper();
        OppdragskontrollDto dto = om.readValue("""
                {
                  "behandlingId": "111",
                  "oppdrag": [
                    {
                      "kodeEndring": "UEND",
                      "kodeFagomrade": "PBREF",
                      "fagsystemId": "ABCDE-1",
                      "oppdragGjelderId": "11111111111",
                      "saksbehId": "Z000000",
                      "ompostering116": null,
                      "oppdragslinje150Liste": [
                        {
                          "kodeEndringLinje": "NY",
                          "vedtakId": "2025-01-01",
                          "delytelseId": "ABCDE-1-999",
                          "kodeKlassifik": "PNBSREFAG-IOP",
                          "vedtakPeriode": {
                            "fomDate": "2025-01-01",
                            "tomDate": "2025-01-01"
                          },
                          "sats": 1000,
                          "typeSats": "DAG",
                          "utbetalingsgrad": 100,
                          "kodeStatusLinje": null,
                          "datoStatusFom": null,
                          "utbetalesTilId": null,
                          "refDelytelseId": "ABCDE-1-998",
                          "refFagsystemId": "ABCDE-1",
                          "refusjonsinfo156": {
                            "maksDato": "2025-01-31",
                            "refunderesId": "00111111111",
                            "datoFom": "2025-01-01"
                          }
                        },
                        {
                          "kodeEndringLinje": "NY",
                          "vedtakId": "2025-01-01",
                          "delytelseId": "ABCDE-1-1000",
                          "kodeKlassifik": "PNBSREFAG-IOP",
                          "vedtakPeriode": {
                            "fomDate": "2025-01-02",
                            "tomDate": "2025-01-02"
                          },
                          "sats": 100,
                          "typeSats": "DAG",
                          "utbetalingsgrad": 10,
                          "kodeStatusLinje": null,
                          "datoStatusFom": null,
                          "utbetalesTilId": null,
                          "refDelytelseId": "ABCDE-1-999",
                          "refFagsystemId": "ABCDE-1",
                          "refusjonsinfo156": {
                            "maksDato": "2025-01-31",
                            "refunderesId": "00111111111",
                            "datoFom": "2025-01-01"
                          }
                        }
                      ]
                    }
                  ]
                }
                """,
            OppdragskontrollDto.class);

        List<SimulerBeregningRequest> simulerBeregningRequests = SimuleringRequestMapper.tilSimulerBeregingsRequester(dto, YtelseType.PSB, false);

        List<Oppdragslinje> oppdragslinjer = simulerBeregningRequests.get(0).getRequest().getOppdrag().getOppdragslinje();
        assertThat(oppdragslinjer.get(0).getDelytelseId()).isEqualTo("ABCDE-1-999");
        assertThat(oppdragslinjer.get(1).getDelytelseId()).isEqualTo("ABCDE-1-1000");
    }

    @Test
    void verifiserAtUnmarshallingAvXMLStrengIkkeMinsterNoeData() {
        var startTidspunkt = LocalDate.now().minusMonths(4);
        var oppdragslinje150_1 = new Oppdragslinje150Dto(
            KodeEndringLinje.NY,
            "2018-08-16",
            "135702910101100",
            KodeKlassifik.FPF_REFUSJON_AG,
            new LukketPeriode(startTidspunkt, startTidspunkt.plusDays(5)),
            SatsDto.valueOf(1177),
            TypeSats.DAG,
            UtbetalingsgradDto.valueOf(100),
            null,
            null,
            SyntetiskTestData.SYNTETISK_FNR,
            "135702910101100",
            "135702910101",
            lagRefusjon(SyntetiskTestData.SYNTETISK_FNR)
        );
        var oppdragslinje150_2 = new Oppdragslinje150Dto(
            KodeEndringLinje.NY,
            "2018-08-16",
            "135702910101101",
            KodeKlassifik.FPF_REFUSJON_AG,
            new LukketPeriode(startTidspunkt.plusDays(6), startTidspunkt.plusDays(6).plusWeeks(2)),
            SatsDto.valueOf(1177),
            TypeSats.DAG,
            UtbetalingsgradDto.valueOf(100),
            null,
            null,
            SyntetiskTestData.SYNTETISK_FNR,
            "135702910101101",
            "135702910101",
            lagRefusjon(SyntetiskTestData.SYNTETISK_FNR)
        );
        var oppdragslinje150_3 = new Oppdragslinje150Dto(
            KodeEndringLinje.NY,
            "2018-08-16",
            "135702910101102",
            KodeKlassifik.FPF_REFUSJON_AG,
            new LukketPeriode(startTidspunkt.plusMonths(1), startTidspunkt.plusMonths(1).plusWeeks(1)),
            SatsDto.valueOf(1177),
            TypeSats.DAG,
            UtbetalingsgradDto.valueOf(100),
            null,
            null,
            SyntetiskTestData.SYNTETISK_FNR,
            "135702910101102",
            "135702910101",
            lagRefusjon(SyntetiskTestData.SYNTETISK_FNR)
        );
        var oppdragslinje150_4 = new Oppdragslinje150Dto(
            KodeEndringLinje.NY,
            "2018-08-16",
            "135702910101103",
            KodeKlassifik.FPF_FERIEPENGER_AG,
            new LukketPeriode(startTidspunkt.plusMonths(6), startTidspunkt.plusMonths(7)),
            SatsDto.valueOf(2881),
            TypeSats.ENG,
            UtbetalingsgradDto.valueOf(100),
            null,
            null,
            SyntetiskTestData.SYNTETISK_FNR,
            "135702910101103",
            "135702910101",
            lagRefusjon(SyntetiskTestData.SYNTETISK_FNR)
        );
        var oppdrag110Dto = lagOppdrag110Dto(KodeFagområde.FPREF, null,
            List.of(oppdragslinje150_1, oppdragslinje150_2, oppdragslinje150_3, oppdragslinje150_4));
        var oppdragskontroll = new OppdragskontrollDto("1000202", List.of(oppdrag110Dto));
        var simuleringsrequest = SimuleringRequestMapper.tilSimulerBeregingsRequester(oppdragskontroll, null, false);

        assertThat(simuleringsrequest).hasSize(1);
        var simulerBeregningRequest = simuleringsrequest.get(0);
        var oppdrag = simulerBeregningRequest.getRequest().getOppdrag();

        verifiserAtMappingIkkeMisterNoeData(oppdrag110Dto, oppdrag);
    }

    @Test
    void verifiserAtSimuleringUtenInntrekkGjøresBareMedRiktigYtelseTypeSamtOmposteringLikNAlleOppdragsXMLerMatcher() {
        var oppdragFP = lagOppdragFPEnkel(LocalDate.now().minusMonths(2), LocalDate.now().minusMonths(1), false);
        var oppdragSVP = lagOppdragSVPEnkel(LocalDate.now().minusMonths(2), LocalDate.now().minusMonths(1));
        var oppdragskontroll = new OppdragskontrollDto("123456789", List.of(oppdragFP, oppdragSVP));
        var simuleringsrequest = SimuleringRequestMapper.tilSimulerBeregingsRequester(oppdragskontroll, FP, true);

        assertThat(simuleringsrequest).hasSize(1)
            .extracting(request -> request.getRequest().getOppdrag().getKodeEndring())
            .containsOnly("ENDR");
        assertThat(simuleringsrequest)
            .extracting(request -> request.getRequest().getOppdrag().getOmpostering().getOmPostering())
            .containsOnly("N");
        assertThat(simuleringsrequest.get(0).getRequest().getOppdrag().getKodeFagomraade()).isEqualTo(KodeFagområde.FP.name());
    }

    @Test
    void verifiserAtSimuleringUtenInntrekkGjøresBareMedRiktigYtelseTypeSamtOmposteringLikNBareEnXMLMatcher() {
        var oppdragFP = lagOppdragFPEnkel(LocalDate.now().minusMonths(2), LocalDate.now().minusMonths(1), false);
        var oppdragFPOmpostering = lagOppdragMedOmposteringEnkel(KodeFagområde.FP, LocalDate.now().minusMonths(2), LocalDate.now().minusMonths(1));
        var oppdragskontroll = new OppdragskontrollDto("123456789", List.of(oppdragFP, oppdragFPOmpostering));
        var simuleringsrequest = SimuleringRequestMapper.tilSimulerBeregingsRequester(oppdragskontroll, FP, true);

        assertThat(simuleringsrequest).hasSize(2)
            .extracting(request -> request.getRequest().getOppdrag().getKodeEndring())
            .containsOnly("ENDR");
        assertThat(simuleringsrequest)
            .extracting(request -> request.getRequest().getOppdrag().getOmpostering().getOmPostering())
            .containsOnly("N");
    }
}
