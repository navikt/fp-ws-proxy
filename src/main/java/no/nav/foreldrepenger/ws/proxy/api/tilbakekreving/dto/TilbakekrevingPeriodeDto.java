package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public record TilbakekrevingPeriodeDto(Periode periode,
                                       BigDecimal renter,
                                       List<TilbakekrevingBeløp> beløp) {
    public TilbakekrevingPeriodeDto {
        renter = BigDecimal.ZERO;
        beløp = new ArrayList<>();
    }
}
