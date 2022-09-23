package no.nav.foreldrepenger.ws.proxy.api.simulering.dto;

import java.util.List;

public record BeregningsPeriodeDto(String periodeFom,
                                   String periodeTom,
                                   List<BeregningStoppnivåDto> beregningStoppnivaa) {

}
