package no.nav.foreldrepenger.ws.proxy.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static java.lang.System.getenv;
import static java.lang.System.setProperty;

public class ClusterUtil {
    private static final Logger LOG = LoggerFactory.getLogger(ClusterUtil.class);
    static final String NAIS_CLUSTER_NAME = "NAIS_CLUSTER_NAME";

    private ClusterUtil() {
    }

    public static String[] profiler() {
        return profilerFraCluster(cluster());
    }

    static String[] profilerFraCluster(String cluster) {
        return switch (cluster) {
            case EnvUtil.DEV_GCP -> List.of(EnvUtil.DEV, EnvUtil.DEV_GCP, EnvUtil.GCP).toArray(new String[0]);
            case EnvUtil.PROD_GCP ->
                List.of(EnvUtil.PROD, EnvUtil.PROD_GCP, EnvUtil.GCP).toArray(new String[0]);
            case EnvUtil.DEV_FSS ->
                List.of(EnvUtil.DEV, EnvUtil.DEV_FSS, EnvUtil.FSS).toArray(new String[0]);
            case EnvUtil.PROD_FSS ->
                List.of(EnvUtil.PROD, EnvUtil.PROD_FSS, EnvUtil.FSS).toArray(new String[0]);
            default -> {
                LOG.info("NAIS cluster ikke detektert, antar {}", EnvUtil.LOCAL);
                setProperty(NAIS_CLUSTER_NAME, EnvUtil.LOCAL);
                yield new String[]{EnvUtil.LOCAL};
            }
        };
    }

    private static String cluster() {
        return Optional.ofNullable(getenv(NAIS_CLUSTER_NAME)).orElse(EnvUtil.LOCAL);
    }
}
