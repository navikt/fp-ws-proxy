package no.nav.foreldrepenger.ws.proxy.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.pattern.PathPatternParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EndpointAuthorizationAnnotationTest {

    private static final String BASE_PACKAGE = "no.nav.foreldrepenger.ws.proxy";

    @Test
    void alleEndepunkterErAnnotertMedEntraCCRequiredEllerUnprotectedEndpoint() {
        var uannoterte = endpoints().stream().filter(endpoint -> !endpoint.entraCCRequired && !endpoint.unprotectedEndpoint).map(Endpoint::beskrivelse).toList();

        assertThat(uannoterte).describedAs("Endepunkter må annoteres med @EntraCCRequired eller @UnprotectedEndpoint").isEmpty();
    }

    @Test
    void ingenEndepunkterErAnnotertMedBegge() {
        var begge = endpoints().stream().filter(endpoint -> endpoint.entraCCRequired && endpoint.unprotectedEndpoint).map(Endpoint::beskrivelse).toList();

        assertThat(begge).describedAs("Endepunkter kan ikke ha både @EntraCCRequired og @UnprotectedEndpoint").isEmpty();
    }

    @Test
    void unprotectedEndpointMaaVaereTillattISecurityConfiguration() {
        var ikkeTillatt = endpoints().stream().filter(Endpoint::unprotectedEndpoint).filter(endpoint -> !erUnntatt(endpoint.path)).map(Endpoint::beskrivelse).toList();

        assertThat(ikkeTillatt).describedAs("Endepunkt annotert med @UnprotectedEndpoint må finnes i SecurityConfiguration.UNPROTECTED_ENDPOINTS").isEmpty();
    }

    @Test
    void entraCCRequiredEndepunktKanIkkeVaereUnprotected() {
        var feilaktigTillatt = endpoints().stream().filter(Endpoint::entraCCRequired).filter(endpoint -> erUnntatt(endpoint.path)).map(Endpoint::beskrivelse).toList();

        assertThat(feilaktigTillatt).describedAs("Endepunkt annotert med @EntraCCRequired kan ikke stå i SecurityConfiguration.UNPROTECTED_ENDPOINTS").isEmpty();
    }

    // verifiserer at scan treffer reelle tilfeller
    @Test
    void finnerFaktiskEndepunktSomSkalSjekkes() {
        assertThat(endpoints().stream().map(Endpoint::path).toList()).contains("/arena", "/simulering/start", "/tilbakekreving/tilbakekrevingsvedtak", "/tilbakekreving/kravgrunnlag", "/tilbakekreving/kravgrunnlag/annuller");
    }

    private static List<Endpoint> endpoints() {
        return controllerClasses().stream().flatMap(controller -> {
            var prefikser = pathsFromRequestMapping(AnnotatedElementUtils.findMergedAnnotation(controller, RequestMapping.class));
            return Arrays.stream(controller.getDeclaredMethods()).flatMap(method -> pathsFromRequestMapping(AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class)).stream().flatMap(path -> prefikser.stream().map(prefiks -> endpointFrom(controller, method, joinPaths(prefiks, path)))));
        }).toList();
    }

    private static Endpoint endpointFrom(Class<?> controller, Method method, String fullPath) {
        return new Endpoint(fullPath, harAnnotasjon(method, controller, EntraCCRequired.class), harAnnotasjon(method, controller, UnprotectedEndpoint.class), controller.getSimpleName() + "." + method.getName() + " [" + fullPath + "]");
    }

    private static List<String> pathsFromRequestMapping(RequestMapping mapping) {
        if (mapping == null) {
            return List.of();
        }
        var paths = new ArrayList<String>();
        if (mapping.path().length > 0) {
            paths.addAll(List.of(mapping.path()));
        } else if (mapping.value().length > 0) {
            paths.addAll(List.of(mapping.value()));
        } else {
            paths.add("");
        }
        return paths;
    }

    private static <A extends Annotation> boolean harAnnotasjon(Method method, Class<?> controller, Class<A> type) {
        return AnnotatedElementUtils.hasAnnotation(method, type) || AnnotatedElementUtils.hasAnnotation(controller, type);
    }

    private static boolean erUnntatt(String path) {
        var container = PathContainer.parsePath(path);
        return Arrays.stream(SecurityConfiguration.UNPROTECTED_ENDPOINTS).map(PathPatternParser.defaultInstance::parse).anyMatch(pattern -> pattern.matches(container));
    }

    private static List<Class<?>> controllerClasses() {
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));
        var classes = new ArrayList<Class<?>>();
        for (var candidate : scanner.findCandidateComponents(BASE_PACKAGE)) {
            var className = candidate.getBeanClassName();
            if (className != null) {
                classes.add(loadClass(className));
            }
        }
        return classes;
    }

    private static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Klarte ikke laste klasse: " + className, e);
        }
    }

    private static String joinPaths(String prefix, String path) {
        var left = prefix == null ? "" : prefix;
        var right = path == null ? "" : path;
        if (left.isBlank()) {
            return normalizePath(right);
        }
        if (right.isBlank()) {
            return normalizePath(left);
        }
        return normalizePath(left + "/" + right);
    }

    private static String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            return "/";
        }
        var normalized = path.replaceAll("/{2,}", "/");
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        if (normalized.length() > 1 && normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    private record Endpoint(String path, boolean entraCCRequired, boolean unprotectedEndpoint, String beskrivelse) { }
}
