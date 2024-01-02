package no.nav.foreldrepenger.ws.proxy.util;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public final class StreamUtil {

    private StreamUtil() {
    }

    @SafeVarargs
    public static <T> Stream<T> safeStream(T... elems) {
        return safeStream(List.of(elems));
    }

    public static <T> Stream<T> safeStream(List<T> list) {
        return Optional.ofNullable(list)
            .orElseGet(List::of)
            .stream();
    }

    public static <T> Stream<T> safeStream(Collection<T> set) {
        return Optional.ofNullable(set)
            .orElseGet(Set::of)
            .stream();
    }
}
