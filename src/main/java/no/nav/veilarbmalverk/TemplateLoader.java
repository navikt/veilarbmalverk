package no.nav.veilarbmalverk;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class TemplateLoader {
    private static Map<String, String> templates;
    private static TypeReference<java.util.HashMap<String, Object>> mapTypeReference = new TypeReference<>() {};

    public static List<String> list() {
        ensureLoaded();

        return templates.keySet().stream().collect(Collectors.toList());
    }

    public static Optional<HashMap<String, Object>> get(String name) {
        ensureLoaded();

        var value = templates.get(name);
        return Optional.ofNullable(readvalue(value));
    }

    private static HashMap<String, Object> readvalue(String content) {
        try {
            return SerializerUtils.mapper.readValue(content, mapTypeReference);
        } catch (Exception e) {
            log.error("Parsing content", e);
            return null;
        }
    }

    private static Map<String, String> readAllFiles() {
       return Map.of(
               "cv_jobbprofil_aktivitet", getResourceFileAsString("templates/cv_jobbprofil_aktivitet.json"),
               "soke_jobber_aktivitet", getResourceFileAsString("templates/soke_jobber_aktivitet.json")
       );
    }

    @SneakyThrows
    private static String getResourceFileAsString(String fileName) {
        ClassLoader classLoader = VeilarbmalverkApp.class.getClassLoader();
        try (InputStream resourceStream = classLoader.getResourceAsStream(fileName)) {
            return new String(resourceStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static void ensureLoaded() {
        if (templates == null) {
            templates = readAllFiles();
        }
    }
}
