package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving;

import static no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.TilbakekrevingController.TILBAKEKREVING_PATH;
import static no.nav.foreldrepenger.ws.proxy.config.TokenUtilConfiguration.STS_RS;

import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.dto.AnnulerKravGrunnlagDtoRest;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.dto.KodeAksjon;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.dto.Kravgrunnlag431Dto;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.dto.KravgrunnlagDetaljDto;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.dto.KravgrunnlagHentDetaljResponsDto;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.dto.Kvittering;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.dto.TilbakekrevingVedtakDto;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error.ØkonomiConsumerFeil;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error.ØkonomiKvitteringTolk;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.mapper.HentKravgrunnlagMapper;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.mapper.TilbakekrevingWSMapper;
import no.nav.okonomi.tilbakekrevingservice.KravgrunnlagAnnulerRequest;
import no.nav.okonomi.tilbakekrevingservice.KravgrunnlagAnnulerResponse;
import no.nav.okonomi.tilbakekrevingservice.TilbakekrevingsvedtakResponse;
import no.nav.security.token.support.spring.ProtectedRestController;
import no.nav.tilbakekreving.kravgrunnlag.annuller.v1.AnnullerKravgrunnlagDto;
import no.nav.tilbakekreving.typer.v1.MmelDto;

/**
 * Skal på sikt ersatte WS kall fra fptilbake til økonomi som gjøres i
 * https://github.com/navikt/fptilbake/blob/master/integrasjontjenester/oekonomi-tilbakekreving-klient/src/main/java/no/nav/foreldrepenger/tilbakekreving/integrasjon/økonomi/ØkonomiConsumerImpl.java
 */
@Validated
@ProtectedRestController(issuer = STS_RS, value = TILBAKEKREVING_PATH)
class TilbakekrevingController {

    private static final Logger LOG = LoggerFactory.getLogger(TilbakekrevingController.class);
    public static final String TILBAKEKREVING_PATH = "/tilbakekreving";

    private final TilbakekrevingKlientWs tilbakekrevingKlientWs;

    public TilbakekrevingController(TilbakekrevingKlientWs tilbakekrevingKlientWs) {
        this.tilbakekrevingKlientWs = tilbakekrevingKlientWs;
    }

    // TODO: Problemer
    //  Exceptions som catch (SOAPFaultException e) { blir oversatt til IntegrasjonException exception i fptilbake
    //      SOAPFaultException kan mappes til Integrasjonsexcepiton og retuner new IntegrasjonException("F-942048", String.format("SOAP tjenesten [ %s ] returnerte en SOAP Fault:", webservice), e);
    //  Returnerer kvittering som fptilbake kan aggerer på slik at den kan oversette til logiske Exceptions? Eller skal fp-ws-proxy gjøre dette?
    //      Fptilbake aggerer på kvittering.
    //
    //  Fptilbake lagrer XML requesten. Hvordan løse dette? Logge dett i secure logs? Løsning: Logg til secure loggs ved feil.
    @PostMapping
    public Kvittering tilbakekrevingsvedtak(TilbakekrevingVedtakDto tilbakekrevingDto) {
        LOG.info("Sender request til tilbakekrevingsvedtak til økonomi");
        var request = TilbakekrevingWSMapper.tilTilbakekrevingsvedtakRequest(tilbakekrevingDto);
        var respons = tilbakekrevingKlientWs.tilbakekrevingsvedtak(request);
        return HentKravgrunnlagMapper.tilKvitteringDto(respons.getMmel());
    }

    @GetMapping
    public KravgrunnlagHentDetaljResponsDto kravgrunnlagHentDetalj(KravgrunnlagDetaljDto kravgrunnlagDetaljDto) {
        LOG.info("Sender request til tilbakekreving hos økonomi");
        var request = TilbakekrevingWSMapper.tilKravgrunnlagHentDetaljRequest(kravgrunnlagDetaljDto);
        var respons = tilbakekrevingKlientWs.kravgrunnlagHentDetalj(request);

        var kvittering = respons.getMmel();
        var kravgrunnlagId = request.getHentkravgrunnlag().getKravgrunnlagId().longValue();
        var behandlingsId = kravgrunnlagDetaljDto.behandlingsId();
        LOG.info("Hentet kravgrunnlag fra oppdragsystemet for behandlingId={} KravgrunnlagId={} Alvorlighetsgrad='{}' kodeMelding='{}' infomelding='{}'",
            behandlingsId,
            kravgrunnlagId,
            kvittering.getAlvorlighetsgrad(),
            kvittering.getKodeMelding(),
            kvittering.getBeskrMelding());
        LOG.info("Referanse fra WS: {}", respons.getDetaljertkravgrunnlag().getReferanse());
        return HentKravgrunnlagMapper.mapTilDto(respons);
    }

    @PostMapping
    public Kvittering kravgrunnlagAnnuler(AnnulerKravGrunnlagDtoRest annulerKravGrunnlagDtoRest) {
        var behandlingId = annulerKravGrunnlagDtoRest.behandlingId();
        LOG.info("Starter Anullerekravgrunnlag for behandlingId={}", behandlingId);
        var request = TilbakekrevingWSMapper.tilKravgrunnlagAnnulerRequest(annulerKravGrunnlagDtoRest);
        var respons = tilbakekrevingKlientWs.kravgrunnlagAnnuler(request);
        var kvittering = respons.getMmel();
        LOG.info("AnnulereKravgrunnlag sendt til oppdragssystemet. BehandlingId={} Alvorlighetsgrad='{}' infomelding='{}'",
            behandlingId,
            kvittering.getAlvorlighetsgrad(),
            kvittering.getBeskrMelding());
        return HentKravgrunnlagMapper.tilKvitteringDto(kvittering);
    }
}
