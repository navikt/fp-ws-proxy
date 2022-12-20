package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.mapper;

import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.kravgrunnlag.request.HentKravgrunnlagDetaljDto;
import no.nav.okonomi.tilbakekrevingservice.KravgrunnlagHentDetaljRequest;

public class HentKravgrunnlagDetaljRequestMapper {

    private HentKravgrunnlagDetaljRequestMapper() {
    }

    public static KravgrunnlagHentDetaljRequest tilKravgrunnlagHentDetaljXMLRequest(HentKravgrunnlagDetaljDto kravgrunnlagDetaljDto) {
        var hentKravgrunnlagDetalj = new no.nav.tilbakekreving.kravgrunnlag.detalj.v1.HentKravgrunnlagDetaljDto();
        hentKravgrunnlagDetalj.setKodeAksjon(kravgrunnlagDetaljDto.kodeAksjon().getKode());
        hentKravgrunnlagDetalj.setEnhetAnsvarlig(kravgrunnlagDetaljDto.enhetAnsvarlig());
        hentKravgrunnlagDetalj.setKravgrunnlagId(kravgrunnlagDetaljDto.kravgrunnlagId());
        hentKravgrunnlagDetalj.setSaksbehId(kravgrunnlagDetaljDto.saksbehId());
        var hentKravgrunnlagRequest = new KravgrunnlagHentDetaljRequest();
        hentKravgrunnlagRequest.setHentkravgrunnlag(hentKravgrunnlagDetalj);
        return hentKravgrunnlagRequest;
    }
}
