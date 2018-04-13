package no.nav.fo.veilarbmalverk;

import org.junit.jupiter.api.Test;

import java.time.*;

import static org.assertj.core.api.Java6Assertions.assertThat;

class ExtrapolatorTest {
    private final ZonedDateTime now = ZonedDateTime.of(2018, 3, 20, 10, 0, 0, 0, ZoneId.of("Z"));
    private final Clock clock = Clock.fixed(now.toInstant(), now.getZone());

    private final Extrapolator extrapolator = new Extrapolator(clock);

    @Test
    public void should_extrapolate_multiple() {
        assertThat(extrapolator.extrapolate("DATA {now+12t} BETWEEN { now + 1d } VARS"))
                .isEqualTo("DATA 2018-03-20T22:00:00.000Z BETWEEN 2018-03-21T10:00:00.000Z VARS");
    }

    @Test
    public void should_extrapolate_now() {
        assertThat(extrapolator.extrapolate(prep("{now}"))).isEqualTo(prep("2018-03-20T10:00:00.000Z"));
        assertThat(extrapolator.extrapolate(prep("{now }"))).isEqualTo(prep("2018-03-20T10:00:00.000Z"));
        assertThat(extrapolator.extrapolate(prep("{ now}"))).isEqualTo(prep("2018-03-20T10:00:00.000Z"));
        assertThat(extrapolator.extrapolate(prep("{ now }"))).isEqualTo(prep("2018-03-20T10:00:00.000Z"));
    }

    @Test
    public void should_force_zulu_time() {
        ZonedDateTime offsetNow = ZonedDateTime.of(2018, 3, 20, 10, 0, 0, 0, ZoneId.of("+1"));
        Clock offsetClock = Clock.fixed(offsetNow.toInstant(), offsetNow.getZone());
        Extrapolator offsetExtrapolator = new Extrapolator(offsetClock);

        assertThat(offsetExtrapolator.extrapolate(prep("{now}"))).isEqualTo(prep("2018-03-20T09:00:00.000Z"));
    }

    @Test
    void should_extrapolate_and_calculate_dates_in_future() {
        assertThat(extrapolator.extrapolate(prep("{now + 7d}"))).isEqualTo(prep("2018-03-27T10:00:00.000Z"));
        assertThat(extrapolator.extrapolate(prep("{now + 7d }"))).isEqualTo(prep("2018-03-27T10:00:00.000Z"));
        assertThat(extrapolator.extrapolate(prep("{ now +7d}"))).isEqualTo(prep("2018-03-27T10:00:00.000Z"));
        assertThat(extrapolator.extrapolate(prep("{ now+7d }"))).isEqualTo(prep("2018-03-27T10:00:00.000Z"));
    }

    @Test
    void should_support_environment() {
        System.setProperty("FASIT_ENVIRONMENT_NAME", "p");
        assertThat(extrapolator.extrapolate(prep("{miljo}"))).isEqualTo(prep(""));
        assertThat(extrapolator.extrapolate(prep("https://tjenester{miljo}.nav.no/test")))
                .isEqualTo(prep("https://tjenester.nav.no/test"));

        System.setProperty("FASIT_ENVIRONMENT_NAME", "t6");
        assertThat(extrapolator.extrapolate(prep("{miljo}"))).isEqualTo(prep("-t6"));
        assertThat(extrapolator.extrapolate(prep("https://tjenester{miljo}.nav.no/test")))
                .isEqualTo(prep("https://tjenester-t6.nav.no/test"));
    }

    @Test
    void should_handle_null_strings() {
        assertThat(extrapolator.extrapolate(null)).isEqualTo(null);
    }

    private static String prep(String s) {
        return String.format("random data%s more random data", s);
    }
}