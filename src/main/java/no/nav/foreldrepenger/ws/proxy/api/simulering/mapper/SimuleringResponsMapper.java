package no.nav.foreldrepenger.ws.proxy.api.simulering.mapper;

import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;

import java.util.List;

import no.nav.foreldrepenger.kontrakter.simulering.respons.BeregningDto;
import no.nav.foreldrepenger.kontrakter.simulering.respons.BeregningStoppnivåDetaljerDto;
import no.nav.foreldrepenger.kontrakter.simulering.respons.BeregningStoppnivåDto;
import no.nav.foreldrepenger.kontrakter.simulering.respons.BeregningsPeriodeDto;
import no.nav.system.os.entiteter.beregningskjema.BeregningStoppnivaa;
import no.nav.system.os.entiteter.beregningskjema.BeregningStoppnivaaDetaljer;
import no.nav.system.os.entiteter.beregningskjema.BeregningsPeriode;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningResponse;

public class SimuleringResponsMapper {

    private SimuleringResponsMapper() {
    }

    public static List<BeregningDto> tilBeregningDtoListe(List<SimulerBeregningResponse> simulerBeregningResponse) {
        return safeStream(simulerBeregningResponse)
            .filter(SimuleringResponsMapper::erSimuleringTilstede)
            .map(SimuleringResponsMapper::tilBeregningDto)
            .toList();
    }

    private static boolean erSimuleringTilstede(SimulerBeregningResponse simulerBeregningResponse) {
        var response = simulerBeregningResponse.getResponse();
        return response != null && response.getSimulering() != null;
    }

    private static BeregningDto tilBeregningDto(SimulerBeregningResponse simulerBeregningResponse) {
        var simulering = simulerBeregningResponse.getResponse().getSimulering();
        return new BeregningDto.Builder()
            .gjelderId(simulering.getGjelderId())
            .gjelderNavn(simulering.getGjelderNavn())
            .datoBeregnet(simulering.getDatoBeregnet())
            .kodeFaggruppe(simulering.getKodeFaggruppe())
            .belop(simulering.getBelop())
            .beregningsPeriode(tilBeregningPeriodeDto(simulering.getBeregningsPeriode()))
            .build();
    }

    private static List<BeregningsPeriodeDto> tilBeregningPeriodeDto(List<BeregningsPeriode> beregningsPerioder) {
        return safeStream(beregningsPerioder)
            .map(SimuleringResponsMapper::tilBeregningPeriodeDto)
            .toList();
    }

    private static BeregningsPeriodeDto tilBeregningPeriodeDto(BeregningsPeriode beregningsPeriode) {
        return new BeregningsPeriodeDto(beregningsPeriode.getPeriodeFom(),
            beregningsPeriode.getPeriodeTom(),
            tilBeregningStoppNivåDto(beregningsPeriode.getBeregningStoppnivaa()));

    }

    private static List<BeregningStoppnivåDto> tilBeregningStoppNivåDto(List<BeregningStoppnivaa> beregningStoppnivaa) {
        return safeStream(beregningStoppnivaa)
            .map(SimuleringResponsMapper::tilBeregningStoppNivåDto)
            .toList();
    }

    private static BeregningStoppnivåDto tilBeregningStoppNivåDto(BeregningStoppnivaa stoppnivå) {
        return new BeregningStoppnivåDto.Builder()
            .kodeFagomraade(stoppnivå.getKodeFagomraade())
            .stoppNivaaId(stoppnivå.getStoppNivaaId())
            .behandlendeEnhet(stoppnivå.getBehandlendeEnhet())
            .oppdragsId(stoppnivå.getOppdragsId())
            .fagsystemId(stoppnivå.getFagsystemId())
            .kid(stoppnivå.getKid())
            .utbetalesTilId(stoppnivå.getUtbetalesTilId())
            .utbetalesTilNavn(stoppnivå.getUtbetalesTilNavn())
            .bilagsType(stoppnivå.getBilagsType())
            .forfall(stoppnivå.getForfall())
            .feilkonto(stoppnivå.isFeilkonto())
            .beregningStoppnivaaDetaljer(tilBeregningStoppNivåDetaljerDto(stoppnivå.getBeregningStoppnivaaDetaljer()))
            .build();
    }

    private static List<BeregningStoppnivåDetaljerDto> tilBeregningStoppNivåDetaljerDto(List<BeregningStoppnivaaDetaljer> beregningStoppnivaaDetaljer) {
        return safeStream(beregningStoppnivaaDetaljer)
            .map(SimuleringResponsMapper::tilBeregningStoppNivåDto)
            .toList();
    }

    private static BeregningStoppnivåDetaljerDto tilBeregningStoppNivåDto(BeregningStoppnivaaDetaljer detaljer) {
        return new BeregningStoppnivåDetaljerDto.Builder()
            .faktiskFom(detaljer.getFaktiskFom())
            .faktiskTom(detaljer.getFaktiskTom())
            .kontoStreng(detaljer.getKontoStreng())
            .behandlingskode(detaljer.getBehandlingskode())
            .belop(detaljer.getBelop())
            .trekkVedtakId(detaljer.getTrekkVedtakId())
            .stonadId(detaljer.getStonadId())
            .korrigering(detaljer.getKorrigering())
            .tilbakeforing(detaljer.isTilbakeforing())
            .linjeId(detaljer.getLinjeId())
            .sats(detaljer.getSats())
            .typeSats(detaljer.getTypeSats())
            .antallSats(detaljer.getAntallSats())
            .saksbehId(detaljer.getSaksbehId())
            .uforeGrad(detaljer.getUforeGrad())
            .kravhaverId(detaljer.getKravhaverId())
            .delytelseId(detaljer.getDelytelseId())
            .bostedsenhet(detaljer.getBostedsenhet())
            .skykldnerId(detaljer.getSkykldnerId())
            .klassekode(detaljer.getKlassekode())
            .klasseKodeBeskrivelse(detaljer.getKlasseKodeBeskrivelse())
            .typeKlasse(detaljer.getTypeKlasse())
            .klasseKodeBeskrivelse(detaljer.getTypeKlasseBeskrivelse())
            .refunderesOrgNr(detaljer.getRefunderesOrgNr())
            .build();
    }
}
