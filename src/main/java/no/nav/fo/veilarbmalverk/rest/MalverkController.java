package no.nav.fo.veilarbmalverk.rest;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.control.Option;
import no.nav.fo.veilarbmalverk.Extrapolator;
import no.nav.fo.veilarbmalverk.FilterUtils;
import no.nav.fo.veilarbmalverk.TemplateLoader;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Service
@Path("/mal")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MalverkController {

    private Extrapolator extrapolator = new Extrapolator();

    @GET
    public List<String> getTemplateList() {
        return TemplateLoader.list().toJavaList();
    }

    @GET
    @Path("/{name}")
    public Map<String, Object> getTemplateExtrapolated(@PathParam("name") String name) {
        return TemplateLoader.get(name)
                .map(this::extrapolate)
                .map(io.vavr.collection.Map::toJavaMap)
                .getOrElseThrow(() -> new NotFoundException("Template not found: " + name));
    }

    @GET
    @Path("/{name}/raw")
    public Map<String, Object> getTemplate(@PathParam("name") String name) {
        return TemplateLoader.get(name)
                .map(io.vavr.collection.Map::toJavaMap)
                .getOrElseThrow(() -> new NotFoundException("Template not found: " + name));
    }

    @POST
    public List<Map<String, Object>> filter(Map<String, Object> filter) {
        Predicate<Map<String, Object>> predicate = FilterUtils.createFilter(filter);
        return TemplateLoader.list()
                .map(TemplateLoader::get)
                .filter(Option::isSingleValued)
                .map(Option::get)
                .map(this::extrapolate)
                .map(io.vavr.collection.Map::toJavaMap)
                .filter(predicate)
                .toJavaList();
    }

    private HashMap<String, Object> extrapolate(HashMap<String, Object> template) {
        return template.mapValues((value) -> {
            if (value instanceof String) {
                return extrapolator.extrapolate((String) value);
            }

            return value;
        });
    }
}
