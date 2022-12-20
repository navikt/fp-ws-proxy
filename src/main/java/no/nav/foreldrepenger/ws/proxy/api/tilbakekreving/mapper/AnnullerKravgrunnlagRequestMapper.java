package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.mapper;

import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.kravgrunnlag.request.AnnullerKravGrunnlagDto;
import no.nav.okonomi.tilbakekrevingservice.KravgrunnlagAnnulerRequest;

public class AnnullerKravgrunnlagRequestMapper {

    private AnnullerKravgrunnlagRequestMapper() {
    }

    public static KravgrunnlagAnnulerRequest tilKravgrunnlagAnnulerRequest(AnnullerKravGrunnlagDto annulerKravGrunnlagDtoRest) {
        var annullerKravgrunnlagDto = new no.nav.tilbakekreving.kravgrunnlag.annuller.v1.AnnullerKravgrunnlagDto();
        annullerKravgrunnlagDto.setKodeAksjon(annulerKravGrunnlagDtoRest.kodeAksjon());
        annullerKravgrunnlagDto.setVedtakId(annulerKravGrunnlagDtoRest.vedtakId());
        annullerKravgrunnlagDto.setSaksbehId(annulerKravGrunnlagDtoRest.saksbehId());
        var kravgrunnlagAnnulerRequest = new KravgrunnlagAnnulerRequest();
        kravgrunnlagAnnulerRequest.setAnnullerkravgrunnlag(annullerKravgrunnlagDto);
        return kravgrunnlagAnnulerRequest;
    }
}
