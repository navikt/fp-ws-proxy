package no.nav.foreldrepenger.ws.proxy.api.simulering.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

public record BeregningStoppnivåDetaljerDto(String faktiskFom,
                                            String faktiskTom,
                                            String kontoStreng,
                                            String behandlingskode,
                                            BigDecimal belop,
                                            long trekkVedtakId,
                                            String stonadId,
                                            String korrigering,
                                            boolean tilbakeforing,
                                            BigInteger linjeId,
                                            BigDecimal sats,
                                            String typeSats,
                                            BigDecimal antallSats,
                                            String saksbehId,
                                            BigInteger uforeGrad,
                                            String kravhaverId,
                                            String delytelseId,
                                            String bostedsenhet,
                                            String skykldnerId,
                                            String klassekode,
                                            String klasseKodeBeskrivelse,
                                            String typeKlasse,
                                            String typeKlasseBeskrivelse,
                                            String refunderesOrgNr) {


}
