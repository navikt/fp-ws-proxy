package no.nav.foreldrepenger.ws.proxy.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClusterUtilTest {

    @Test
    void testThatDefaultProfileIsLocal_ok() {
        assertThat(ClusterUtil.profiler()).containsOnly(EnvUtil.LOCAL);
    }

    @Test
    void testThatDevFssGivesAllNeededProfiles() {
        assertThat(ClusterUtil.profilerFraCluster(EnvUtil.DEV_FSS)).containsExactlyInAnyOrder(EnvUtil.DEV, EnvUtil.DEV_FSS, EnvUtil.FSS);
    }

    @Test
    void testThatProdFssGivesAllNeededProfiles() {
        assertThat(ClusterUtil.profilerFraCluster(EnvUtil.PROD_FSS)).containsExactlyInAnyOrder(EnvUtil.PROD, EnvUtil.PROD_FSS, EnvUtil.FSS);
    }

    @Test
    void testThatDevGcpGivesAllNeededProfiles() {
        assertThat(ClusterUtil.profilerFraCluster(EnvUtil.DEV_GCP)).containsExactlyInAnyOrder(EnvUtil.DEV, EnvUtil.DEV_GCP, EnvUtil.GCP);
    }

    @Test
    void testThatProdGcpGivesAllNeededProfiles() {
        assertThat(ClusterUtil.profilerFraCluster(EnvUtil.PROD_GCP)).containsExactlyInAnyOrder(EnvUtil.PROD, EnvUtil.PROD_GCP, EnvUtil.GCP);
    }
}

