package no.nav.veilarbmalverk;

import java.util.Map;
import java.util.function.Predicate;

public class FilterUtils {

        public static Predicate<Map<String, Object>> createFilter(Map<String, Object> filter) {
            return (Map<String, Object> template) -> filter
                    .keySet()
                    .stream()
                    .allMatch(key -> filter.get(key).equals(template.get(key)));
        }

}
