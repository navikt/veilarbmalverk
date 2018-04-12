package no.nav.fo.veilarbmalverk;

import no.bekk.bekkopen.date.NorwegianDateUtil;

import java.sql.Date;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtils {
    public static final DateTimeFormatter ISO8601 = DateTimeFormatter.ofPattern("yyy-MM-dd'T'HH:mm:ss.SSSX");
    private static final String MONTHS = "m";
    private static final String WEEKS = "u";
    private static final String DAYS = "d";
    private static final String HOURS = "t";

    public static Pattern pattern = Pattern.compile("(?:now\\+)?(\\d+[" + MONTHS + WEEKS + DAYS + HOURS + "])");

    static ZonedDateTime relativeTime(ZonedDateTime now, String relative) {
        ZonedDateTime time = now;
        Matcher matcher = pattern.matcher(relative);

        while (matcher.find()) {
            time = createTimeFunction(matcher.group(1)).apply(time);
        }

        return fastForwardToFirstWorkingDay(time);
    }

    private static ZonedDateTime fastForwardToFirstWorkingDay(ZonedDateTime startDate) {
        ZonedDateTime date = startDate;

        while (!NorwegianDateUtil.isWorkingDay(Date.valueOf(date.toLocalDate()))) {
            date = date.plusDays(1);
        }

        return date;
    }

    private static Function<ZonedDateTime, ZonedDateTime> createTimeFunction(String time) {
        return (date) -> {
            int number = Integer.parseInt(time.substring(0, time.length() - 1), 10);
            if (time.endsWith(MONTHS)) {
                return date.plusMonths(number);
            } else if (time.endsWith(WEEKS)) {
                return date.plusWeeks(number);
            } else if (time.endsWith(DAYS)) {
                return date.plusDays(number);
            } else if (time.endsWith(HOURS)) {
                return date.plusHours(number);
            } else {
                return date;
            }
        };
    }
}
