package no.nav.veilarbmalverk.controller;

import no.nav.veilarbmalverk.Extrapolator;
import no.nav.veilarbmalverk.FilterUtils;
import no.nav.veilarbmalverk.TemplateLoader;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mal")
public class MalverkController {

    private Extrapolator extrapolator = new Extrapolator();

    @GetMapping
    public List<String> getTemplateList() {
        return TemplateLoader.list();
    }

    @GetMapping("/{name}")
    public Map<String, Object> getTemplateExtrapolated(@PathVariable("name") String name) {
        return TemplateLoader.get(name)
                .map(this::extrapolate)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{name}/raw")
    public Map<String, Object> getTemplate(@PathVariable("name") String name) {
        return TemplateLoader.get(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public List<Map<String, Object>> filter(Map<String, Object> filter) {
        Predicate<Map<String, Object>> predicate = FilterUtils.createFilter(filter);
        return TemplateLoader.list()
                .stream()
                .map(TemplateLoader::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::extrapolate)
                .filter(predicate)
                .collect(Collectors.toList());
    }

    private Map<String, Object> extrapolate(HashMap<String, Object> template) {
        return template
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> {
                    var value = e.getValue();
                    if (value instanceof String){
                        return extrapolator.extrapolate((String) value);
                    }
                    return value;
                }));
    }
}
