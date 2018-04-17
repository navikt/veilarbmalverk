package no.nav.fo.veilarbmalverk;

import io.vavr.Tuple;

import java.util.Map;
import java.util.function.Predicate;

public class FilterUtils {

        public static Predicate<Map<String, Object>> createFilter(Map<String, Object> filter) {
            return (Map<String, Object> template) -> filter
                    .keySet()
                    .stream()
                    .map((key) -> Tuple.of(filter.get(key), template.get(key)))
                    .allMatch((tuple) -> tuple._1.equals(tuple._2));
        }

}
