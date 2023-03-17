package no.nav.veilarbmalverk;

import org.springframework.boot.SpringApplication;

//@SpringBootApplication
// Spring boot test tillater ikke at det er flere @SpringBootApplication, så denne må utkommenteres ved behov.
public class VeilarbMalverkTestApp {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(VeilarbMalverkTestApp.class);
        application.setAdditionalProfiles("local");
        application.run(args);

    }
}
