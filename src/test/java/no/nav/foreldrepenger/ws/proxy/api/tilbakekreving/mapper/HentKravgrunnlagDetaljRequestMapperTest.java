package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.mapper;

import static no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.mapper.HentKravgrunnlagDetaljRequestMapper.tilKravgrunnlagHentDetaljXMLRequest;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.kravgrunnlag.request.HentKravgrunnlagDetaljDto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.tilbakekreving.kravgrunnlag.request.KodeAksjon;

class HentKravgrunnlagDetaljRequestMapperTest {

    @Test
    void verifiserMappingFraInnkommendeDtotilXMLDtoIkkeMisterData() {
        var hentKravgrunnlagDetaljDto = new HentKravgrunnlagDetaljDto.Builder()
            .kodeAksjon(KodeAksjon.HENT_KORRIGERT_KRAVGRUNNLAG)
            .kravgrunnlagId(BigInteger.valueOf(123456))
            .saksbehId("W123456")
            .enhetAnsvarlig("8052")
            .build();
        var kravgrunnlagHentDetaljRequest = tilKravgrunnlagHentDetaljXMLRequest(hentKravgrunnlagDetaljDto);

        assertThat(kravgrunnlagHentDetaljRequest).isNotNull();
        assertThat(kravgrunnlagHentDetaljRequest.getHentkravgrunnlag()).isNotNull();
        assertThat(kravgrunnlagHentDetaljRequest.getHentkravgrunnlag().getKodeAksjon()).isEqualTo(hentKravgrunnlagDetaljDto.kodeAksjon().getKode());
        assertThat(kravgrunnlagHentDetaljRequest.getHentkravgrunnlag().getKravgrunnlagId()).isEqualTo(hentKravgrunnlagDetaljDto.kravgrunnlagId());
        assertThat(kravgrunnlagHentDetaljRequest.getHentkravgrunnlag().getSaksbehId()).isEqualTo(hentKravgrunnlagDetaljDto.saksbehId());
        assertThat(kravgrunnlagHentDetaljRequest.getHentkravgrunnlag().getEnhetAnsvarlig()).isEqualTo(hentKravgrunnlagDetaljDto.enhetAnsvarlig());
    }
}
