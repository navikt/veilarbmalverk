package no.nav.veilarbmalverk.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@SpringBootTest
@AutoConfigureMockMvc
class MalverkControllerITest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void should_get_template_array() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/mal"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[\"cv_jobbprofil_aktivitet\",\"soke_jobber_aktivitet\"]"));

    }

    @Test
    @SneakyThrows
    void should_get_template_content() {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/mal/{malnavn}", "soke_jobber_aktivitet"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);

        Assertions.assertTrue(jsonNode.has("type"));
        Assertions.assertTrue(jsonNode.has("status"));
        Assertions.assertTrue(jsonNode.has("tittel"));
        Assertions.assertTrue(jsonNode.has("beskrivelse"));
        Assertions.assertTrue(jsonNode.has("fraDato"));
        Assertions.assertTrue(jsonNode.has("tilDato"));
        Assertions.assertTrue(jsonNode.has("malid"));


        ZonedDateTime fraDato = ZonedDateTime.parse(jsonNode.get("fraDato").asText());

        assertThat(fraDato).isCloseTo(ZonedDateTime.now(), within(100, ChronoUnit.MILLIS));
    }

    @Test
    @SneakyThrows
    void should_get_template_raw() {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/mal/{malnavn}/raw", "soke_jobber_aktivitet"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);

        Assertions.assertTrue(jsonNode.has("type"));
        Assertions.assertTrue(jsonNode.has("status"));
        Assertions.assertTrue(jsonNode.has("tittel"));
        Assertions.assertTrue(jsonNode.has("beskrivelse"));
        Assertions.assertTrue(jsonNode.has("fraDato"));
        Assertions.assertTrue(jsonNode.has("tilDato"));
        Assertions.assertTrue(jsonNode.has("malid"));

        String unextrapolated = jsonNode.get("fraDato").asText();
        assertThat(unextrapolated).isEqualTo("{now}");
    }

    @Test
    @SneakyThrows
    void should_filter() {

        String requestBody = "{\"type\": \"EGEN\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/mal", "soke_jobber_aktivitet").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].type", Matchers.contains("EGEN")));


    }

}
