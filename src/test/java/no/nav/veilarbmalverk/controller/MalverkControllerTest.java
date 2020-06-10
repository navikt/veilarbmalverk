package no.nav.veilarbmalverk.controller;

import no.nav.veilarbmalverk.TemplateLoader;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MalverkControllerTest {
    private static final Predicate<String> extrapolatorVariable = Pattern.compile("\\{(.+?)\\}").asPredicate();
    private final MalverkController controller = new MalverkController();
    private final String templateName = TemplateLoader.list().get(0);

    @Test
    void getTemplateList() {
        List<String> templateList = controller.getTemplateList();
        assertThat(templateList).isNotEmpty();
    }

    @Test
    void getTemplateExtrapolated() {
        Map<String, Object> template = controller.getTemplateExtrapolated(templateName);
        boolean noExtrapolatorVariable = template
                .values()
                .stream()
                .noneMatch(MalverkControllerTest::hasVariable);

        assertThat(noExtrapolatorVariable).isTrue();
    }

    @Test
    void getTemplateExtrapolated_should_throw_on_unknown_template() {
        Exception e = assertThrows(
                ResponseStatusException.class,
                () -> controller.getTemplateExtrapolated("dummy-name")
        );
        assertThat(e.getMessage()).isEqualTo("404 NOT_FOUND");
    }

    @Test
    void getTemplate_returns_raw_template() {
        Map<String, Object> template = controller.getTemplate(templateName);
        boolean foundExtrapolatorVariable = template
                .values()
                .stream()
                .anyMatch(MalverkControllerTest::hasVariable);

        assertThat(foundExtrapolatorVariable).isTrue();
    }

    @Test
    void getTemplate_should_throw_on_unknown_template() {
        Exception e = assertThrows(
                ResponseStatusException.class,
                () -> controller.getTemplate("dummy-name")
        );

        assertThat(e.getMessage()).isEqualTo("404 NOT_FOUND");
    }

    @Test
    void should_filter() {
        Map<String, Object> allMatchFilter = new HashMap<>();
        Map<String, Object> noMatchFilter = new HashMap<>();
        noMatchFilter.put("type", "NOMATCH-1234");

        assertThat(controller.filter(allMatchFilter).size()).isGreaterThan(0);
        assertThat(controller.filter(noMatchFilter).size()).isEqualTo(0);
    }

    private static boolean hasVariable(Object obj) {
        if (obj instanceof String) {
            String str = ((String) obj);
            return extrapolatorVariable.test(str);
        }
        return false;
    }
}