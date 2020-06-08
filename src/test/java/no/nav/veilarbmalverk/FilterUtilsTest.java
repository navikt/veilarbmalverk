package no.nav.veilarbmalverk;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static org.assertj.core.api.Java6Assertions.assertThat;

class FilterUtilsTest {
    @Test
    public void should_match_single() {
        assertThat(lagPredicate("fraDato", "{now}").test(lagMap("fraDato", "{now}"))).isTrue();
        assertThat(lagPredicate("fraDato", "{now}").test(lagMap("fraDato", "{not_now}"))).isFalse();
        assertThat(lagPredicate("type", "EGEN").test(lagMap("type", "EGEN"))).isTrue();
        assertThat(lagPredicate("type", "SOKEAVTALE").test(lagMap("type", "SOKEAVTALE"))).isTrue();
        assertThat(lagPredicate("type", 0).test(lagMap("type", 0))).isTrue();
        assertThat(lagPredicate("type", "EGEN").test(lagMap("type", "EGEN", "fraDato", "skjer"))).isTrue();
        assertThat(lagPredicate("type", "EGEN").test(lagMap("unknown", "EGENS", "other", "value"))).isFalse();
        assertThat(lagPredicate("type", "EGEN").test(lagMap("type", "EGENS", "other", "value"))).isFalse();
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