package no.nav.foreldrepenger.ws.proxy.api.simulering.mapper;

import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.YtelseType.FP;
import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.OppdragMapperTest.verifiserAtMappingIkkeMisterNoeData;
import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.OppdragTestdataGenerator.lagOppdrag110Dto;
import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.OppdragTestdataGenerator.lagOppdragFPEnkel;
import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.OppdragTestdataGenerator.lagOppdragMedOmposteringEnkel;
import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.OppdragTestdataGenerator.lagOppdragSVPEnkel;
import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.OppdragTestdataGenerator.lagRefusjon;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.KodeEndringLinje;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.KodeFagområde;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.KodeKlassifik;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.LukketPeriode;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.OppdragskontrollDto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.Oppdragslinje150Dto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.SatsDto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.TypeSats;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.UtbetalingsgradDto;

class SimuleringRequestMapperTest {

    @Test
    void verifiserAtUnmarshallingAvXMLStrengIkkeMinsterNoeData() {
        var startTidspunkt = LocalDate.now().minusMonths(4);
        var oppdragslinje150_1 = new Oppdragslinje150Dto(
            KodeEndringLinje.NY,
            "2018-08-16",
            135702910101100L,
            KodeKlassifik.FPF_REFUSJON_AG,
            new LukketPeriode(startTidspunkt, startTidspunkt.plusDays(5)),
            SatsDto.valueOf(1177),
            TypeSats.DAG,
            UtbetalingsgradDto.valueOf(100),
            null,
            null,
            "15088011020",
            135702910101100L,
            135702910101L,
            lagRefusjon("12345678910")
        );
        var oppdragslinje150_2 = new Oppdragslinje150Dto(
            KodeEndringLinje.NY,
            "2018-08-16",
            135702910101101L,
            KodeKlassifik.FPF_REFUSJON_AG,
            new LukketPeriode(startTidspunkt.plusDays(6), startTidspunkt.plusDays(6).plusWeeks(2)),
            SatsDto.valueOf(1177),
            TypeSats.DAG,
            UtbetalingsgradDto.valueOf(100),
            null,
            null,
            "15088011020",
            135702910101101L,
            135702910101L,
            lagRefusjon("12345678910")
        );
        var oppdragslinje150_3 = new Oppdragslinje150Dto(
            KodeEndringLinje.NY,
            "2018-08-16",
            135702910101102L,
            KodeKlassifik.FPF_REFUSJON_AG,
            new LukketPeriode(startTidspunkt.plusMonths(1), startTidspunkt.plusMonths(1).plusWeeks(1)),
            SatsDto.valueOf(1177),
            TypeSats.DAG,
            UtbetalingsgradDto.valueOf(100),
            null,
            null,
            "15088011020",
            135702910101102L,
            135702910101L,
            lagRefusjon("12345678910")
        );
        var oppdragslinje150_4 = new Oppdragslinje150Dto(
            KodeEndringLinje.NY,
            "2018-08-16",
            135702910101103L,
            KodeKlassifik.FPF_FERIEPENGER_AG,
            new LukketPeriode(startTidspunkt.plusMonths(6), startTidspunkt.plusMonths(7)),
            SatsDto.valueOf(2881),
            TypeSats.ENG,
            UtbetalingsgradDto.valueOf(100),
            null,
            null,
            "15088011020",
            135702910101103L,
            135702910101L,
            lagRefusjon("12345678910")
        );
        var oppdrag110Dto = lagOppdrag110Dto(KodeFagområde.FPREF, null,
            List.of(oppdragslinje150_1, oppdragslinje150_2, oppdragslinje150_3, oppdragslinje150_4));
        var oppdragskontroll = new OppdragskontrollDto(1000202L, List.of(oppdrag110Dto));
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
        var oppdragskontroll = new OppdragskontrollDto(123456789L, List.of(oppdragFP, oppdragSVP));
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
        var oppdragskontroll = new OppdragskontrollDto(123456789L, List.of(oppdragFP, oppdragFPOmpostering));
        var simuleringsrequest = SimuleringRequestMapper.tilSimulerBeregingsRequester(oppdragskontroll, FP, true);

        assertThat(simuleringsrequest).hasSize(2)
            .extracting(request -> request.getRequest().getOppdrag().getKodeEndring())
            .containsOnly("ENDR");
        assertThat(simuleringsrequest)
            .extracting(request -> request.getRequest().getOppdrag().getOmpostering().getOmPostering())
            .containsOnly("N");
    }
}
