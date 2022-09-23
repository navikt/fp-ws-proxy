package no.nav.foreldrepenger.ws.proxy.api.simulering.mapper;

import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;

import java.util.List;

import no.nav.foreldrepenger.ws.proxy.api.simulering.dto.BeregningDto;
import no.nav.foreldrepenger.ws.proxy.api.simulering.dto.BeregningStoppnivåDetaljerDto;
import no.nav.foreldrepenger.ws.proxy.api.simulering.dto.BeregningStoppnivåDto;
import no.nav.foreldrepenger.ws.proxy.api.simulering.dto.BeregningsPeriodeDto;
import no.nav.system.os.entiteter.beregningskjema.BeregningStoppnivaa;
import no.nav.system.os.entiteter.beregningskjema.BeregningStoppnivaaDetaljer;
import no.nav.system.os.entiteter.beregningskjema.BeregningsPeriode;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningResponse;

public class SimuleringResponsMapper {

    private SimuleringResponsMapper() {
    }

    public static List<BeregningDto> tilBeregningDtoListe(List<SimulerBeregningResponse> simulerBeregningResponse) {
        return safeStream(simulerBeregningResponse)
            .map(SimuleringResponsMapper::tilBeregningDto)
            .toList();
    }

    private static BeregningDto tilBeregningDto(SimulerBeregningResponse simulerBeregningResponse) {
        var response = simulerBeregningResponse.getResponse();
        if (response == null || response.getSimulering() == null) {
            return new BeregningDto();
        }
        var simulering = response.getSimulering();
        return new BeregningDto(simulering.getGjelderId(),
            simulering.getGjelderNavn(),
            simulering.getDatoBeregnet(),
            simulering.getKodeFaggruppe(),
            simulering.getBelop(),
            tilBeregningPeriodeDto(simulering.getBeregningsPeriode()));
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
        return new BeregningStoppnivåDto(
            stoppnivå.getKodeFagomraade(),
            stoppnivå.getStoppNivaaId(),
            stoppnivå.getBehandlendeEnhet(),
            stoppnivå.getOppdragsId(),
            stoppnivå.getFagsystemId(),
            stoppnivå.getKid(),
            stoppnivå.getUtbetalesTilId(),
            stoppnivå.getUtbetalesTilNavn(),
            stoppnivå.getBilagsType(),
            stoppnivå.getForfall(),
            stoppnivå.isFeilkonto(),
            tilBeregningStoppNivåDetaljerDto(stoppnivå.getBeregningStoppnivaaDetaljer()));

    }

    private static List<BeregningStoppnivåDetaljerDto> tilBeregningStoppNivåDetaljerDto(List<BeregningStoppnivaaDetaljer> beregningStoppnivaaDetaljer) {
        return safeStream(beregningStoppnivaaDetaljer)
            .map(SimuleringResponsMapper::tilBeregningStoppNivåDto)
            .toList();
    }

    private static BeregningStoppnivåDetaljerDto tilBeregningStoppNivåDto(BeregningStoppnivaaDetaljer detaljer) {
        return new BeregningStoppnivåDetaljerDto(
            detaljer.getFaktiskFom(),
            detaljer.getFaktiskTom(),
            detaljer.getKontoStreng(),
            detaljer.getBehandlingskode(),
            detaljer.getBelop(),
            detaljer.getTrekkVedtakId(),
            detaljer.getStonadId(),
            detaljer.getKorrigering(),
            detaljer.isTilbakeforing(),
            detaljer.getLinjeId(),
            detaljer.getSats(),
            detaljer.getTypeSats(),
            detaljer.getAntallSats(),
            detaljer.getSaksbehId(),
            detaljer.getUforeGrad(),
            detaljer.getKravhaverId(),
            detaljer.getDelytelseId(),
            detaljer.getBostedsenhet(),
            detaljer.getSkykldnerId(),
            detaljer.getKlassekode(),
            detaljer.getKlasseKodeBeskrivelse(),
            detaljer.getTypeKlasse(),
            detaljer.getTypeKlasseBeskrivelse(),
            detaljer.getRefunderesOrgNr());

    }
}
