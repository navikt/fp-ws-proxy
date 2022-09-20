package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.dto;

import java.math.BigDecimal;

public record TilbakekrevingBeløp(KlasseType klasseType,
                                  String klassekode,
                                  BigDecimal nyttBeløp,
                                  BigDecimal utbetaltBeløp,
                                  BigDecimal tilbakekrevBeløp,
                                  BigDecimal uinnkrevdBeløp,
                                  BigDecimal skattBeløp,
                                  KodeResultat kodeResultat) {

}
