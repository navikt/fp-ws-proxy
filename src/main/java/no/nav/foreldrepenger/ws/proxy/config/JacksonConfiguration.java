package no.nav.foreldrepenger.ws.proxy.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.json.JsonMapper;

import java.util.TimeZone;

@Configuration
public class JacksonConfiguration {

    @Bean
    @Primary
    public JsonMapper customObjectmapper() {
        return JsonMapper.builder()
            .defaultTimeZone(TimeZone.getTimeZone("Europe/Oslo"))
            .changeDefaultPropertyInclusion(a -> a
                .withValueInclusion(JsonInclude.Include.NON_ABSENT)
                .withContentInclusion(JsonInclude.Include.NON_ABSENT))
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .build();
    }


    @Bean
    public JsonMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder.defaultTimeZone(TimeZone.getTimeZone("Europe/Oslo"))
            .changeDefaultPropertyInclusion(a -> a
                .withValueInclusion(JsonInclude.Include.NON_ABSENT)
                .withContentInclusion(JsonInclude.Include.NON_ABSENT))
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    }
}
