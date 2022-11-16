package no.nav.foreldrepenger.ws.proxy.api.arena.mapper;

import java.util.Arrays;

import no.nav.foreldrepenger.kontrakter.arena.respons.YtelseStatusDto;

public class RelatertYtelseStatusReverse {


    public static YtelseStatusDto reverseMap(String kode) {
        if (kode == null || kodeEksistereIkkeIEnum(kode)) {
            return YtelseStatusDto.UBEH;
        }
        return switch (RelatertYtelseStatus.valueOf(kode)) {
            case AVSLUTTET_IT, AVSLU, INAKT -> YtelseStatusDto.AVSLU;
            case LØPENDE_VEDTAK, IVERK -> YtelseStatusDto.LOP;
            case IKKE_STARTET, AKTIV, GODKJ, INNST, MOTAT, OPPRE, REGIS -> YtelseStatusDto.UBEH;
            default -> YtelseStatusDto.UBEH;
        };
    }

    private static boolean kodeEksistereIkkeIEnum(String kode) {
        return Arrays.stream(RelatertYtelseStatus.values())
            .map(RelatertYtelseStatus::getKode)
            .noneMatch(k -> k.equals(kode));
    }
}
