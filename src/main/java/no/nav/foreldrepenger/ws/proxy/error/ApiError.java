package no.nav.foreldrepenger.ws.proxy.error;

import static com.fasterxml.jackson.annotation.JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED;
import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.common.util.MDCUtil.callId;
import static org.springframework.core.NestedExceptionUtils.getMostSpecificCause;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.ImmutableList;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(HttpStatus status,
                       @JsonFormat(shape = STRING, pattern = "dd-MM-yyyy hh:mm:ss") LocalDateTime timestamp,
                       @JsonFormat(with = WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED) List<String> messages,
                       String uuid) {

    ApiError(HttpStatus status, Throwable t, List<Object> objects) {
        this(status, LocalDateTime.now(), messages(t, objects), callId());
    }

    private static String getRootCauseMessage(Throwable e) {
        return getMostSpecificCause(e).getMessage();
    }

    private static List<String> messages(Throwable t, List<Object> objects) {
        var builder = new ImmutableList.Builder<String>();
        String msg = getRootCauseMessage(t);
        if (msg != null) {
            builder.add(msg);
        }
        return builder
                .addAll(objects.stream()
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .toList())
                .build();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[status=" + status + ", timestamp=" + timestamp + ", messages=" + messages
                + ", uuid=" + uuid + "]";
    }
}
