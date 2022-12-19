package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.mapper;

import static no.nav.foreldrepenger.ws.proxy.util.DateUtil.convertToLocalDate;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.kontrakter.tilbakekreving.iverksett.TilbakekrevingVedtakDTO;
import no.nav.foreldrepenger.kontrakter.tilbakekreving.iverksett.TilbakekrevingsbelopDTO;
import no.nav.foreldrepenger.kontrakter.tilbakekreving.iverksett.TilbakekrevingsperiodeDTO;
import no.nav.foreldrepenger.kontrakter.tilbakekreving.kravgrunnlag.respons.Periode;
import no.nav.okonomi.tilbakekrevingservice.TilbakekrevingsvedtakRequest;

class TilbakekrevingsvedtakRequestMapperTest {




    @Test
    void verifiserAtMappingAvTilbakekrevingDtoTilXMLRequestIkkeMisterNoeInformasjon(){
        var tilbakekrevingsperider = List.of(
            lagTilbakekrevingsperiodeDTO(List.of(lagTilbakekrevingsbelop())),
            lagTilbakekrevingsperiodeDTO(List.of(lagTilbakekrevingsbelop(), lagTilbakekrevingsbelop()))
        );
        var tilbakekrevingVedtakDto = lagTilbakekrevingVedtakDTO(tilbakekrevingsperider);

        // Act
        var tilbakekrevingsvedtakRequest = TilbakekrevingsvedtakRequestMapper.tilTilbakekrevingsvedtakRequest(tilbakekrevingVedtakDto);

        // Verify
        verifiserMappingKonsistens(tilbakekrevingVedtakDto, tilbakekrevingsvedtakRequest);
    }


    private static void verifiserMappingKonsistens(TilbakekrevingVedtakDTO tilbakekrevingVedtakDto, TilbakekrevingsvedtakRequest tilbakekrevingsvedtakRequest) {
        var tilbakekrevingsvedtakXML = tilbakekrevingsvedtakRequest.getTilbakekrevingsvedtak();
        assertThat(tilbakekrevingsvedtakXML).isNotNull();
        assertThat(tilbakekrevingsvedtakXML.getKodeAksjon()).isEqualTo(tilbakekrevingVedtakDto.kodeAksjon());
        assertThat(tilbakekrevingsvedtakXML.getVedtakId().longValue()).isEqualTo(tilbakekrevingVedtakDto.vedtakId());
        assertThat(convertToLocalDate(tilbakekrevingsvedtakXML.getDatoVedtakFagsystem()))
            .isEqualTo(tilbakekrevingVedtakDto.datoVedtakFagsystem())
            .isNotNull();
        assertThat(tilbakekrevingsvedtakXML.getKodeHjemmel())
            .isEqualTo(tilbakekrevingVedtakDto.kodeHjemmel())
            .isEqualTo("22-15");
        // assertThat(tilbakekrevingsvedtakXML.getRenterBeregnes()).isEqualTo(tilbakekrevingVedtakDto.renterBeregnes()); // Brukes ikke i request
        assertThat(tilbakekrevingsvedtakXML.getEnhetAnsvarlig()).isEqualTo(tilbakekrevingVedtakDto.enhetAnsvarlig());
        assertThat(tilbakekrevingsvedtakXML.getSaksbehId()).isEqualTo(tilbakekrevingVedtakDto.saksbehId());


        var tilbakekrevingsperiodeXMLListe = tilbakekrevingsvedtakXML.getTilbakekrevingsperiode();
        var tilbakekrevingsperiodeListe = tilbakekrevingVedtakDto.tilbakekrevingsperiode();
        assertThat(tilbakekrevingsperiodeXMLListe).hasSameSizeAs(tilbakekrevingsperiodeListe);
        for (int i = 0; i < tilbakekrevingsperiodeListe.size(); i++) {
            var tilbakekrevingsperiodeDtoXML = tilbakekrevingsperiodeXMLListe.get(i);
            var tilbakekrevingsperiodeDTO = tilbakekrevingsperiodeListe.get(i);
            assertThat(convertToLocalDate(tilbakekrevingsperiodeDtoXML.getPeriode().getFom())).isEqualTo(tilbakekrevingsperiodeDTO.periode().fom());
            assertThat(convertToLocalDate(tilbakekrevingsperiodeDtoXML.getPeriode().getTom())).isEqualTo(tilbakekrevingsperiodeDTO.periode().tom());
            // assertThat(tilbakekrevingsperiodeDtoXML.getRenterBeregnes()).isEqualTo(tilbakekrevingsperiodeDTO.renterBeregnes()); // Settes ikke i request
            assertThat(tilbakekrevingsperiodeDtoXML.getBelopRenter()).isEqualTo(tilbakekrevingsperiodeDTO.belopRenter());


            var tilbakekrevingsbelopXMLListe = tilbakekrevingsperiodeDtoXML.getTilbakekrevingsbelop();
            var tilbakekrevingsbelopListe = tilbakekrevingsperiodeDTO.tilbakekrevingsbelop();
            assertThat(tilbakekrevingsbelopXMLListe).hasSameSizeAs(tilbakekrevingsbelopListe);
            for (int j = 0; i < tilbakekrevingsbelopListe.size(); i++) {
                var tilbakekrevingsbelopDtoXML = tilbakekrevingsbelopXMLListe.get(j);
                var tilbakekrevingsbelopDTO = tilbakekrevingsbelopListe.get(j);

                assertThat(tilbakekrevingsbelopDtoXML.getKodeKlasse()).isEqualTo(tilbakekrevingsbelopDTO.kodeKlasse());
                assertThat(tilbakekrevingsbelopDtoXML.getBelopOpprUtbet()).isEqualTo(tilbakekrevingsbelopDTO.belopOpprUtbet());
                assertThat(tilbakekrevingsbelopDtoXML.getBelopNy()).isEqualTo(tilbakekrevingsbelopDTO.belopNy());
                assertThat(tilbakekrevingsbelopDtoXML.getBelopTilbakekreves()).isEqualTo(tilbakekrevingsbelopDTO.belopTilbakekreves());
                assertThat(tilbakekrevingsbelopDtoXML.getBelopUinnkrevd()).isEqualTo(tilbakekrevingsbelopDTO.belopUinnkrevd());
                assertThat(tilbakekrevingsbelopDtoXML.getBelopSkatt()).isEqualTo(tilbakekrevingsbelopDTO.belopSkatt());
                assertThat(tilbakekrevingsbelopDtoXML.getKodeResultat()).isEqualTo(tilbakekrevingsbelopDTO.kodeResultat());
                assertThat(tilbakekrevingsbelopDtoXML.getKodeAarsak()).isEqualTo(tilbakekrevingsbelopDTO.kodeAarsak());
                assertThat(tilbakekrevingsbelopDtoXML.getKodeSkyld()).isEqualTo(tilbakekrevingsbelopDTO.kodeSkyld());
            }
        }
    }


    private static TilbakekrevingVedtakDTO lagTilbakekrevingVedtakDTO(List<TilbakekrevingsperiodeDTO> tilbakekrevingsperider) {
        return new TilbakekrevingVedtakDTO.Builder()
            .kodeAksjon("8")
            .vedtakId(10000000L)
            .datoVedtakFagsystem(LocalDate.now())
            .kodeHjemmel("22-15")
            .renterBeregnes("retnerBeregnes")
            .enhetAnsvarlig("8042")
            .kontrollfelt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss.SSSSSS")))
            .saksbehId("W123456")
            .tilbakekrevingsperiode(tilbakekrevingsperider)
            .build();
    }

    private TilbakekrevingsperiodeDTO lagTilbakekrevingsperiodeDTO(List<TilbakekrevingsbelopDTO> tilbakekrevingsbelop) {
        return new TilbakekrevingsperiodeDTO.Builder()
            .periode(new Periode(LocalDate.now().minusMonths(4), LocalDate.now()))
            .renterBeregnes("beregnes ja")
            .belopRenter(BigDecimal.TEN)
            .tilbakekrevingsbelop(tilbakekrevingsbelop)
            .build();
    }

    private TilbakekrevingsbelopDTO lagTilbakekrevingsbelop() {
        return new TilbakekrevingsbelopDTO.Builder()
            .kodeKlasse("kodeklasse")
            .belopOpprUtbet(BigDecimal.valueOf(1))
            .belopNy(BigDecimal.valueOf(2))
            .belopTilbakekreves(BigDecimal.ZERO)
            .belopUinnkrevd(BigDecimal.TEN)
            .belopSkatt(BigDecimal.valueOf(5))
            .kodeResultat("FEILREGISTRERT")
            .kodeAarsak("ANNET")
            .kodeSkyld("IKKE_FORDELT")
            .build();
    }
}
