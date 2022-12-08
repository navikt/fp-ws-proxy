package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving;

import static no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error.ØkonomiKvitteringTilStrengMapper.formaterKvittering;
import static no.nav.foreldrepenger.ws.proxy.config.TokenUtilConfiguration.STS_RS;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import no.nav.foreldrepenger.kontrakter.tilbakekreving.kravgrunnlag.request.HentKravgrunnlagDetaljDto;
import no.nav.foreldrepenger.kontrakter.tilbakekreving.kravgrunnlag.respons.Kravgrunnlag431Dto;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error.KravgrunnlagErSperretException;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error.MangledeKravgrunnlagException;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error.ØkonomiKvitteringTolk;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.mapper.HentKravgrunnlagMapper;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.mapper.TilbakekrevingWSMapper;
import no.nav.foreldrepenger.ws.proxy.error.GenerellSoapFaultException;
import no.nav.security.token.support.spring.ProtectedRestController;
import no.nav.tilbakekreving.typer.v1.MmelDto;

/**
 * Skal på sikt ersatte WS kall fra fptilbake til økonomi som gjøres i
 * https://github.com/navikt/fptilbake/blob/master/integrasjontjenester/oekonomi-tilbakekreving-klient/src/main/java/no/nav/foreldrepenger/tilbakekreving/integrasjon/økonomi/ØkonomiConsumerImpl.java
 */
@Validated
@ProtectedRestController(issuer = STS_RS, value = "/tilbakekreving", claimMap = {})
class TilbakekrevingController {

    private static final Logger LOG = LoggerFactory.getLogger(TilbakekrevingController.class);
    private static final Logger SECURE_LOG = LoggerFactory.getLogger("secureLogger");

    private static final String KRAVGRUNNLAG_PATH = "/kravgrunnlag";
    private static final String TILBAKEKREVINGVEDTAK_PATH = "/tilbakekrevingsvedtak";

    private final TilbakekrevingKlientWs tilbakekrevingKlientWs;

    public TilbakekrevingController(TilbakekrevingKlientWs tilbakekrevingKlientWs) {
        this.tilbakekrevingKlientWs = tilbakekrevingKlientWs;
    }

    @PostMapping(KRAVGRUNNLAG_PATH)
    public Kravgrunnlag431Dto kravgrunnlagHentDetalj(@Valid @RequestBody HentKravgrunnlagDetaljDto kravgrunnlagDetaljDto) {
        LOG.info("Sender request til tilbakekreving hos økonomi");
        var request = TilbakekrevingWSMapper.tilKravgrunnlagHentDetaljRequest(kravgrunnlagDetaljDto);
        var response = tilbakekrevingKlientWs.kravgrunnlagHentDetalj(request);
        var kvittering = response.getMmel();
        var kravgrunnlagId = request.getHentkravgrunnlag().getKravgrunnlagId().longValue();
        var behandlingId = kravgrunnlagDetaljDto.behandlingsId();
        validerKvitteringForHentGrunnlag(kravgrunnlagDetaljDto, behandlingId, kravgrunnlagId, kvittering);
        LOG.info("Hentet kravgrunnlag fra oppdragsystemet for behandlingId={} KravgrunnlagId={} Alvorlighetsgrad='{}' kodeMelding='{}' infomelding='{}'",
            behandlingId,
            kravgrunnlagId,
            kvittering.getAlvorlighetsgrad(),
            kvittering.getKodeMelding(),
            kvittering.getBeskrMelding());
        LOG.info("Referanse fra WS: {}", response.getDetaljertkravgrunnlag().getReferanse());
        return HentKravgrunnlagMapper.mapTilDto(response.getDetaljertkravgrunnlag());
    }

    private void validerKvitteringForHentGrunnlag(HentKravgrunnlagDetaljDto kravgrunnlagDetaljDto, Long behandlingId, Long kravgrunnlagId, MmelDto mmel) {
        if (!ØkonomiKvitteringTolk.erKvitteringOK(mmel)) {
            throw new GenerellSoapFaultException(String.format("FPT-539078: Fikk feil fra OS ved henting av kravgrunnlag for behandlingId=%s.%s",
                behandlingId, formaterKvittering(mmel)));
        } else if (ØkonomiKvitteringTolk.erKravgrunnlagetIkkeFinnes(mmel)) {
            SECURE_LOG.info("Kravgrunnlag finnes ikke for request {}", kravgrunnlagDetaljDto);
            throw new MangledeKravgrunnlagException(behandlingId, kravgrunnlagId, formaterKvittering(mmel));
        } else if (ØkonomiKvitteringTolk.erKravgrunnlagetSperret(mmel)) {
            throw new KravgrunnlagErSperretException(behandlingId, kravgrunnlagId, formaterKvittering(mmel));
        } else if (ØkonomiKvitteringTolk.harKravgrunnlagNoeUkjentFeil(mmel)) {
            throw new GenerellSoapFaultException(String.format("FPT-539085: Fikk ukjent feil fra OS ved henting av kravgrunnlag for behandlingId=%s og kravgrunnlagId=%s.%s",
                behandlingId, kravgrunnlagId, formaterKvittering(mmel)));
        }
    }


    // TODO: Problemer:
    //  Exceptions som catch (SOAPFaultException e) { blir oversatt til IntegrasjonException exception i fptilbake
    //      SOAPFaultException kan mappes til Integrasjonsexcepiton og retuner new IntegrasjonException("F-942048", String.format("SOAP tjenesten [ %s ] returnerte en SOAP Fault:", webservice), e);
    //  Returnerer kvittering som fptilbake kan aggerer på slik at den kan oversette til logiske Exceptions? Eller skal fp-ws-proxy gjøre dette?
    //      Fptilbake aggerer på kvittering
    //  Fptilbake lagrer XML requesten. Hvordan løse dette? Logge dett i secure logs?
    //      Løsning: Logg til secure loggs ved feil
//  @PostMapping(TILBAKEKREVINGVEDTAK_PATH)
//  public Kvittering tilbakekrevingsvedtak(@Valid @RequestBody TilbakekrevingVedtakDto tilbakekrevingDto) {
//      LOG.info("Sender request til tilbakekrevingsvedtak til økonomi");
//      var request = TilbakekrevingWSMapper.tilTilbakekrevingsvedtakRequest(tilbakekrevingDto);
//      var respons = tilbakekrevingKlientWs.tilbakekrevingsvedtak(request);
//        return HentKravgrunnlagMapper.tilKvitteringDto(respons.getMmel());
//      return null;
//  }

//    @PostMapping(KRAVGRUNNLAG_PATH)
//    public Kvittering kravgrunnlagAnnuler(@Valid @RequestBody AnnulerKravGrunnlagDtoRest annulerKravGrunnlagDtoRest) {
//        var behandlingId = annulerKravGrunnlagDtoRest.behandlingId();
//        LOG.info("Starter Anullerekravgrunnlag for behandlingId={}", behandlingId);
//        var request = TilbakekrevingWSMapper.tilKravgrunnlagAnnulerRequest(annulerKravGrunnlagDtoRest);
//        var respons = tilbakekrevingKlientWs.kravgrunnlagAnnuler(request);
//        var kvittering = respons.getMmel();
//        LOG.info("AnnulereKravgrunnlag sendt til oppdragssystemet. BehandlingId={} Alvorlighetsgrad='{}' infomelding='{}'",
//            behandlingId,
//            kvittering.getAlvorlighetsgrad(),
//            kvittering.getBeskrMelding());
//        return HentKravgrunnlagMapper.tilKvitteringDto(kvittering);
//        return null;
//    }
}
