package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.mapper;

import no.nav.foreldrepenger.ws.proxy.util.DateUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigInteger;
import java.util.List;

import static no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.util.TilbakekrevingsVedtakTestDataBygger.lagTilbakekrevingVedtakDTORequest;
import static no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.util.TilbakekrevingsVedtakTestDataBygger.lagTilbakekrevingsbelop;
import static no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.util.TilbakekrevingsVedtakTestDataBygger.lagTilbakekrevingsperiodeDTO;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class TilbakekrevingsvedtakRequestMapperTest {


    @Test
    void requestMapperTest() {
        var tilbakekrevingsperider = List.of(
            lagTilbakekrevingsperiodeDTO(List.of(lagTilbakekrevingsbelop())),
            lagTilbakekrevingsperiodeDTO(List.of(lagTilbakekrevingsbelop(), lagTilbakekrevingsbelop()))
        );
        var orginalTilbakekrevingVedtakDto = lagTilbakekrevingVedtakDTORequest(tilbakekrevingsperider);

        var requestXML = TilbakekrevingsvedtakRequestMapper.tilTilbakekrevingsvedtakDto(orginalTilbakekrevingVedtakDto);

        assertThat(requestXML.getKodeAksjon()).isEqualTo("8"); // fast verdi!
        assertThat(requestXML.getKodeHjemmel()).isEqualTo("22-15"); // fast verdi!
        assertThat(requestXML.getRenterBeregnes()).isNull();


        assertThat(BigInteger.valueOf(orginalTilbakekrevingVedtakDto.vedtakId())).isEqualTo(requestXML.getVedtakId());
        assertThat(orginalTilbakekrevingVedtakDto.datoVedtakFagsystem()).isEqualTo(DateUtil.convertToLocalDate(requestXML.getDatoVedtakFagsystem()));
        assertThat(orginalTilbakekrevingVedtakDto.enhetAnsvarlig()).isEqualTo(requestXML.getEnhetAnsvarlig());
        assertThat(orginalTilbakekrevingVedtakDto.kontrollfelt()).isEqualTo(requestXML.getKontrollfelt());
        assertThat(orginalTilbakekrevingVedtakDto.saksbehId()).isEqualTo(requestXML.getSaksbehId());

        var tilbakekrevingsperiodeJSON = orginalTilbakekrevingVedtakDto.tilbakekrevingsperiode();
        var tilbakekrevingsperiodeXML = requestXML.getTilbakekrevingsperiode();
        assertThat(tilbakekrevingsperiodeJSON)
            .hasSameSizeAs(tilbakekrevingsperiodeXML)
            .hasSizeGreaterThan(0);
        for (int i = 0; i < tilbakekrevingsperiodeJSON.size(); i++) {
            var tilbakekrevingsperiodeDTOJSON = tilbakekrevingsperiodeJSON.get(i);
            var tilbakekrevingsperiodeDtoXML = tilbakekrevingsperiodeXML.get(i);
            assertThat(tilbakekrevingsperiodeDtoXML.getRenterBeregnes()).isNull();
            assertThat(tilbakekrevingsperiodeDTOJSON.periode().fom()).isEqualTo(DateUtil.convertToLocalDate(tilbakekrevingsperiodeDtoXML.getPeriode().getFom()));
            assertThat(tilbakekrevingsperiodeDTOJSON.periode().tom()).isEqualTo(DateUtil.convertToLocalDate(tilbakekrevingsperiodeDtoXML.getPeriode().getTom()));
            assertThat(tilbakekrevingsperiodeDTOJSON.belopRenter()).isEqualTo(tilbakekrevingsperiodeDtoXML.getBelopRenter());

            var tilbakekrevingsbelopDTOJSONList = tilbakekrevingsperiodeDTOJSON.tilbakekrevingsbelop();
            var tilbakekrevingsbelopXMLList = tilbakekrevingsperiodeDtoXML.getTilbakekrevingsbelop();
            assertThat(tilbakekrevingsbelopDTOJSONList)
                .hasSameSizeAs(tilbakekrevingsbelopXMLList)
                .hasSizeGreaterThan(0);
            for (int j = 0; j < tilbakekrevingsbelopDTOJSONList.size(); j++) {
                var tilbakekrevingsbelopDTOJSON = tilbakekrevingsbelopDTOJSONList.get(j);
                var tilbakekrevingsbelopDtoXML = tilbakekrevingsbelopXMLList.get(j);
                assertThat(tilbakekrevingsbelopDTOJSON.kodeKlasse()).isEqualTo(tilbakekrevingsbelopDtoXML.getKodeKlasse());
                assertThat(tilbakekrevingsbelopDTOJSON.belopOpprUtbet()).isEqualTo(tilbakekrevingsbelopDtoXML.getBelopOpprUtbet());
                assertThat(tilbakekrevingsbelopDTOJSON.belopNy()).isEqualTo(tilbakekrevingsbelopDtoXML.getBelopNy());
                assertThat(tilbakekrevingsbelopDTOJSON.belopTilbakekreves()).isEqualTo(tilbakekrevingsbelopDtoXML.getBelopTilbakekreves());
                assertThat(tilbakekrevingsbelopDTOJSON.belopUinnkrevd()).isEqualTo(tilbakekrevingsbelopDtoXML.getBelopUinnkrevd());
                assertThat(tilbakekrevingsbelopDTOJSON.belopSkatt()).isEqualTo(tilbakekrevingsbelopDtoXML.getBelopSkatt());

                assertThat(tilbakekrevingsbelopDTOJSON.kodeResultat().name()).isEqualTo(tilbakekrevingsbelopDtoXML.getKodeResultat());
                assertThat(tilbakekrevingsbelopDTOJSON.kodeAarsak().name()).isEqualTo(tilbakekrevingsbelopDtoXML.getKodeAarsak());
                assertThat(tilbakekrevingsbelopDTOJSON.kodeSkyld().name()).isEqualTo(tilbakekrevingsbelopDtoXML.getKodeSkyld());

            }
        }
    }


}
