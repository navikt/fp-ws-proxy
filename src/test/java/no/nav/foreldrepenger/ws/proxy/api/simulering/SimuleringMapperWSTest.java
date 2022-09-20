package no.nav.foreldrepenger.ws.proxy.api.simulering;

import static no.nav.foreldrepenger.ws.proxy.api.arena.dto.YtelseType.DAGPENGER;
import static no.nav.foreldrepenger.xmlutils.DateUtil.convertToLocalDate;
import static no.nav.foreldrepenger.xmlutils.DateUtil.convertToXMLGregorianCalendar;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.ws.proxy.api.arena.dto.ArenaRequestDto;
import no.nav.foreldrepenger.ws.proxy.api.arena.dto.MeldekortUtbetalingsgrunnlagSak;
import no.nav.foreldrepenger.ws.proxy.api.arena.dto.YtelseStatus;
import no.nav.foreldrepenger.ws.proxy.api.arena.dto.YtelseType;
import no.nav.foreldrepenger.ws.proxy.api.arena.mapper.ArenaMapperWS;
import no.nav.foreldrepenger.ws.proxy.api.arena.mapper.RelatertYtelseStatus;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.Bruker;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.Kodeverdi;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.Meldekort;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.Periode;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.Sak;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.Saksstatuser;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.Tema;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.Vedtak;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.informasjon.Vedtaksstatuser;

class SimuleringMapperWSTest {

    @Test
    void verifiserAtDataIkkeForsvinnerIMappingTilXMLRequest() {


    }

}
