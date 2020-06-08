package no.nav.veilarbmalverk.controller;

import no.nav.common.health.selftest.SelftestHtmlGenerator;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/internal")
public class InternalController {

    @GetMapping("/isReady")
    public void isReady() {}

    @GetMapping("/isAlive")
    public void isAlive() {}

    @GetMapping("/selftest")
    public ResponseEntity selftest() {
        String html = SelftestHtmlGenerator.generate(Collections.emptyList());
        int status = 200;

        return ResponseEntity
                .status(status)
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

}
