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

    // TODO: fptilbake kjører med RunWithSavepoint og aggerer på kvitteringen. Returnere kvittering?
    //  Hvem skal reaguere på kvitteringene? fp-ws-proxy eller fptilbake?
    //  Hvis det skal her, mulig flytt det inn i klienten?
    @PostMapping
    public TilbakekrevingsvedtakResponse tilbakekrevingsvedtak(TilbakekrevingVedtakDto tilbakekrevingDto) {
        LOG.info("Sender request til tilbakekrevingsvedtak til økonomi");
        var request = TilbakekrevingWSMapper.tilTilbakekrevingsvedtakRequest(tilbakekrevingDto);
        return tilbakekrevingKlientWs.tilbakekrevingsvedtak(request);
    }

    @GetMapping
    public Kravgrunnlag431Dto kravgrunnlagHentDetalj(KravgrunnlagDetaljDto kravgrunnlagDetaljDto) {
        LOG.info("Sender request til tilbakekreving hos økonomi");
        var request = TilbakekrevingWSMapper.tilKravgrunnlagHentDetaljRequest(kravgrunnlagDetaljDto);
        var respons = tilbakekrevingKlientWs.kravgrunnlagHentDetalj(request);

        var kvittering = respons.getMmel();
        var kravgrunnlagId = request.getHentkravgrunnlag().getKravgrunnlagId().longValue();
        var behandlingsId = kravgrunnlagDetaljDto.behandlingsId();
        validerKvitteringForHentGrunnlag(behandlingsId, kravgrunnlagId, kvittering);
        LOG.info("Hentet kravgrunnlag fra oppdragsystemet for behandlingId={} KravgrunnlagId={} Alvorlighetsgrad='{}' kodeMelding='{}' infomelding='{}'",
            behandlingsId,
            kravgrunnlagId,
            kvittering.getAlvorlighetsgrad(),
            kvittering.getKodeMelding(),
            kvittering.getBeskrMelding());

        return HentKravgrunnlagMapper.mapTilDto(respons.getDetaljertkravgrunnlag());
    }

    @PostMapping
    public void kravgrunnlagAnnuler(AnnulerKravGrunnlagDtoRest annulerKravGrunnlagDtoRest) {
        var behandlingId = annulerKravGrunnlagDtoRest.behandlingId();
        LOG.info("Starter Anullerekravgrunnlag for behandlingId={}", behandlingId);
        var request = TilbakekrevingWSMapper.tilKravgrunnlagAnnulerRequest(annulerKravGrunnlagDtoRest);
        var respons = tilbakekrevingKlientWs.kravgrunnlagAnnuler(request);
        var kvittering = respons.getMmel();
        validerKvitteringForAnnulereGrunnlag(behandlingId, kvittering);
        LOG.info("AnnulereKravgrunnlag sendt til oppdragssystemet. BehandlingId={} Alvorlighetsgrad='{}' infomelding='{}'",
            behandlingId,
            kvittering.getAlvorlighetsgrad(),
            kvittering.getBeskrMelding());
    }

    private void validerKvitteringForHentGrunnlag(Long behandlingId, Long kravgrunnlagId, MmelDto mmel) {
        if (!ØkonomiKvitteringTolk.erKvitteringOK(mmel)) {
            throw ØkonomiConsumerFeil.fikkFeilkodeVedHentingAvKravgrunnlag(behandlingId, ØkonomiConsumerFeil.formaterKvittering(mmel));
        } else if (ØkonomiKvitteringTolk.erKravgrunnlagetIkkeFinnes(mmel)) {
            throw ØkonomiConsumerFeil.fikkFeilkodeVedHentingAvKravgrunnlagNårKravgrunnlagIkkeFinnes(behandlingId, kravgrunnlagId, ØkonomiConsumerFeil.formaterKvittering(mmel));
        } else if (ØkonomiKvitteringTolk.erKravgrunnlagetSperret(mmel)) {
            throw ØkonomiConsumerFeil.fikkFeilkodeVedHentingAvKravgrunnlagNårKravgrunnlagErSperret(behandlingId, kravgrunnlagId, ØkonomiConsumerFeil.formaterKvittering(mmel));
        } else if (ØkonomiKvitteringTolk.harKravgrunnlagNoeUkjentFeil(mmel)) {
            throw ØkonomiConsumerFeil.fikkUkjentFeilkodeVedHentingAvKravgrunnlag(behandlingId, kravgrunnlagId, ØkonomiConsumerFeil.formaterKvittering(mmel));
        }
    }

    private void validerKvitteringForAnnulereGrunnlag(Long behandlingId, MmelDto mmel) {
        if (!ØkonomiKvitteringTolk.erKvitteringOK(mmel)) {
            throw ØkonomiConsumerFeil.fikkFeilkodeVedAnnulereKravgrunnlag(behandlingId, ØkonomiConsumerFeil.formaterKvittering(mmel));
        }
    }
}
