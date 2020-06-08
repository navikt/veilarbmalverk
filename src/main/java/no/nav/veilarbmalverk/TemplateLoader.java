package no.nav.veilarbmalverk;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
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
        return Optional.ofNullable(TemplateLoader.class.getResource("/templates"))
                .map(URL::getFile)
                .map(File::new)
                .map(TemplateLoader::listFiles)
                .orElseGet(Collections::emptyList)
                .stream()
                .collect(Collectors.toMap(TemplateLoader::findName, TemplateLoader::readFile));
    }

    private static String findName(File file) {
        String filenameWithExtension = file.getName();
        int extensionIndex = filenameWithExtension.lastIndexOf(".");

        return filenameWithExtension.substring(0, extensionIndex);
    }

    private static String readFile(File file) {
        try {
            return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        } catch (Exception e) {
            log.error("Reading file", e);
            return "";
        }
    }

    private static List<File> listFiles(File directory) {
        return Optional.ofNullable(directory.listFiles())
                .map(List::of)
                .orElse(Collections.emptyList())
                .stream()
                .flatMap((file) -> {
                    if (file.isFile()) {
                        return List.of(file).stream();
                    } else {
                        return listFiles(directory).stream();
                    }
                })
                .collect(Collectors.toList());
    }

    private static void ensureLoaded() {
        if (templates == null) {
            templates = readAllFiles();
        }
    }
}
