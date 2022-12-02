package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.mapper;

import java.math.BigInteger;

import no.nav.foreldrepenger.kontrakter.tilbakekreving.kravgrunnlag.request.HentKravgrunnlagDetaljDto;
import no.nav.foreldrepenger.kontrakter.tilbakekreving.kravgrunnlag.request.KodeAksjon;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.dto.AnnulerKravGrunnlagDtoRest;
import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.dto.TilbakekrevingVedtakDto;
import no.nav.okonomi.tilbakekrevingservice.KravgrunnlagAnnulerRequest;
import no.nav.okonomi.tilbakekrevingservice.KravgrunnlagHentDetaljRequest;
import no.nav.okonomi.tilbakekrevingservice.TilbakekrevingsvedtakRequest;
import no.nav.tilbakekreving.kravgrunnlag.annuller.v1.AnnullerKravgrunnlagDto;
import no.nav.tilbakekreving.tilbakekrevingsvedtak.vedtak.v1.TilbakekrevingsvedtakDto;

public class TilbakekrevingWSMapper {

    private static final String OKO_SAKSBEH_ID = "K231B433";  //fast verdi

    private TilbakekrevingWSMapper() {
        // Statisk implementasjon
    }

    /**
     * TilbakekrevingsvedtakRequest
     * @param tilbakekrevingDto
     * @return
     */
    public static TilbakekrevingsvedtakRequest tilTilbakekrevingsvedtakRequest(TilbakekrevingVedtakDto tilbakekrevingDto) {
        var tilbakekrevingsvedtakDto = TilbakekrevingsvedtakMapper.tilDto(tilbakekrevingDto);
        return lagRequest(tilbakekrevingsvedtakDto);
    }

    private static TilbakekrevingsvedtakRequest lagRequest(TilbakekrevingsvedtakDto tilbakekrevingsvedtak) {
        TilbakekrevingsvedtakRequest request = new TilbakekrevingsvedtakRequest();
        request.setTilbakekrevingsvedtak(tilbakekrevingsvedtak);
        return request;
    }

    /**
     * KravgrunnlagHentDetlajRequest
     * @param tilbakekrevingDto
     * @return
     */
    public static KravgrunnlagHentDetaljRequest tilKravgrunnlagHentDetaljRequest(HentKravgrunnlagDetaljDto kravgrunnlagDetaljDto) {
        var hentKravgrunnlagDetalj = new no.nav.tilbakekreving.kravgrunnlag.detalj.v1.HentKravgrunnlagDetaljDto();
        hentKravgrunnlagDetalj.setKodeAksjon(kravgrunnlagDetaljDto.kodeAksjon().getKode());
        hentKravgrunnlagDetalj.setEnhetAnsvarlig(kravgrunnlagDetaljDto.enhetAnsvarlig());
        hentKravgrunnlagDetalj.setKravgrunnlagId(kravgrunnlagDetaljDto.kravgrunnlagId());
        hentKravgrunnlagDetalj.setSaksbehId(kravgrunnlagDetaljDto.saksbehId());
        var hentKravgrunnlagRequest = new KravgrunnlagHentDetaljRequest();
        hentKravgrunnlagRequest.setHentkravgrunnlag(hentKravgrunnlagDetalj);
        return hentKravgrunnlagRequest;
    }

    /**
     * KravgrunnlagAnnulerRequest
     * @param tilbakekrevingDto
     * @return
     */
    public static KravgrunnlagAnnulerRequest tilKravgrunnlagAnnulerRequest(AnnulerKravGrunnlagDtoRest annulerKravGrunnlagDtoRest) {
        var annullerKravgrunnlagDto = new AnnullerKravgrunnlagDto();
        annullerKravgrunnlagDto.setSaksbehId(OKO_SAKSBEH_ID);
        annullerKravgrunnlagDto.setKodeAksjon(KodeAksjon.ANNULERE_GRUNNLAG.getKode());
        annullerKravgrunnlagDto.setVedtakId(BigInteger.valueOf(annulerKravGrunnlagDtoRest.vedtakId()));
        var request = new KravgrunnlagAnnulerRequest();
        request.setAnnullerkravgrunnlag(annullerKravgrunnlagDto);
        return request;
    }
}
