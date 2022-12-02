package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.dto;

import java.math.BigInteger;

import no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.dto.KodeAksjon;

public record KravgrunnlagDetaljDto(KodeAksjon kodeAksjon,
                                    String enhetAnsvarlig,
                                    BigInteger kravgrunnlagId,
                                    String saksbehId,
                                    //
                                    Long behandlingsId) {
}
