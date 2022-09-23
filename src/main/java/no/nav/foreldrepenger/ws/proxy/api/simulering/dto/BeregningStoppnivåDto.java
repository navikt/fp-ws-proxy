package no.nav.foreldrepenger.ws.proxy.api.simulering.dto;

import java.math.BigInteger;
import java.util.List;


public record BeregningStoppnivåDto(String kodeFagomraade,
                                    BigInteger stoppNivaaId,
                                    String behandlendeEnhet,
                                    long oppdragsId,
                                    String fagsystemId,
                                    String kid,
                                    String utbetalesTilId,
                                    String utbetalesTilNavn,
                                    String bilagsType,
                                    String forfall,
                                    boolean feilkonto,
                                    List<BeregningStoppnivåDetaljerDto> beregningStoppnivaaDetaljer) {

}
