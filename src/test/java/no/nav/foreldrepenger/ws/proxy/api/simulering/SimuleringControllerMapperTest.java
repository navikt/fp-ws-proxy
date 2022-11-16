package no.nav.foreldrepenger.ws.proxy.api.simulering;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.SimuleringResponsMapper;
import no.nav.system.os.entiteter.beregningskjema.Beregning;
import no.nav.system.os.entiteter.beregningskjema.BeregningStoppnivaa;
import no.nav.system.os.entiteter.beregningskjema.BeregningStoppnivaaDetaljer;
import no.nav.system.os.entiteter.beregningskjema.BeregningsPeriode;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningResponse;

class SimuleringControllerMapperTest {

    @Test
    void mapperTilResponsDtoMapperKorrektAntallElementer() {
        var simulerBeregningResponse = lagRespons("gjelderid1232456", "123456789");
        var simuleringsresponser = List.of(simulerBeregningResponse);

        var beregningDtos = SimuleringResponsMapper.tilBeregningDtoListe(simuleringsresponser);

        assertThat(beregningDtos)
            .hasSameSizeAs(simuleringsresponser)
            .hasSize(1);
        var beregnignDto = beregningDtos.get(0);
        assertThat(beregnignDto.beregningsPeriode())
            .hasSameSizeAs(simulerBeregningResponse.getResponse().getSimulering().getBeregningsPeriode())
            .hasSize(1);
        var beregningsPeriodeDto = beregnignDto.beregningsPeriode().get(0);
        assertThat(beregningsPeriodeDto.beregningStoppnivaa())
            .hasSameSizeAs(simulerBeregningResponse.getResponse().getSimulering().getBeregningsPeriode().get(0).getBeregningStoppnivaa())
            .hasSize(2);
    }

    private SimulerBeregningResponse lagRespons(String gjelderId, String fagsysId) {
        return lagRespons(gjelderId, fagsysId, "2018-10-15");
    }

    private SimulerBeregningResponse lagRespons(String gjelderId, String fagsysId, String forfallsdato) {
        SimulerBeregningResponse response = new SimulerBeregningResponse();
        no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.SimulerBeregningResponse innerResponse = new no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.SimulerBeregningResponse();
        response.setResponse(innerResponse);

        Beregning beregning = new Beregning();
        innerResponse.setSimulering(beregning);

        beregning.setGjelderNavn("dummy");
        beregning.setGjelderId(gjelderId);
        beregning.setDatoBeregnet("2018-10-10");
        beregning.setKodeFaggruppe("DUMMY");
        beregning.setBelop(BigDecimal.valueOf(1234L));

        BeregningsPeriode beregningsPeriode = new BeregningsPeriode();
        beregning.getBeregningsPeriode().add(beregningsPeriode);

        beregningsPeriode.setPeriodeFom("2018-09-01");
        beregningsPeriode.setPeriodeTom("2018-09-31");

        BeregningStoppnivaa stoppnivaaFP = opprettBeregningStoppnivå(Fagområde.FP, gjelderId, fagsysId, forfallsdato);
        BeregningStoppnivaa stoppnivaaSVP = opprettBeregningStoppnivå(Fagområde.SVP, gjelderId, fagsysId, forfallsdato);
        beregningsPeriode.getBeregningStoppnivaa().add(stoppnivaaFP);
        beregningsPeriode.getBeregningStoppnivaa().add(stoppnivaaSVP);

        BeregningStoppnivaaDetaljer stoppnivaaDetaljer = opprettStoppnivaaDetaljer("2018-10-10", "2018-11-11", "YTEL", BigDecimal.valueOf(12532L));
        stoppnivaaFP.getBeregningStoppnivaaDetaljer().add(stoppnivaaDetaljer);
        stoppnivaaSVP.getBeregningStoppnivaaDetaljer().add(stoppnivaaDetaljer);
        return response;
    }

    private BeregningStoppnivaa opprettBeregningStoppnivå(Fagområde fagområde, String gjelderId, String fagsysId, String forfallsdato) {
        BeregningStoppnivaa stoppnivaa = new BeregningStoppnivaa();

        stoppnivaa.setKodeFagomraade(fagområde.name());
        stoppnivaa.setUtbetalesTilId(gjelderId);
        stoppnivaa.setUtbetalesTilNavn("asfasf");
        stoppnivaa.setBehandlendeEnhet("8052");
        stoppnivaa.setForfall(forfallsdato);
        stoppnivaa.setOppdragsId(1234L);
        stoppnivaa.setStoppNivaaId(BigInteger.ONE);
        stoppnivaa.setFagsystemId(fagsysId);
        stoppnivaa.setBilagsType("U");
        stoppnivaa.setFeilkonto(false);
        return stoppnivaa;
    }

    private BeregningStoppnivaaDetaljer opprettStoppnivaaDetaljer(String fom, String tom, String posteringType, BigDecimal beløp) {
        BeregningStoppnivaaDetaljer stoppnivaaDetaljer = new BeregningStoppnivaaDetaljer();

        stoppnivaaDetaljer.setBelop(BigDecimal.valueOf(12345L));
        stoppnivaaDetaljer.setFaktiskFom(fom);
        stoppnivaaDetaljer.setFaktiskTom(tom);
        stoppnivaaDetaljer.setKontoStreng("1235432");
        stoppnivaaDetaljer.setBehandlingskode("2");
        stoppnivaaDetaljer.setBelop(beløp);
        stoppnivaaDetaljer.setTrekkVedtakId(0L);
        stoppnivaaDetaljer.setStonadId("2018-12-12");
        stoppnivaaDetaljer.setTilbakeforing(false);
        stoppnivaaDetaljer.setLinjeId(BigInteger.valueOf(21423L));
        stoppnivaaDetaljer.setSats(BigDecimal.valueOf(2254L));
        stoppnivaaDetaljer.setTypeSats("DAG");
        stoppnivaaDetaljer.setAntallSats(BigDecimal.valueOf(2542L));
        stoppnivaaDetaljer.setSaksbehId("5323");
        stoppnivaaDetaljer.setUforeGrad(BigInteger.valueOf(100L));
        stoppnivaaDetaljer.setDelytelseId("3523");
        stoppnivaaDetaljer.setBostedsenhet("4643");
        stoppnivaaDetaljer.setTypeKlasse(posteringType);
        stoppnivaaDetaljer.setTypeKlasseBeskrivelse("sfas");

        return stoppnivaaDetaljer;
    }


}
