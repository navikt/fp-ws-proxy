package no.nav.foreldrepenger.ws.proxy.api.simulering.mapper;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.KodeEndring;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.KodeEndringLinje;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.KodeFagområde;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.KodeKlassifik;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.KodeStatusLinje;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.LukketPeriode;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.Ompostering116Dto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.Oppdrag110Dto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.Oppdragslinje150Dto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.Refusjonsinfo156Dto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.SatsDto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.TypeSats;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.UtbetalingsgradDto;
import no.nav.foreldrepenger.ws.proxy.api.SyntetiskTestData;

public class OppdragTestdataGenerator {

    private OppdragTestdataGenerator() {
    }


    public static Oppdrag110Dto lagOppdragFPEnkel(LocalDate fom, LocalDate tom, boolean refusjon) {
        var oppdrag = oppdragslinje150Dto(
            fom,
            tom,
            KodeKlassifik.FPF_FRILANSER,
            TypeSats.DAG,
            refusjon ? lagRefusjon(SyntetiskTestData.SYNTETISK_FNR) : null
        );
        return lagOppdrag110Dto(refusjon ? KodeFagområde.FPREF : KodeFagområde.FP, null, List.of(oppdrag));
    }

    public static Oppdrag110Dto lagOppdragSVPEnkel(LocalDate fom, LocalDate tom) {
        var oppdrag = oppdragslinje150Dto(
            fom,
            tom,
            KodeKlassifik.FPF_FRILANSER,
            TypeSats.DAG,
            null
        );
        return lagOppdrag110Dto(KodeFagområde.SVP, null, List.of(oppdrag));
    }

    public static Oppdrag110Dto lagOppdragESEnkel(LocalDate fom, LocalDate tom) {
        var oppdrag = oppdragslinje150Dto(
            fom,
            tom,
            KodeKlassifik.ES_FØDSEL,
            TypeSats.ENG,
            null
        );
        return lagOppdrag110Dto(KodeFagområde.REFUTG, null, List.of(oppdrag));
    }

    public static Oppdrag110Dto lagOppdragPSBEnkel(LocalDate fom, LocalDate tom, boolean refusjon) {
        var oppdrag = oppdragslinje150DtoK9variant(
            fom,
            tom,
            KodeKlassifik.PSB_ARBEDISTAKER,
            TypeSats.DAG,
            refusjon ? lagRefusjon(SyntetiskTestData.SYNTETISK_FNR) : null
        );
        return lagOppdrag110DtoK9variant(refusjon ? KodeFagområde.PBREF : KodeFagområde.PB, null, List.of(oppdrag));
    }

    public static Oppdrag110Dto lagOppdragOMPEnkel(LocalDate fom, LocalDate tom, boolean refusjon) {
        var oppdrag = oppdragslinje150DtoK9variant(
            fom,
            tom,
            KodeKlassifik.OMP_ARBEDISTAKER,
            TypeSats.DAG7,
            refusjon ? lagRefusjon(SyntetiskTestData.SYNTETISK_FNR) : null
        );
        return lagOppdrag110DtoK9variant(refusjon ? KodeFagområde.OMREF : KodeFagområde.OM, null, List.of(oppdrag));
    }

    public static Oppdrag110Dto lagOppdragOMPRefusjonToPerioder() {
        LocalDate dag1 = LocalDate.of(2024, 2, 23);
        LocalDate dag2 = LocalDate.of(2024, 2, 27);
        LocalDate mai1 = LocalDate.of(2025, 5, 1);
        LocalDate mai31 = LocalDate.of(2025, 5, 31);

        String syntetiskOrgnr = "111111111";
        var linje1 = oppdragslinje150Dto(dag1, dag1, KodeKlassifik.OMP_REFUSJON_AG, TypeSats.DAG7, "F00BAR1-1-1", 1000, lagRefusjon(syntetiskOrgnr));
        var linje2 = oppdragslinje150Dto(dag2, dag2, KodeKlassifik.OMP_REFUSJON_AG, TypeSats.DAG7, "F00BAR1-1-2", 1000, lagRefusjon(syntetiskOrgnr));
        var linje3 = oppdragslinje150Dto(mai1, mai31, KodeKlassifik.OMP_FERIEPENGER_REFUSJON_AG, TypeSats.ENG, "F00BAR1-1-3", 204, lagRefusjon(syntetiskOrgnr));
        return lagOppdrag110DtoK9variant(KodeFagområde.PBREF, null, List.of(linje1, linje2, linje3));
    }

    public static Oppdrag110Dto lagOppdragMedOmposteringEnkel(KodeFagområde kodeFagområde, LocalDate fom, LocalDate tom) {
        var oppdragslinje150 = oppdragslinje150Dto(
            fom,
            tom,
            KodeKlassifik.SVP_ARBEDISTAKER,
            TypeSats.ENG,
            null
        );
        var ompostering116 = ompostering116Dto(false, LocalDate.of(2018, 5, 15));
        return lagOppdrag110Dto(kodeFagområde, ompostering116, List.of(oppdragslinje150));
    }


    public static Oppdrag110Dto lagOppdrag110Dto(KodeFagområde kodeFagområde, Ompostering116Dto ompostering116, List<Oppdragslinje150Dto> oppdragslinje150Dto) {
        return new Oppdrag110Dto(
            KodeEndring.NY,
            kodeFagområde,
            "130158784100",
            SyntetiskTestData.SYNTETISK_FNR,
            "Z123456",
            ompostering116,
            oppdragslinje150Dto
        );
    }

    public static Oppdrag110Dto lagOppdrag110DtoK9variant(KodeFagområde kodeFagområde, Ompostering116Dto ompostering116, List<Oppdragslinje150Dto> oppdragslinje150Dto) {
        return new Oppdrag110Dto(
            KodeEndring.NY,
            kodeFagområde,
            "F00BAR1-1",
            SyntetiskTestData.SYNTETISK_FNR,
            "Z123456",
            ompostering116,
            oppdragslinje150Dto
        );
    }

    public static Refusjonsinfo156Dto lagRefusjon(String refunderesTilId) {
        return new Refusjonsinfo156Dto(
            LocalDate.now().minusYears(2),
            refunderesTilId,
            LocalDate.now().minusYears(2).plusMonths(2)
        );
    }

    public static Ompostering116Dto ompostering116Dto(boolean ompostering, LocalDate datoOmposterFom) {
        return new Ompostering116Dto(
            ompostering,
            datoOmposterFom,
            "2018-08-16-15.36.55.543"
        );
    }

    public static Oppdragslinje150Dto oppdragslinje150Dto(LocalDate fom, LocalDate tom, KodeKlassifik kodeKlassifik,
                                                          TypeSats typeSats, Refusjonsinfo156Dto refusjon) {
        return oppdragslinje150Dto(fom, tom, kodeKlassifik, typeSats, "130158784100100", 738, refusjon);
    }

    public static Oppdragslinje150Dto oppdragslinje150DtoK9variant(LocalDate fom, LocalDate tom, KodeKlassifik kodeKlassifik,
                                                                   TypeSats typeSats, Refusjonsinfo156Dto refusjon) {
        return oppdragslinje150Dto(fom, tom, kodeKlassifik, typeSats, "F00BAR1-1-1", 738, refusjon);
    }

    public static Oppdragslinje150Dto oppdragslinje150Dto(LocalDate fom, LocalDate tom, KodeKlassifik kodeKlassifik,
                                                          TypeSats typeSats, String delytelseId, Integer sats, Refusjonsinfo156Dto refusjon) {
        return new Oppdragslinje150Dto(
            KodeEndringLinje.NY,
            "2018-08-16",
            delytelseId,
            kodeKlassifik,
            new LukketPeriode(fom, tom),
            SatsDto.valueOf(sats),
            typeSats,
            UtbetalingsgradDto.valueOf(100),
            KodeStatusLinje.OPPH,
            null,
            SyntetiskTestData.SYNTETISK_FNR,
            null,
            null,
            refusjon
        );
    }
}
