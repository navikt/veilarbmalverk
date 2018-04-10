package no.nav.fo.veilarbmalverk;

import com.fasterxml.jackson.core.type.TypeReference;
import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;

@Slf4j
public class TemplateLoader {
    private static Map<String, String> templates;
    private static TypeReference<java.util.HashMap<String, Object>> mapTypeReference = new TypeReference<java.util.HashMap<String, Object>>() {
    };

    public static List<String> list() {
        ensureLoaded();

        return templates.keySet().toList();
    }

    public static Option<HashMap<String, Object>> get(String name) {
        ensureLoaded();

        return templates
                .get(name)
                .onEmpty(() -> log.warn("Requested '" + name + "', but template does not exist."))
                .flatMap((content) -> readvalue(content).toOption());
    }

    private static Either<Exception, HashMap<String, Object>> readvalue(String content) {
        try {
            java.util.Map<String, Object> right = SerializerUtils.mapper.readValue(content, mapTypeReference);
            return Either.right(HashMap.ofAll(right));
        } catch (Exception e) {
            log.error("Parsing content", e);
            return Either.left(e);
        }
    }

    private static Map<String, String> readAllFiles() {
        return Option.of(TemplateLoader.class.getResource("/templates"))
                .map(URL::getFile)
                .map(File::new)
                .map(TemplateLoader::listFiles)
                .getOrElse(List.empty())
                .map((file) -> Tuple.of(findName(file), readFile(file).getOrElse((String) null)))
                .toMap(Function.identity());
    }

    private static String findName(File file) {
        String filenameWithExtension = file.getName();
        int extensionIndex = filenameWithExtension.lastIndexOf(".");

        return filenameWithExtension.substring(0, extensionIndex);
    }

    private static Either<Exception, String> readFile(File file) {
        try {
            return Either.right(new String(Files.readAllBytes(Paths.get(file.getAbsolutePath()))));
        } catch (Exception e) {
            log.error("Reading file", e);
            return Either.left(e);
        }
    }

    private static List<File> listFiles(File directory) {
        return Option.of(directory.listFiles())
                .map(List::of)
                .getOrElse(List.empty())
                .flatMap((File file) -> {
                    if (file.isFile()) {
                        return List.of(file);
                    } else {
                        return listFiles(directory);
                    }
                });
    }

    private static void ensureLoaded() {
        if (templates == null) {
            templates = readAllFiles();
        }
    }
}
