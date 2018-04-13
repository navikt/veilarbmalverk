package no.nav.fo.veilarbmalverk;

import no.bekk.bekkopen.date.NorwegianDateUtil;

import java.sql.Date;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

public class TimeUtils {
    private static final List<TemporalUnit> SUPPORTED_TEMPORALUNITS_INSTANT = asList(
            ChronoUnit.NANOS,
            ChronoUnit.MICROS,
            ChronoUnit.MILLIS,
            ChronoUnit.SECONDS,
            ChronoUnit.MINUTES,
            ChronoUnit.HOURS,
            ChronoUnit.HALF_DAYS,
            ChronoUnit.DAYS
    );
    public static final ZoneId ZULU = ZoneId.of("Z");
    public static final DateTimeFormatter ISO8601 = DateTimeFormatter
            .ofPattern("yyy-MM-dd'T'HH:mm:ss.SSSX")
            .withZone(ZULU);

    private static final String MONTHS = "m";
    private static final String WEEKS = "u";
    private static final String DAYS = "d";
    private static final String HOURS = "t";

    public static Pattern pattern = Pattern.compile("(?:now\\+)?(\\d+[" + MONTHS + WEEKS + DAYS + HOURS + "])");

    static Instant relativeTime(Instant now, String relative) {
        Instant time = now;
        Matcher matcher = pattern.matcher(relative);

        while (matcher.find()) {
            time = createTimeFunction(matcher.group(1)).apply(time);
        }

        return fastForwardToFirstWorkingDay(time);
    }

    private static Instant fastForwardToFirstWorkingDay(Instant startInstant) {
        Instant instant = startInstant;
        while (!NorwegianDateUtil.isWorkingDay(Date.from(instant))) {
            instant = instant.plus(1, ChronoUnit.DAYS);
        }

        return instant;
    }

    private static Function<Instant, Instant> createTimeFunction(String time) {
        return (instant) -> {
            int number = Integer.parseInt(time.substring(0, time.length() - 1), 10);
            if (time.endsWith(MONTHS)) {
                return plus(instant, number, ChronoUnit.MONTHS);
            } else if (time.endsWith(WEEKS)) {
                return plus(instant, number, ChronoUnit.WEEKS);
            } else if (time.endsWith(DAYS)) {
                return plus(instant, number, ChronoUnit.DAYS);
            } else if (time.endsWith(HOURS)) {
                return plus(instant, number, ChronoUnit.HOURS);
            } else {
                return instant;
            }
        };
    }

    public static Instant plus(Instant start, long add, TemporalUnit unit) {
        if (SUPPORTED_TEMPORALUNITS_INSTANT.contains(unit)) {
            return start.plus(add, unit);
        } else {
            return start.atZone(ZULU).plus(add, unit).toInstant();
        }
    }
}
