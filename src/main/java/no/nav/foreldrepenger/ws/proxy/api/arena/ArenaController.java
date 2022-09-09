package no.nav.foreldrepenger.ws.proxy.api.arena;

import static no.nav.foreldrepenger.ws.proxy.api.arena.ArenaController.ARENA_PATH;
import static no.nav.foreldrepenger.ws.proxy.config.TokenUtilConfiguration.STS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;

import no.nav.security.token.support.spring.ProtectedRestController;

@ProtectedRestController(issuer = STS, value = ARENA_PATH)
public class ArenaController {
    private static final Logger LOG = LoggerFactory.getLogger(ArenaController.class);
    public static final String ARENA_PATH = "/arena";

    private final ArenaTjeneste arenaTjeneste;

    public ArenaController(ArenaTjeneste arenaTjeneste) {
        this.arenaTjeneste = arenaTjeneste;
    }

    @PostMapping
    public void sendTilArena(ArenaDto arenaDto) {
        LOG.info("Sender request til arena");
        var arenaWSRequest = ArenaMapperWS.tilWS(arenaDto);
        arenaTjeneste.sendWSRequest(arenaWSRequest);
    }
}
