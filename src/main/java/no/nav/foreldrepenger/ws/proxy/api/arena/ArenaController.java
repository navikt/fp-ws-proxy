package no.nav.foreldrepenger.ws.proxy.api.arena;

import static no.nav.foreldrepenger.ws.proxy.api.arena.mapper.ArenaMapperWS.tilWSRequest;
import static no.nav.foreldrepenger.ws.proxy.config.TokenUtilConfiguration.STS_RS;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import no.nav.foreldrepenger.kontrakter.arena.request.ArenaRequestDto;
import no.nav.foreldrepenger.kontrakter.arena.respons.MeldekortUtbetalingsgrunnlagSakDto;
import no.nav.foreldrepenger.ws.proxy.api.arena.mapper.ArenaMapperWS;
import no.nav.security.token.support.spring.ProtectedRestController;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.meldinger.FinnMeldekortUtbetalingsgrunnlagListeResponse;

/**
 * Skal erstatte Meldekorttjenesten i fpabakus: https://github.com/navikt/fp-abakus/blob/master/domenetjenester/iay/src/main/java/no/nav/foreldrepenger/abakus/registerdata/ytelse/arena/MeldekortTjeneste.java
 */
@Validated
@ProtectedRestController(issuer = STS_RS, value = "/arena", claimMap = {})
public class ArenaController {
    private static final Logger LOG = LoggerFactory.getLogger(ArenaController.class);

    private final ArenaKlientWs arenaKlientWs;

    public ArenaController(ArenaKlientWs arenaKlientWs) {
        this.arenaKlientWs = arenaKlientWs;
    }

    @PostMapping
    public List<MeldekortUtbetalingsgrunnlagSakDto> henterDagpengerOgAAP(@Valid @RequestBody ArenaRequestDto arenaDto) {
        LOG.info("Henter dagpenger/AAP for {}", arenaDto);
        var arenaWSRequest = tilWSRequest(arenaDto);

        var meldekortUtbetalingsgrunnlagListe = Optional.ofNullable(arenaKlientWs.finnMeldekortUtbetalingsgrunnlagListe(arenaWSRequest))
            .map(FinnMeldekortUtbetalingsgrunnlagListeResponse::getMeldekortUtbetalingsgrunnlagListe)
            .orElse(List.of())
            .stream()
            .map(ArenaMapperWS::oversettArenaSak)
            .flatMap(Collection::stream)
            .toList();

        LOG.info("Hentet {} meldekort for dagpenger/AAP", meldekortUtbetalingsgrunnlagListe.size());
        return meldekortUtbetalingsgrunnlagListe;
    }


}
