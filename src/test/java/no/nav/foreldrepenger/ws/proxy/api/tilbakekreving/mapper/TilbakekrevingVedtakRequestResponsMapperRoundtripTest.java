package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.mapper;

import static no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.util.TilbakekrevingsVedtakTestDataBygger.lagTilbakekrevingVedtakDTORequest;
import static no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.util.TilbakekrevingsVedtakTestDataBygger.lagTilbakekrevingsbelop;
import static no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.util.TilbakekrevingsVedtakTestDataBygger.lagTilbakekrevingsperiodeDTO;
import static no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.util.TilbakekrevingsVedtakTestDataBygger.lagTilbakekrevingsvedtakResponsXMLFraRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.iverksett.TilbakekrevingsperiodeDTO;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.TilbakekrevingController;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.TilbakekrevingKlientWs;
import no.nav.tilbakekreving.tilbakekrevingsvedtak.vedtak.v1.TilbakekrevingsvedtakDto;
import no.nav.tilbakekreving.typer.v1.MmelDto;

/**
 * Testene følger følgende struktur:
 *  1) Lag en TilbakekrevingVedtakDTO som er DTOen for APIet
 *  2.1) Bruk request mapperen til å mappe denne til en TilbakekrevingsvedtakDto XML request
 *  2.2) Siden XML request og respons har like objekter så setter vi TilbakekrevingsvedtakResponse basert på denne requesten.
 *  3) ACT: IverksettTilbakekrevingsvedtak
 *  4) Assert at objektet i 1) er likt objektet i 3).
 */
@ActiveProfiles(value = "local")
@ExtendWith(SpringExtension.class)
class TilbakekrevingVedtakRequestResponsMapperRoundtripTest {

    @Autowired
    private Environment env;
    private TilbakekrevingController tilbakekrevingController;
    private final TilbakekrevingKlientWs tilbakekrevingKlientWs = mock(TilbakekrevingKlientWs.class);

    @BeforeEach
    public void setup() {
        tilbakekrevingController = new TilbakekrevingController(tilbakekrevingKlientWs, env);
    }


    @Test
    void verifiserMappingFraRequestOgResponsHarErLike() {
        // 1) Bygg opp en TilbakekrevingVedtakDTO
        var tilbakekrevingsperider = List.of(
            lagTilbakekrevingsperiodeDTO(List.of(lagTilbakekrevingsbelop()), null),
            lagTilbakekrevingsperiodeDTO(List.of(lagTilbakekrevingsbelop(), lagTilbakekrevingsbelop()), null)
        );
        var orginalTilbakekrevingVedtakDto = lagTilbakekrevingVedtakDTORequest(tilbakekrevingsperider, null);

        // 2) Lag en TilbakekrevingVedtakDTO respons XML basert på denne
        when(tilbakekrevingKlientWs.iverksettTilbakekrevingsvedtak(any()))
            .thenReturn(lagTilbakekrevingsvedtakResponsXMLFraRequest(orginalTilbakekrevingVedtakDto, kvitteringOK()));

        // 3) ACT: IverksettTilbakekrevingsvedtak
        var respons = tilbakekrevingController.iverksettTilbakekrevingsvedtak(orginalTilbakekrevingVedtakDto);

        // 4) Assert at objektet i 1) er likt objektet i 3).
        assertThat(respons).isEqualTo(orginalTilbakekrevingVedtakDto);
    }

    @Test
    void verifiserResponsOgsåMapperFelteneRetnerBeregnes() {
        // 1) Bygg opp en TilbakekrevingVedtakDTO
        var renterBeregnesPeriode1 = "RENTER BEREGNEES I PERIODE1";
        var renterBeregnesPeriode2 = "RENTER BEREGNEES I PERIODE2";
        var tilbakekrevingsperider = List.of(
            lagTilbakekrevingsperiodeDTO(List.of(lagTilbakekrevingsbelop()), renterBeregnesPeriode1),
            lagTilbakekrevingsperiodeDTO(List.of(lagTilbakekrevingsbelop(), lagTilbakekrevingsbelop()), renterBeregnesPeriode2)
        );
        var renterBeregnesVedtak = "RENTER BEREGNEES!";
        var orginalTilbakekrevingVedtakDto = lagTilbakekrevingVedtakDTORequest(tilbakekrevingsperider, renterBeregnesVedtak);

        // 2) Lag en TilbakekrevingVedtakDTO respons XML basert på denne + setter renterBeregnes
        var tilbakekrevingsvedtakResponse1 = lagTilbakekrevingsvedtakResponsXMLFraRequest(orginalTilbakekrevingVedtakDto, kvitteringOK());
        // Setter renterBergenesVedtak manuelt ettersom det ikke følger med request-mappingen
        TilbakekrevingsvedtakDto tilbakekrevingsvedtak1 = tilbakekrevingsvedtakResponse1.getTilbakekrevingsvedtak();
        tilbakekrevingsvedtak1.setRenterBeregnes(renterBeregnesVedtak);
        tilbakekrevingsvedtak1.getTilbakekrevingsperiode().get(0).setRenterBeregnes(renterBeregnesPeriode1);
        tilbakekrevingsvedtak1.getTilbakekrevingsperiode().get(1).setRenterBeregnes(renterBeregnesPeriode2);

        when(tilbakekrevingKlientWs.iverksettTilbakekrevingsvedtak(any()))
            .thenReturn(tilbakekrevingsvedtakResponse1);

        // 3) ACT: IverksettTilbakekrevingsvedtak
        var respons = tilbakekrevingController.iverksettTilbakekrevingsvedtak(orginalTilbakekrevingVedtakDto);

        // 4) Assert at objektet i 1) er likt objektet i 3).
        assertThat(respons.renterBeregnes()).isEqualTo(renterBeregnesVedtak);
        assertThat(respons.tilbakekrevingsperiode())
            .extracting(TilbakekrevingsperiodeDTO::renterBeregnes)
            .containsExactlyInAnyOrder(renterBeregnesPeriode1, renterBeregnesPeriode2);
        assertThat(respons).isEqualTo(orginalTilbakekrevingVedtakDto);
    }

    public static MmelDto kvitteringOK() {
        var kvitteringOK = new MmelDto();
        kvitteringOK.setAlvorlighetsgrad("00");
        return kvitteringOK;
    }

}
