package no.nav.fo.veilarbmalverk;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static no.nav.fo.veilarbmalverk.TimeUtils.ZULU;
import static no.nav.fo.veilarbmalverk.TimeUtils.plus;
import static org.assertj.core.api.Java6Assertions.assertThat;

class TimeUtilsTest {
    static ZoneId zoneId = ZoneId.of("Z");
    static Instant now = ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, zoneId).toInstant();

    @Test
    void plus_hours() {
        Instant future = TimeUtils.relativeTime(now, "12t");

        // 1970.1.1 is a holyday, hence +1d
        assertThat(future).isEqualByComparingTo(now.plus(12, ChronoUnit.HOURS).plus(1, ChronoUnit.DAYS));
    }

    @Test
    void plus_days() {
        Instant future = TimeUtils.relativeTime(now, "5d");
        assertThat(future).isEqualByComparingTo(now.plus(5, ChronoUnit.DAYS));
    }

    @Test
    void plus_weeks() {
        Instant future = TimeUtils.relativeTime(now, "5u");
        assertThat(future).isEqualByComparingTo(plus(now, 5, ChronoUnit.WEEKS));
    }

    @Test
    void plus_months() {
        Instant future = TimeUtils.relativeTime(now, "2m");

        // 1970.3.1 is a sunday, hence +1d
        assertThat(future).isEqualByComparingTo(plus(plus(now, 2, ChronoUnit.MONTHS), 1, ChronoUnit.DAYS));
    }

    @Test
    void should_return_working_day() {
        // now: 1970.1.1 thursday
        // +2d: 1970.1.3 saturday is not a working day, fastforward to next monday 1970.1.5
        Instant future = TimeUtils.relativeTime(now, "2d");
        assertThat(future).isEqualByComparingTo(now.plus(4, ChronoUnit.DAYS));

        // now: 1970.1.1 thursday
        // +3d: 1970.1.4 sunday is not a working day, fastforward to next monday 1970.1.5
        Instant future2 = TimeUtils.relativeTime(now, "3d");
        assertThat(future).isEqualByComparingTo(now.plus(4, ChronoUnit.DAYS));

        // now: 2018.3.28 wednesday
        // +1d: 2018.3.29 thursday is not a working day (easter), fastforward to 2018.4.3
        Instant easter = ZonedDateTime.of(2018, 3, 28, 0, 0, 0, 0, zoneId).toInstant();
        Instant future3 = TimeUtils.relativeTime(easter, "1d");
        assertThat(future3).isEqualByComparingTo(easter.plus(6, ChronoUnit.DAYS));
    }

    @Test
    void plus_combination() {
        Instant future = TimeUtils.relativeTime(now, "2m5u5d12t");
        Instant future2 = TimeUtils.relativeTime(now, "2m 5u 5d 12t");

        Instant expected = now
                .atZone(ZULU)
                .plusMonths(2)
                .plusWeeks(5)
                .plusDays(5)
                .plusHours(12)
                .toInstant();
        assertThat(future).isEqualByComparingTo(expected);
        assertThat(future2).isEqualByComparingTo(expected);
    }
}