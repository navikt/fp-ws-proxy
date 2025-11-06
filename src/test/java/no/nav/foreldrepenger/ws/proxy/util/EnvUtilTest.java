package no.nav.foreldrepenger.ws.proxy.util;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

class EnvUtilTest {

    @Test
    void testIsLocal_true() {
        var env = new MockEnvironment();
        env.addActiveProfile(EnvUtil.LOCAL);
        assertThat(EnvUtil.isDev(env)).isFalse();
        assertThat(EnvUtil.isLocal(env)).isTrue();
        assertThat(EnvUtil.isDevOrLocal(env)).isTrue();
    }

    @Test
    void testIsDev_true() {
        var env = new MockEnvironment();
        env.addActiveProfile(EnvUtil.DEV_FSS);
        assertThat(EnvUtil.isDev(env)).isTrue();
        assertThat(EnvUtil.isLocal(env)).isFalse();
        assertThat(EnvUtil.isDevOrLocal(env)).isTrue();
    }

    @Test
    void noProfilesSet() {
        var env = new MockEnvironment();
        assertThat(EnvUtil.isDev(env)).isFalse();
        assertThat(EnvUtil.isLocal(env)).isFalse();
        assertThat(EnvUtil.isDevOrLocal(env)).isFalse();
    }
}
