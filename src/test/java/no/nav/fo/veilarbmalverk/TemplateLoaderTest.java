package no.nav.fo.veilarbmalverk;

import io.vavr.collection.HashMap;
import io.vavr.control.Option;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

class TemplateLoaderTest {
    @Test
    void should_read_file_into_class() {
        Option<HashMap<String, Object>> cvAktivitet = TemplateLoader.get("cv_aktivitet");

        assertThat(cvAktivitet.isSingleValued()).isTrue();
        assertThat(cvAktivitet.get().get("type").isSingleValued()).isTrue();
        assertThat(cvAktivitet.get().get("tittel").isSingleValued()).isTrue();
        assertThat(cvAktivitet.get().get("hensikt").isSingleValued()).isTrue();
    }

    @Test
    void should_give_option_none_if_it_doesnt_exist() {
        Option<HashMap<String, Object>> cvAktivitet = TemplateLoader.get("fake-task");

        assertThat(cvAktivitet.isEmpty()).isTrue();
    }

    @Test
    void should_give_option_none_if_marshalling_fails() {
        Option<HashMap<String, Object>> cvAktivitet = TemplateLoader.get("test_cv_aktivitet");

        assertThat(cvAktivitet.isEmpty()).isTrue();
    }
}