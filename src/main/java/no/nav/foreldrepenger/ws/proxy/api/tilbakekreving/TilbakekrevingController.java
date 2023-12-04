package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.iverksett.TilbakekrevingVedtakDTO;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.kravgrunnlag.request.AnnullerKravGrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.kravgrunnlag.request.HentKravgrunnlagDetaljDto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.kravgrunnlag.respons.Kravgrunnlag431Dto;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error.KravgrunnlagErSperretException;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error.MangledeKravgrunnlagException;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error.UkjentFeilIKvitteringFraOSException;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error.ØkonomiKvitteringTolk;
import no.nav.foreldrepenger.ws.proxy.error.GenerellSoapFaultException;
import no.nav.foreldrepenger.ws.proxy.http.ProtectedRestController;
import no.nav.tilbakekreving.kravgrunnlag.detalj.v1.DetaljertKravgrunnlagDto;
import no.nav.tilbakekreving.typer.v1.MmelDto;
import no.nav.tilbakekreving.typer.v1.PeriodeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.stream.Collectors;

import static no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error.ØkonomiKvitteringTilStrengMapper.formaterKvittering;
import static no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.mapper.AnnullerKravgrunnlagRequestMapper.tilKravgrunnlagAnnulerRequest;
import static no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.mapper.HentKravgrunnlagDetaljRequestMapper.tilKravgrunnlagHentDetaljXMLRequest;
import static no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.mapper.Kravgrunnlag431DtoMapper.tilDto;
import static no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.mapper.TilbakekrevingsvedtakRequestMapper.tilTilbakekrevingsvedtakRequest;
import static no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.util.TilbakekrevingsvedtakMarshaller.marshall;

/**
 * Skal på sikt ersatte WS kall fra fptilbake til økonomi som gjøres i
 * https://github.com/navikt/fptilbake/blob/master/integrasjontjenester/oekonomi-tilbakekreving-klient/src/main/java/no/nav/foreldrepenger/tilbakekreving/integrasjon/økonomi/ØkonomiConsumerImpl.java
 */
@ProtectedRestController(value = "/tilbakekreving")
public class TilbakekrevingController {

    private static final Logger LOG = LoggerFactory.getLogger(TilbakekrevingController.class);
    private static final Logger SECURE_LOG = LoggerFactory.getLogger("secureLogger");

    private static final String KRAVGRUNNLAG_PATH = "/kravgrunnlag";
    private static final String KRAVGRUNNLAG_ANNULLER_PATH = "/kravgrunnlag/annuller";
    private static final String TILBAKEKREVINGVEDTAK_PATH = "/tilbakekrevingsvedtak";

    private final TilbakekrevingSoapClient tilbakekrevingKlientWs;

    public TilbakekrevingController(TilbakekrevingSoapClient tilbakekrevingKlientWs) {
        this.tilbakekrevingKlientWs = tilbakekrevingKlientWs;
    }

    @PostMapping(TILBAKEKREVINGVEDTAK_PATH)
    public void iverksettTilbakekrevingsvedtak(@Valid @NotNull @RequestBody TilbakekrevingVedtakDTO tilbakekrevingVedtakDto) {
        try {
            LOG.info("Iverksetter tilbakekrevingsvedtak for vedtak {}", tilbakekrevingVedtakDto.vedtakId());
            var request = tilTilbakekrevingsvedtakRequest(tilbakekrevingVedtakDto);
            var respons = tilbakekrevingKlientWs.iverksettTilbakekrevingsvedtak(request);
            var kvittering = respons.getMmel();
            validerKvitteringIverksettTilbakekrevingsvedtak(kvittering);
            LOG.info("Tilbakekrevingsvedtak sendt til oppdragsystemet OK. Alvorlighetsgrad='{}' infomelding='{}'", kvittering.getAlvorlighetsgrad(), kvittering.getBeskrMelding());
        } catch (Exception e) {
            loggRequestTilSecureLogsIForbindelseMedFeil(tilbakekrevingVedtakDto);
            throw e;
        }
    }

    @PostMapping(KRAVGRUNNLAG_PATH)
    public Kravgrunnlag431Dto kravgrunnlagHentDetalj(@Valid @NotNull @RequestBody HentKravgrunnlagDetaljDto kravgrunnlagDetaljDto) {
        var kravgrunnlagId = kravgrunnlagDetaljDto.kravgrunnlagId().longValue();
        LOG.info("Henter kravgrunnlag for kravgrunnlagId {}", kravgrunnlagId);
        var request = tilKravgrunnlagHentDetaljXMLRequest(kravgrunnlagDetaljDto);
        {
            // Midlertidig for feilanalyse
            var req = request.getHentkravgrunnlag();
            LOG.info("Henter kravgrunnlag for {}, med: kode: {}, enhet: {}, saksbeh: {}", req.getKravgrunnlagId(), req.getKodeAksjon(), req.getEnhetAnsvarlig(), req.getSaksbehId());
        }
        var response = tilbakekrevingKlientWs.kravgrunnlagHentDetalj(request);
        var kvittering = response.getMmel();
        var nyKrav = response.getDetaljertkravgrunnlag();
        {
            // Midlertidig for feilanalyse
            LOG.info("Hentet kravgrunnlag for {}, med: vedtak: {}, vedtakOmgjort: {}, perioder: {}, liste fom: {}", nyKrav.getKravgrunnlagId(),
                nyKrav.getVedtakId(),
                nyKrav.getVedtakIdOmgjort(),
                (long) nyKrav.getTilbakekrevingsPeriode().size(),
                nyKrav.getTilbakekrevingsPeriode().stream().map(p -> p.getPeriode().getFom().toString()).sorted().collect(Collectors.joining(", ")));
        }
        validerMottattKvitteringVedHentingAvKravgrunnlag(kravgrunnlagDetaljDto, kravgrunnlagId, kvittering);
        LOG.info("Kravgrunnlag hentet OK for kravgrunnlagId={} med Alvorlighetsgrad='{}' kodeMelding='{}' infomelding='{}'",
            kravgrunnlagId,
            kvittering.getAlvorlighetsgrad(),
            kvittering.getKodeMelding(),
            kvittering.getBeskrMelding());
        LOG.info("Referanse fra WS: {}", nyKrav.getReferanse());
        return tilDto(nyKrav);
    }

    @PutMapping(KRAVGRUNNLAG_ANNULLER_PATH)
    public void kravgrunnlagAnnuler(@Valid @NotNull @RequestBody AnnullerKravGrunnlagDto annulerKravGrunnlagDtoRest) {
        LOG.info("Annulerer kravgrunnlag for vedtakid {}", annulerKravGrunnlagDtoRest.vedtakId());
        var request = tilKravgrunnlagAnnulerRequest(annulerKravGrunnlagDtoRest);
        var respons = tilbakekrevingKlientWs.kravgrunnlagAnnuler(request);
        validerKvitteringForAnnulereGrunnlag(respons.getMmel());
        var kvittering = respons.getMmel();
        LOG.info("Annulering av kravgrunnlag OK. Alvorlighetsgrad='{}' infomelding='{}'",
            kvittering.getAlvorlighetsgrad(),
            kvittering.getBeskrMelding());
    }

    private static void loggRequestTilSecureLogsIForbindelseMedFeil(TilbakekrevingVedtakDTO tilbakekrevingVedtakDto) {
        try {
            var request = tilTilbakekrevingsvedtakRequest(tilbakekrevingVedtakDto);
            SECURE_LOG.info("Iverksetting av tilbakekrevingsvedtak feilet for følgende request {}", marshall(request));
        } catch (Exception exceptionIMapping) {
            SECURE_LOG.info("Iverksetting av tilbakekrevingsvedtak feilet ved mapping fra JSON til XML for følgende request {}", tilbakekrevingVedtakDto);
        }
    }

    private void validerKvitteringForAnnulereGrunnlag(MmelDto mmel) {
        if (!ØkonomiKvitteringTolk.erKvitteringOK(mmel)) {
            throw new GenerellSoapFaultException(String.format("FPT-539079: Fikk feil fra OS ved annulere av kravgrunnlag. %s", formaterKvittering(mmel)));
        }
    }

    private void validerKvitteringIverksettTilbakekrevingsvedtak(MmelDto mmel) {
        if (!ØkonomiKvitteringTolk.erKvitteringOK(mmel)) {
            throw new UkjentFeilIKvitteringFraOSException(String.format("FPT-609912: Fikk feil fra OS ved iverksetting. %s", formaterKvittering(mmel)));
        }
    }

    private void validerMottattKvitteringVedHentingAvKravgrunnlag(HentKravgrunnlagDetaljDto kravgrunnlagDetaljDto, Long kravgrunnlagId, MmelDto mmel) {
        if (!ØkonomiKvitteringTolk.erKvitteringOK(mmel)) {
            throw new GenerellSoapFaultException(String.format("FPT-539078: Fikk feil fra OS ved henting av kravgrunnlag for kravgrunnlagId=%s. %s", kravgrunnlagId, formaterKvittering(mmel)));
        }
        if (ØkonomiKvitteringTolk.erKravgrunnlagetIkkeFinnes(mmel)) {
            SECURE_LOG.info("Kravgrunnlag finnes ikke for request {}", kravgrunnlagDetaljDto);
            throw new MangledeKravgrunnlagException(kravgrunnlagId, formaterKvittering(mmel));
        }
        if (ØkonomiKvitteringTolk.erKravgrunnlagetSperret(mmel)) {
            throw new KravgrunnlagErSperretException(kravgrunnlagId, formaterKvittering(mmel));
        }
        if (ØkonomiKvitteringTolk.erDetUkjentFeilIKvitteringen(mmel)) {
            throw new UkjentFeilIKvitteringFraOSException(String.format("FPT-539085: Fikk ukjent feil fra OS ved henting av kravgrunnlag med kravgrunnlagId=%s. %s",
                kravgrunnlagId, formaterKvittering(mmel)));
        }
    }

}
