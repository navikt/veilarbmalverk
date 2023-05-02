package no.nav.veilarbmalverk;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ExtrapolatorTest {
    private final ZonedDateTime now = ZonedDateTime.of(2018, 3, 20, 10, 0, 0, 0, ZoneId.of("Z"));
    private final Clock clock = Clock.fixed(now.toInstant(), now.getZone());

    private final Extrapolator extrapolator = new Extrapolator(clock);

    @Test
    void should_extrapolate_multiple() {
        assertThat(extrapolator.extrapolate("DATA {now+12t} BETWEEN { now + 1d } VARS"))
                .isEqualTo("DATA 2018-03-20T22:00:00.000Z BETWEEN 2018-03-21T10:00:00.000Z VARS");
    }

    @Test
    void should_extrapolate_now() {
        assertThat(extrapolator.extrapolate(prep("{now}"))).isEqualTo(prep("2018-03-20T10:00:00.000Z"));
        assertThat(extrapolator.extrapolate(prep("{now }"))).isEqualTo(prep("2018-03-20T10:00:00.000Z"));
        assertThat(extrapolator.extrapolate(prep("{ now}"))).isEqualTo(prep("2018-03-20T10:00:00.000Z"));
        assertThat(extrapolator.extrapolate(prep("{ now }"))).isEqualTo(prep("2018-03-20T10:00:00.000Z"));
    }

    @Test
    void should_force_zulu_time() {
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
        System.setProperty("NAIS_CLUSTER_NAME", "prod-fss");
        assertThat(extrapolator.extrapolate(prep("{miljo}"))).isEqualTo(prep(""));
        assertThat(extrapolator.extrapolate(prep("https://tjenester{miljo}.nav.no/test")))
                .isEqualTo(prep("https://tjenester.nav.no/test"));

        System.setProperty("NAIS_CLUSTER_NAME", "dev-fss");
        assertThat(extrapolator.extrapolate(prep("{miljo}"))).isEqualTo(prep(".intern.dev"));
        assertThat(extrapolator.extrapolate(prep("https://tjenester{miljo}.nav.no/test")))
                .isEqualTo(prep("https://tjenester.intern.dev.nav.no/test"));

        System.setProperty("NAIS_CLUSTER_NAME", "dev-gcp");
        assertThat(extrapolator.extrapolate(prep("https://blee{miljo}.nav.no/test")))
                .isEqualTo(prep("https://blee.intern.dev.nav.no/test"));


        System.setProperty("NAIS_CLUSTER_NAME", "prod-gcp");
        assertThat(extrapolator.extrapolate(prep("https://blee{miljo}.nav.no/test")))
                .isEqualTo(prep("https://blee.nav.no/test"));
    }

    @Test
    void should_handle_null_strings() {
        assertThat(extrapolator.extrapolate(null)).isNull();
    }

    private static String prep(String s) {
        return String.format("random data%s more random data", s);
    }
}