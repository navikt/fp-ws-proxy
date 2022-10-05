package no.nav.foreldrepenger.ws.proxy.api.arena.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.ws.proxy.config.JacksonConfiguration;

/**
 * Konsistens test for å verifiser at seralisering og deseralisering av DTO ikke endrer seg og fungere som forventet
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { JacksonConfiguration.class })
class MeldekortUtbetalingsgrunnlagSakDtoSeraliseringOgDeseraliseringTest {

    @Autowired
    private ObjectMapper MAPPER;

    @Test
    void konsistenstestForÅSjekkeAtDeseraliseringFungereUavhengigAvSeralisering() throws JsonProcessingException {
        var seralisertStreng = hardkodetSeralisertStreng();
        var testA = MAPPER.readValue(seralisertStreng, MeldekortUtbetalingsgrunnlagSak.class);
        assertThat(testA).isEqualTo(getMeldekortUtbetalingsgrunnlagSakDto());
    }

    @Test
    void konsistenstestForÅSjekkeAtSeraliseringFungereUavhengigAvDeseralisering() throws JsonProcessingException {
        var meldekortUtbetalingsgrunnlagSakDto = getMeldekortUtbetalingsgrunnlagSakDto();
        var seralized = MAPPER.writeValueAsString(meldekortUtbetalingsgrunnlagSakDto);
        assertThat(seralized).isEqualToIgnoringWhitespace(hardkodetSeralisertStreng().replaceAll("[\n\r ]", ""));
    }

    private static String hardkodetSeralisertStreng() {
        return """
             {
                "kilde": "ARENA",
                "kravMottattDato": "2022-08-24",
                "meldekortene": [
                    {
                        "beløp": 10,
                        "dagsats": 1,
                        "meldekortFom": "2022-08-24",
                        "meldekortTom": "2022-08-29",
                        "utbetalingsgrad": 10
                    }
                ],
                "sakStatus": "AKTIV",
                "saksnummer": "1234567890",
                "tilstand": "LOP",
                "type": "DAG",
                "vedtakStatus": "IVERK",
                "vedtaksDagsats": 809.0,
                "vedtaksPeriodeFom": "2022-12-27",
                "vedtaksPeriodeTom": "2023-01-06",
                "vedtattDato": "2022-08-24"
            }
            """;
    }


    static MeldekortUtbetalingsgrunnlagSak getMeldekortUtbetalingsgrunnlagSakDto() {
        return new MeldekortUtbetalingsgrunnlagSak(
            Fagsystem.ARENA,
            LocalDate.of(2022, 8, 24),
            List.of(getMeldekortUtbetalingsgrunnlagMeldekortDto()),
            "AKTIV",
            "1234567890",
            YtelseStatus.LØPENDE,
            YtelseType.DAGPENGER,
            "IVERK",
            new Beløp(BigDecimal.valueOf(809.0)),
            LocalDate.of(2022, 12, 27),
            LocalDate.of(2023, 01, 6),
            LocalDate.of(2022, 8, 24));
    }

    private static MeldekortUtbetalingsgrunnlagMeldekort getMeldekortUtbetalingsgrunnlagMeldekortDto() {
        return new MeldekortUtbetalingsgrunnlagMeldekort(
            BigDecimal.TEN, BigDecimal.ONE,
            LocalDate.of(2022, 8, 24),
            LocalDate.of(2022, 8, 29),
            BigDecimal.TEN);
    }
}
