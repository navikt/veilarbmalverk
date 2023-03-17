package no.nav.veilarbmalverk;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class FilterUtilsTest {
    @Test
    void should_match_single() {
        assertTrue(lagPredicate("fraDato", "{now}").test(lagMap("fraDato", "{now}")));
        assertFalse(lagPredicate("fraDato", "{now}").test(lagMap("fraDato", "{not_now}")));
        assertTrue(lagPredicate("type", "EGEN").test(lagMap("type", "EGEN")));
        assertTrue(lagPredicate("type", "SOKEAVTALE").test(lagMap("type", "SOKEAVTALE")));
        assertTrue(lagPredicate("type", 0).test(lagMap("type", 0)));
        assertTrue(lagPredicate("type", "EGEN").test(lagMap("type", "EGEN", "fraDato", "skjer")));
        assertFalse(lagPredicate("type", "EGEN").test(lagMap("unknown", "EGENS", "other", "value")));
        assertFalse(lagPredicate("type", "EGEN").test(lagMap("type", "EGENS", "other", "value")));
    }

    private Map<String, Object> lagMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    private Map<String, Object> lagMap(String key, Object value, String key2, Object value2) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        map.put(key2, value2);
        return map;
    }

    private Predicate<Map<String, Object>> lagPredicate(String key, Object value) {
        return FilterUtils.createFilter(lagMap(key, value));
    }
}