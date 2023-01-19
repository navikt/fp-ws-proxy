package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving;

import static no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.util.TilbakekrevingsVedtakTestDataBygger.lagTilbakekrevingVedtakDTORequest;
import static no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.util.TilbakekrevingsVedtakTestDataBygger.lagTilbakekrevingsbelop;
import static no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.util.TilbakekrevingsVedtakTestDataBygger.lagTilbakekrevingsperiodeDTO;
import static no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.util.TilbakekrevingsVedtakTestDataBygger.lagTilbakekrevingsvedtakResponsXMLFraRequest;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.iverksett.TilbakekrevingVedtakDTO;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error.UkjentFeilIKvitteringFraOSException;
import no.nav.tilbakekreving.typer.v1.MmelDto;

@ActiveProfiles(value = "local")
@ExtendWith(SpringExtension.class)
class TilbakekrevingControllerIverksettVedtakTest {

    private TilbakekrevingController tilbakekrevingController;
    private final TilbakekrevingKlientWs tilbakekrevingKlientWs = mock(TilbakekrevingKlientWs.class);

    @BeforeEach
    public void setup() {
        tilbakekrevingController = new TilbakekrevingController(tilbakekrevingKlientWs);
    }

    @Test
    void exceptionHivesIkkeHvisAlvorlighetsgradenEr00() {
        var tilbakekrevingsVedtakRequest = lagGyldigTilbakekrevingsVedtakRequest();
        when(tilbakekrevingKlientWs.iverksettTilbakekrevingsvedtak(any()))
            .thenReturn(lagTilbakekrevingsvedtakResponsXMLFraRequest(tilbakekrevingsVedtakRequest, kvittering("00")));
        assertThatCode(() -> tilbakekrevingController.iverksettTilbakekrevingsvedtak(tilbakekrevingsVedtakRequest))
            .doesNotThrowAnyException();
    }

    @Test
    void exceptionHivesIkkeHvisAlvorlighetsgradenEr04() {
        var tilbakekrevingsVedtakRequest = lagGyldigTilbakekrevingsVedtakRequest();
        when(tilbakekrevingKlientWs.iverksettTilbakekrevingsvedtak(any()))
            .thenReturn(lagTilbakekrevingsvedtakResponsXMLFraRequest(tilbakekrevingsVedtakRequest, kvittering("04")));
        assertThatCode(() -> tilbakekrevingController.iverksettTilbakekrevingsvedtak(tilbakekrevingsVedtakRequest))
            .doesNotThrowAnyException();
    }

    @Test
    void exceptionHivesHvisKvitteringErNull() {
        var tilbakekrevingsVedtakRequest = lagGyldigTilbakekrevingsVedtakRequest();
        when(tilbakekrevingKlientWs.iverksettTilbakekrevingsvedtak(any()))
            .thenReturn(lagTilbakekrevingsvedtakResponsXMLFraRequest(tilbakekrevingsVedtakRequest, null));

        assertThatThrownBy(() -> tilbakekrevingController.iverksettTilbakekrevingsvedtak(tilbakekrevingsVedtakRequest))
            .isInstanceOf(UkjentFeilIKvitteringFraOSException.class);
    }

    @Test
    void exceptionHivesHvisKvitteringInneholderHøyAlvorlighetsgrad() {
        var tilbakekrevingsVedtakRequest = lagGyldigTilbakekrevingsVedtakRequest();
        when(tilbakekrevingKlientWs.iverksettTilbakekrevingsvedtak(any()))
            .thenReturn(lagTilbakekrevingsvedtakResponsXMLFraRequest(tilbakekrevingsVedtakRequest, kvittering("08")));

        assertThatThrownBy(() -> tilbakekrevingController.iverksettTilbakekrevingsvedtak(tilbakekrevingsVedtakRequest))
            .isInstanceOf(UkjentFeilIKvitteringFraOSException.class);
    }


    public static MmelDto kvittering(String alvorlighetsgrad) {
        var kvitteringOK = new MmelDto();
        kvitteringOK.setAlvorlighetsgrad(alvorlighetsgrad);
        return kvitteringOK;
    }

    private static TilbakekrevingVedtakDTO lagGyldigTilbakekrevingsVedtakRequest() {
        // 1) Bygg opp en TilbakekrevingVedtakDTO
        var tilbakekrevingsperider = List.of(
            lagTilbakekrevingsperiodeDTO(List.of(lagTilbakekrevingsbelop()))
        );
        return lagTilbakekrevingVedtakDTORequest(tilbakekrevingsperider);
    }

}
