package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving;

import static no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error.ØkonomiKvitteringTolk.KODE_MELDING_KRAVGRUNNLAG_ER_SPERRET;
import static no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error.ØkonomiKvitteringTolk.KODE_MELDING_KRAVGRUNNLAG_IKKE_FINNES;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.kravgrunnlag.request.HentKravgrunnlagDetaljDto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.kravgrunnlag.request.KodeAksjon;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error.KravgrunnlagErSperretException;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error.MangledeKravgrunnlagException;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error.UkjentFeilIKvitteringFraOSException;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.util.KravgrunnlagTestDataBuilder;
import no.nav.foreldrepenger.ws.proxy.error.GenerellSoapFaultException;
import no.nav.okonomi.tilbakekrevingservice.KravgrunnlagHentDetaljResponse;
import no.nav.tilbakekreving.typer.v1.MmelDto;

@ActiveProfiles(value = "local")
@ExtendWith(SpringExtension.class)
class TilbakekrevingControllerKravgrunnlagTest {

    @Autowired
    private Environment env;
    private TilbakekrevingController tilbakekrevingController;
    private final TilbakekrevingKlientWs tilbakekrevingKlientWs = mock(TilbakekrevingKlientWs.class);

    @BeforeEach
    public void setup() {
        tilbakekrevingController = new TilbakekrevingController(tilbakekrevingKlientWs, env);
    }

    @Test
    void happyCaseGyldigKvittering() {
        var hentKravgrunnlagDetaljDto = lagDefaultHentKravgrunnlagRequest();
        when(tilbakekrevingKlientWs.kravgrunnlagHentDetalj(any()))
            .thenReturn(kravgrunnlagHentDetaljResponse(kvittering("00")));

        assertThatCode(() -> tilbakekrevingController.kravgrunnlagHentDetalj(hentKravgrunnlagDetaljDto))
            .doesNotThrowAnyException();
    }

    @Test
    void exceptionHivesIkkeHvisAlvorlighetsgradenEr04() {
        var hentKravgrunnlagDetaljDto = lagDefaultHentKravgrunnlagRequest();
        when(tilbakekrevingKlientWs.kravgrunnlagHentDetalj(any()))
            .thenReturn(kravgrunnlagHentDetaljResponse(kvittering("04")));

        assertThatCode(() -> tilbakekrevingController.kravgrunnlagHentDetalj(hentKravgrunnlagDetaljDto))
            .doesNotThrowAnyException();
    }

    @Test
    void exceptionHivesHvisKvitteringErNull() {
        var hentKravgrunnlagDetaljDto = lagDefaultHentKravgrunnlagRequest();
        when(tilbakekrevingKlientWs.kravgrunnlagHentDetalj(any()))
            .thenReturn(kravgrunnlagHentDetaljResponse(null));

        assertThatThrownBy(() -> tilbakekrevingController.kravgrunnlagHentDetalj(hentKravgrunnlagDetaljDto))
            .isInstanceOf(GenerellSoapFaultException.class);
    }

    @Test
    void mangledeKravgrunnlagHivesGittTypiskMangledeKravgrunnlagsKvittering() {
        var hentKravgrunnlagDetaljDto = lagDefaultHentKravgrunnlagRequest();
        when(tilbakekrevingKlientWs.kravgrunnlagHentDetalj(any()))
            .thenReturn(kravgrunnlagHentDetaljResponse(kvittering("04", KODE_MELDING_KRAVGRUNNLAG_IKKE_FINNES)));

        assertThatThrownBy(() -> tilbakekrevingController.kravgrunnlagHentDetalj(hentKravgrunnlagDetaljDto))
            .isInstanceOf(MangledeKravgrunnlagException.class);
    }

    @Test
    void kravgrunnlagErSperretExceptionHivesHvisKvitteringenTilsierSperretKravgrunnlag() {
        var hentKravgrunnlagDetaljDto = lagDefaultHentKravgrunnlagRequest();
        when(tilbakekrevingKlientWs.kravgrunnlagHentDetalj(any()))
            .thenReturn(kravgrunnlagHentDetaljResponse(kvittering("04", KODE_MELDING_KRAVGRUNNLAG_ER_SPERRET)));

        assertThatThrownBy(() -> tilbakekrevingController.kravgrunnlagHentDetalj(hentKravgrunnlagDetaljDto))
            .isInstanceOf(KravgrunnlagErSperretException.class);
    }

    @Test
    void ukjentFeilIKvitteringFraOSExceptionVedLavAlvolrighetMenMedUkjentKodemeldingOgBeskrivelse() {
        var hentKravgrunnlagDetaljDto1 = lagDefaultHentKravgrunnlagRequest();
        when(tilbakekrevingKlientWs.kravgrunnlagHentDetalj(any()))
            .thenReturn(kravgrunnlagHentDetaljResponse(kvittering("00", "Ukjent kodemelding", "Beskrivelse")));

        assertThatThrownBy(() -> tilbakekrevingController.kravgrunnlagHentDetalj(hentKravgrunnlagDetaljDto1))
            .isInstanceOf(UkjentFeilIKvitteringFraOSException.class);

        var hentKravgrunnlagDetaljDto2 = lagDefaultHentKravgrunnlagRequest();
        when(tilbakekrevingKlientWs.kravgrunnlagHentDetalj(any()))
            .thenReturn(kravgrunnlagHentDetaljResponse(kvittering("04", "Ukjent kodemelding", "Beskrivelse")));

        assertThatThrownBy(() -> tilbakekrevingController.kravgrunnlagHentDetalj(hentKravgrunnlagDetaljDto2))
            .isInstanceOf(UkjentFeilIKvitteringFraOSException.class);
    }

    @Test
    void exceptionHivesHvisKvitteringInneholderHøyAlvorlighetsgrad() {
        var hentKravgrunnlagDetaljDto = lagDefaultHentKravgrunnlagRequest();
        when(tilbakekrevingKlientWs.kravgrunnlagHentDetalj(any()))
            .thenReturn(kravgrunnlagHentDetaljResponse(kvittering("08")));

        assertThatThrownBy(() -> tilbakekrevingController.kravgrunnlagHentDetalj(hentKravgrunnlagDetaljDto))
            .isInstanceOf(GenerellSoapFaultException.class);
    }

    private static MmelDto kvittering(String alvorlighetsgrad) {
        return kvittering(alvorlighetsgrad, null, null);
    }

    private static MmelDto kvittering(String alvorlighetsgrad, String kodemelding) {
        return kvittering(alvorlighetsgrad, kodemelding, null);
    }
    private static MmelDto kvittering(String alvorlighetsgrad, String kodemelding, String beskrivelse) {
        var kvitteringOK = new MmelDto();
        kvitteringOK.setAlvorlighetsgrad(alvorlighetsgrad);
        kvitteringOK.setKodeMelding(kodemelding);
        kvitteringOK.setBeskrMelding(beskrivelse);
        return kvitteringOK;
    }


    private HentKravgrunnlagDetaljDto lagDefaultHentKravgrunnlagRequest() {
        return new HentKravgrunnlagDetaljDto.Builder()
            .kodeAksjon(KodeAksjon.HENT_KORRIGERT_KRAVGRUNNLAG)
            .kravgrunnlagId(BigInteger.valueOf(123456))
            .saksbehId("W123456")
            .enhetAnsvarlig("8052")
            .build();
    }

    private static KravgrunnlagHentDetaljResponse kravgrunnlagHentDetaljResponse(MmelDto kvittering) {
        KravgrunnlagHentDetaljResponse kravgrunnlagHentDetaljResponse = new KravgrunnlagHentDetaljResponse();
        kravgrunnlagHentDetaljResponse.setDetaljertkravgrunnlag(KravgrunnlagTestDataBuilder.hentGrunnlag());
        kravgrunnlagHentDetaljResponse.setMmel(kvittering);
        return kravgrunnlagHentDetaljResponse;
    }

}
