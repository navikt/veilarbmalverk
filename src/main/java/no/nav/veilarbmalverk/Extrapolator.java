package no.nav.veilarbmalverk;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static no.nav.common.utils.EnvironmentUtils.isDevelopment;
import static no.nav.veilarbmalverk.TimeUtils.ISO8601;

public class Extrapolator {
    private final Clock clock;
    private final Pattern pattern = Pattern.compile("\\{(.+?)\\}");
    private final Map<Predicate<String>, Function<String, String>> rules = Map.of(
            regexPredicate(TimeUtils.pattern), this::relativeTime,
            equalsPredicate("now"), this::now,
            equalsPredicate("miljo"), this::miljo,
            equalsPredicate("lenke"), this::lenke
    );

    public Extrapolator() {
        this(Clock.systemDefaultZone());
    }

    Extrapolator(Clock clock) {
        this.clock = clock;
    }

    private String relativeTime(String time) {
        return ISO8601.format(TimeUtils.relativeTime(Instant.now(clock), time));
    }

    private String now(String dontCare) {
        return ISO8601.format(Instant.now(clock));
    }

    private String lenke(String dontCare) {
        if (isDevelopment().isPresent() && isDevelopment().get()) {
            return "https://www.ansatt.dev.nav.no/min-cv";
        } else {
            return "https://www.nav.no/min-cv";
        }
    }

    private String miljo(String dontCare) {
        if (isDevelopment().isPresent() && isDevelopment().get()) {
            return ".intern.dev";
        } else {
            return "";
        }
    }

    public String extrapolate(String s) {
        if (s == null || "".equals(s)) {
            return s;
        }

        int last = 0;
        StringBuilder sb = new StringBuilder();

        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            sb.append(s.substring(last, matcher.start()));

            String templatename = matcher.group(1).replaceAll("\\s", "");

            rules.keySet()
                    .stream()
                    .filter(r -> r.test(templatename))
                    .findFirst()
                    .map(rules::get)
                    .ifPresent(fun -> sb.append(fun.apply(templatename)));
            last = matcher.end();
        }
        if (last != s.length()) {
            sb.append(s.substring(last, s.length()));
        }

        return sb.toString();
    }

    private static Predicate<String> regexPredicate(Pattern regex) {
        return (s) -> regex.matcher(s).matches();
    }

    private static Predicate<String> equalsPredicate(String match) {
        return match::equals;
    }
}
