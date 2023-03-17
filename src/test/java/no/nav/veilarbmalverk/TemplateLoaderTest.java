package no.nav.veilarbmalverk;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TemplateLoaderTest {

    private static boolean isPresent(Object ob){
        return ob != null;
    }

    @Test
    void should_read_file_into_class() {
        var cvAktivitet = TemplateLoader.get("cv_jobbprofil_aktivitet");

        assertTrue(cvAktivitet.isPresent());
        assertTrue(isPresent(cvAktivitet.get().get("type")));
        assertTrue(isPresent(cvAktivitet.get().get("tittel")));
        assertTrue(isPresent(cvAktivitet.get().get("hensikt")));
    }

    @Test
    void should_give_option_none_if_it_doesnt_exist() {
        var cvAktivitet = TemplateLoader.get("fake-task");

        assertTrue(cvAktivitet.isEmpty());
    }

    @Test
    void should_give_option_none_if_marshalling_fails() {
        var cvAktivitet = TemplateLoader.get("test_cv_aktivitet");

        assertTrue(cvAktivitet.isEmpty());
    }
}