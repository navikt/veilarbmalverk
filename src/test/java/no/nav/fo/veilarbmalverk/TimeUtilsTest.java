package no.nav.fo.veilarbmalverk;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Java6Assertions.assertThat;

class TimeUtilsTest {
    static ZoneId zoneId = ZoneId.of("Z");
    static ZonedDateTime now = ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, zoneId);

    @Test
    void plus_hours() {
        ZonedDateTime future = TimeUtils.relativeTime(now, "12t");

        // 1970.1.1 is a holyday, hence +1d
        assertThat(future).isEqualByComparingTo(ZonedDateTime.of(LocalDateTime.of(1970, 1, 2, 12, 0), zoneId));
    }

    @Test
    void plus_days() {
        ZonedDateTime future = TimeUtils.relativeTime(now, "5d");
        assertThat(future).isEqualByComparingTo(ZonedDateTime.of(LocalDateTime.of(1970, 1, 6, 0, 0), zoneId));
    }

    @Test
    void plus_weeks() {
        ZonedDateTime future = TimeUtils.relativeTime(now, "5u");
        assertThat(future).isEqualByComparingTo(ZonedDateTime.of(LocalDateTime.of(1970, 2, 5, 0, 0), zoneId));
    }

    @Test
    void plus_months() {
        ZonedDateTime future = TimeUtils.relativeTime(now, "2m");

        // 1970.3.1 is a sunday, hence +1d
        assertThat(future).isEqualByComparingTo(ZonedDateTime.of(LocalDateTime.of(1970, 3, 2, 0, 0), zoneId));
    }

    @Test
    void should_return_working_day() {
        // now: 1970.1.1 thursday
        // +2d: 1970.1.3 saturday is not a working day, fastforward to next monday 1970.1.5
        ZonedDateTime future = TimeUtils.relativeTime(now, "2d");
        assertThat(future).isEqualByComparingTo(ZonedDateTime.of(LocalDateTime.of(1970, 1, 5, 0, 0), zoneId));

        // now: 1970.1.1 thursday
        // +3d: 1970.1.4 saturday is not a working day, fastforward to next monday 1970.1.5
        ZonedDateTime future2 = TimeUtils.relativeTime(now, "2d");
        assertThat(future2).isEqualByComparingTo(ZonedDateTime.of(LocalDateTime.of(1970, 1, 5, 0, 0), zoneId));

        // now: 2018.3.28 wednesday
        // +1d: 2018.3.29 thursday is not a working day (easter), fastforward to 2018.4.3
        ZonedDateTime easter = ZonedDateTime.of(2018, 3, 28, 0, 0, 0, 0, zoneId);
        ZonedDateTime future3 = TimeUtils.relativeTime(easter, "1d");
        assertThat(future3).isEqualByComparingTo(ZonedDateTime.of(LocalDateTime.of(2018, 4, 3, 0, 0), zoneId));
    }

    @Test
    void plus_combination() {
        ZonedDateTime future = TimeUtils.relativeTime(now, "2m5u5d12t");
        ZonedDateTime future2 = TimeUtils.relativeTime(now, "2m 5u 5d 12t");

        ZonedDateTime expected = ZonedDateTime.of(LocalDateTime.of(1970, 4, 10, 12, 0), zoneId);
        assertThat(future).isEqualByComparingTo(expected);
        assertThat(future2).isEqualByComparingTo(expected);
    }
}