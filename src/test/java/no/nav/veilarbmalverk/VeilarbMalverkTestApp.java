package no.nav.veilarbmalverk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VeilarbMalverkTestApp {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(VeilarbMalverkTestApp.class);
        application.setAdditionalProfiles("local");
        application.run(args);

    }
}
