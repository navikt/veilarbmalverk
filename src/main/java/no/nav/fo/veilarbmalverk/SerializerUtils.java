package no.nav.fo.veilarbmalverk;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.SneakyThrows;
import no.nav.json.JsonProvider;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.function.Function;

public class SerializerUtils {
    public final static ObjectMapper mapper = JsonProvider.createObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.registerModule(new ParameterNamesModule());
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JSR310Module());
    }

    @SneakyThrows
    public static String serialize(Object object) {
        return mapper.writeValueAsString(object);
    }

    public static Date deserializeToDate(String date) {
        ZonedDateTime zdt = ZonedDateTime.of(LocalDateTime.parse(date), ZoneId.systemDefault());
        Calendar cal = GregorianCalendar.from(zdt);
        return cal.getTime();
    }

    public static LocalDateTime deserialize(Timestamp timestamp) {
        return nullAllowed(timestamp, Timestamp::toLocalDateTime);
    }

    private static <S, T> T nullAllowed(S s, Function<S, T> mapper) {
        if (s == null) {
            return null;
        }
        return mapper.apply(s);
    }
}
