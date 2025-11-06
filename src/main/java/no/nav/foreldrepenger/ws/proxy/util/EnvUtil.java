package no.nav.foreldrepenger.ws.proxy.util;

import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

public class EnvUtil {
    static final String FSS = "fss";
    static final String GCP = "gcp";
    static final String DEV = "dev";
    static final String PROD = "prod";
    static final String LOCAL = "local";

    static final String DEV_FSS = "dev-fss";
    static final String DEV_GCP = "dev-gcp";
    static final String PROD_FSS = "prod-fss";
    static final String PROD_GCP = "prod-gcp";

    private EnvUtil() {
    }

    public static boolean isDevOrLocal(Environment env) {
        return isDev(env) || isLocal(env);
    }

    public static boolean isDev(Environment env) {
        return env != null && env.acceptsProfiles(Profiles.of(DEV_GCP, DEV_FSS));
    }

    public static boolean isLocal(Environment env) {
        return env != null && env.acceptsProfiles(Profiles.of(LOCAL));
    }
}
