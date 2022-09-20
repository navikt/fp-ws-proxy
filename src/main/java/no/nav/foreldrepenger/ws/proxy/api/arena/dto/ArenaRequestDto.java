package no.nav.foreldrepenger.ws.proxy.api.arena.dto;

import java.time.LocalDate;

public record ArenaRequestDto(String ident, LocalDate fom, LocalDate tom) {
}
