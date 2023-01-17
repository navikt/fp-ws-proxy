package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.mapper;

import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.kravgrunnlag.request.AnnullerKravGrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.kravgrunnlag.request.KodeAksjon;
import no.nav.okonomi.tilbakekrevingservice.KravgrunnlagAnnulerRequest;

public class AnnullerKravgrunnlagRequestMapper {

    protected static final String OKO_SAKSBEH_ID = "K231B433";  //fast verdi

    private AnnullerKravgrunnlagRequestMapper() {
    }

    public static KravgrunnlagAnnulerRequest tilKravgrunnlagAnnulerRequest(AnnullerKravGrunnlagDto annulerKravGrunnlagDtoRest) {
        var annullerKravgrunnlagDto = new no.nav.tilbakekreving.kravgrunnlag.annuller.v1.AnnullerKravgrunnlagDto();
        annullerKravgrunnlagDto.setKodeAksjon(KodeAksjon.ANNULERE_GRUNNLAG.getKode());
        annullerKravgrunnlagDto.setVedtakId(annulerKravGrunnlagDtoRest.vedtakId());
        annullerKravgrunnlagDto.setSaksbehId(OKO_SAKSBEH_ID);
        var kravgrunnlagAnnulerRequest = new KravgrunnlagAnnulerRequest();
        kravgrunnlagAnnulerRequest.setAnnullerkravgrunnlag(annullerKravgrunnlagDto);
        return kravgrunnlagAnnulerRequest;
    }
}
