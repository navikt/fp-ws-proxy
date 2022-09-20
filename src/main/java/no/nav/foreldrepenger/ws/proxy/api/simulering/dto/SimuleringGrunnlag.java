package no.nav.foreldrepenger.ws.proxy.api.simulering.dto;

import java.time.LocalDateTime;

public record SimuleringGrunnlag(String eksternReferanse,
                                 String aktørid,
                                 SimuleringResultat simuleringResultat,
                                 LocalDateTime simuleringKjørtDato,
                                 YtelseType ytelseType) {

}
