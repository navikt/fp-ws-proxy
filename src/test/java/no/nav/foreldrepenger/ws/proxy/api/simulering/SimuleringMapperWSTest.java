package no.nav.foreldrepenger.ws.proxy.api.simulering;

import static no.nav.foreldrepenger.common.util.ResourceHandleUtil.copyToString;
import static no.nav.foreldrepenger.ws.proxy.api.simulering.YtelseType.FP;
import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.SimuleringRequestMapper.tilSimulerBeregingsRequester;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.system.os.entiteter.oppdragskjema.Attestant;
import no.nav.system.os.entiteter.oppdragskjema.Oppdragslinje;
import no.nav.system.os.entiteter.typer.simpletypes.FradragTillegg;
import no.nav.vedtak.exception.TekniskException;

class SimuleringMapperWSTest {

    private static final Long BEHANDLING_ID_1 = 42345L;

    @Test
    void test_skalKasteFeilVedUkjentEllerUgyldigXml() {
        assertThatThrownBy(() -> tilSimulerBeregingsRequester(Collections.singletonList("abcd"), null, false))
            .isInstanceOf(TekniskException.class)
            .hasMessageContaining("FPO-832562");
    }

    @Test
    void verifiserAtUnmarshallingAvXMLStrengIkkeMinsterNoeData() {
        var oppdragXml = copyToString("xml/simulering/oppdrag_refusjon.xml");
        var simuleringsrequest = tilSimulerBeregingsRequester(List.of(oppdragXml), null, false);

        assertThat(simuleringsrequest).hasSize(1);
        var simulerBeregningRequest = simuleringsrequest.get(0);
        var oppdrag = simulerBeregningRequest.getRequest().getOppdrag();
        assertThat(oppdrag).isNotNull();
        assertThat(oppdrag.getBilagstype()).isEmpty();
        assertThat(oppdrag.getAvstemmingsnokkel()).isEmpty();
        assertThat(oppdrag.getOmpostering()).isNull();
        assertThat(oppdrag.getKodeEndring()).isEqualTo("NY");
        assertThat(oppdrag.getKodeStatus()).isNull();
        assertThat(oppdrag.getDatoStatusFom()).isNull();
        assertThat(oppdrag.getKodeFagomraade()).isEqualTo("FPREF");
        var fagsystemId = "135702910101";
        assertThat(oppdrag.getFagsystemId()).isEqualTo(fagsystemId);
        assertThat(oppdrag.getOppdragsId()).isNull();
        assertThat(oppdrag.getUtbetFrekvens()).isEqualTo("MND");
        assertThat(oppdrag.getDatoForfall()).isNull();
        assertThat(oppdrag.getStonadId()).isNull();
        assertThat(oppdrag.getOppdragGjelderId()).isEqualTo("22067300444");
        assertThat(oppdrag.getDatoOppdragGjelderFom()).isEqualTo("2000-01-01");
        var saksbehandlerId = "Z991095";
        assertThat(oppdrag.getSaksbehId()).isEqualTo(saksbehandlerId);

        assertThat(oppdrag.getEnhet()).hasSize(1);
        var enhet = oppdrag.getEnhet().get(0);
        assertThat(enhet.getTypeEnhet()).isEqualTo("BOS");
        assertThat(enhet.getEnhet()).isEqualTo("8020");
        assertThat(enhet.getDatoEnhetFom()).isEqualTo("1900-01-01");

        assertThat(oppdrag.getBelopsgrense()).isEmpty();
        assertThat(oppdrag.getTekst()).isEmpty();

        // Assertions på oddragslinjene
        assertThat(oppdrag.getOppdragslinje()).hasSize(4);
        assertThat(oppdrag.getOppdragslinje())
            .extracting(Oppdragslinje::getSaksbehId)
            .containsOnly(saksbehandlerId);
        assertThat(oppdrag.getOppdragslinje())
            .extracting(Oppdragslinje::getHenvisning)
            .containsOnly("999951");
        assertThat(oppdrag.getOppdragslinje())
            .extracting(Oppdragslinje::getRefFagsystemId)
            .containsOnly(fagsystemId);

        var oppdragslinje1 = oppdrag.getOppdragslinje().stream()
            .filter(oppdragslinje -> "2017-09-07".equals(oppdragslinje.getDatoVedtakFom()))
            .findFirst()
            .orElseThrow();

        assertThat(oppdragslinje1.getKodeEndringLinje()).isEqualTo("NY");
        assertThat(oppdragslinje1.getVedtakId()).isEqualTo("2017-12-13");
        assertThat(oppdragslinje1.getDelytelseId()).isEqualTo("135702910101100");
        assertThat(oppdragslinje1.getKodeKlassifik()).isEqualTo("FPREFAG-IOP");
        assertThat(oppdragslinje1.getDatoVedtakFom()).isEqualTo("2017-09-07");
        assertThat(oppdragslinje1.getDatoVedtakTom()).isEqualTo("2017-09-09");
        assertThat(oppdragslinje1.getSats()).isEqualTo("1177");
        assertThat(oppdragslinje1.getFradragTillegg()).isEqualTo(FradragTillegg.T);
        assertThat(oppdragslinje1.getTypeSats()).isEqualTo("DAG");
        assertThat(oppdragslinje1.getBrukKjoreplan()).isEqualTo("N");
        assertThat(oppdragslinje1.getRefDelytelseId()).isEqualTo("135702910101100");

        var refusjonsinfo = oppdragslinje1.getRefusjonsInfo();
        assertThat(refusjonsinfo.getMaksDato()).isEqualTo("2017-10-10");
        assertThat(refusjonsinfo.getRefunderesId()).isEqualTo("00973861778");
        assertThat(refusjonsinfo.getDatoFom()).isEqualTo("2017-12-13");

        assertThat(oppdragslinje1.getGrad()).hasSize(1);
        var grad = oppdragslinje1.getGrad().get(0);
        assertThat(grad.getTypeGrad()).isEqualTo("UFOR");
        assertThat(grad.getGrad()).isEqualTo(BigInteger.valueOf(100));

        assertThat(oppdragslinje1.getAttestant()).hasSize(1)
            .extracting(Attestant::getAttestantId)
            .containsExactly(saksbehandlerId);
    }

    @Test
    void verifiserAtSimuleringUtenInntrekkGjøresBareMedRiktigYtelseTypeSamtOmposteringLikNAlleOppdragsXMLerMatcher() {
        var oppdragXml1 = copyToString("xml/simulering/oppdrag_mottaker_FP.xml");
        var oppdragXml2 = copyToString("xml/simulering/oppdrag_mottaker_SVP_2.xml");
        var oppdragXmlListe = List.of(oppdragXml1, oppdragXml2);
        var simuleringsrequest = tilSimulerBeregingsRequester(oppdragXmlListe, FP, true);

        assertThat(simuleringsrequest).hasSize(1)
            .extracting(request -> request.getRequest().getOppdrag().getKodeEndring())
            .containsOnly("ENDR");
        assertThat(simuleringsrequest)
            .extracting(request -> request.getRequest().getOppdrag().getOmpostering().getOmPostering())
            .containsOnly("N");
    }

    @Test
    void verifiserAtSimuleringUtenInntrekkGjøresBareMedRiktigYtelseTypeSamtOmposteringLikNBareEnXMLMatcher() {
        var oppdragXml1 = copyToString("xml/simulering/oppdrag_mottaker_FP.xml");
        var oppdragXml2 = copyToString("xml/simulering/oppdrag_mottaker_FP_2.xml");
        var oppdragXmlListe = List.of(oppdragXml1, oppdragXml2);
        var simuleringsrequest = tilSimulerBeregingsRequester(oppdragXmlListe, FP, true);

        assertThat(simuleringsrequest).hasSize(2)
            .extracting(request -> request.getRequest().getOppdrag().getKodeEndring())
            .containsOnly("ENDR");
        assertThat(simuleringsrequest)
            .extracting(request -> request.getRequest().getOppdrag().getOmpostering().getOmPostering())
            .containsOnly("N");
    }
}
