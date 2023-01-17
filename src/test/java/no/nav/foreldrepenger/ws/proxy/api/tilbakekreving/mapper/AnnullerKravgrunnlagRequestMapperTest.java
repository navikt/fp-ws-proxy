package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.kravgrunnlag.request.AnnullerKravGrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.kravgrunnlag.request.KodeAksjon;

class AnnullerKravgrunnlagRequestMapperTest {

    @Test
    void verifiserKorrektMappingAvAnnullerKravgrunnlagRequest() {
        var annullerKravGrunnlagDto = new AnnullerKravGrunnlagDto(BigInteger.valueOf(100000L));

        var kravgrunnlagAnnulerRequest = AnnullerKravgrunnlagRequestMapper.tilKravgrunnlagAnnulerRequest(annullerKravGrunnlagDto);
        var annullerkravgrunnlagXML = kravgrunnlagAnnulerRequest.getAnnullerkravgrunnlag();

        assertThat(annullerkravgrunnlagXML).isNotNull();
        assertThat(annullerkravgrunnlagXML.getKodeAksjon()).isEqualTo(KodeAksjon.ANNULERE_GRUNNLAG.getKode());
        assertThat(annullerkravgrunnlagXML.getVedtakId()).isEqualTo(annullerKravGrunnlagDto.vedtakId());
        assertThat(annullerkravgrunnlagXML.getSaksbehId()).isEqualTo(AnnullerKravgrunnlagRequestMapper.OKO_SAKSBEH_ID);
    }
}
