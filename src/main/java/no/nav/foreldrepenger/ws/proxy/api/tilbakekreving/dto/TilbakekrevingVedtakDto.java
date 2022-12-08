package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.dto;

import java.util.List;

import no.nav.foreldrepenger.kontrakter.tilbakekreving.kravgrunnlag.respons.Kravgrunnlag431Dto;

public record TilbakekrevingVedtakDto(Kravgrunnlag431Dto kravgrunnlag,
                                      List<TilbakekrevingPeriodeDto> tilbakekrevingPerioder,
                                      String saksbehandlerid) { // SubjectHandler.getSubjectHandler().getUid() fra fptilbake
}
