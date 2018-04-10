package no.nav.fo.veilarbmalverk.config;

import no.nav.apiapp.ApiApplication;
import no.nav.apiapp.config.ApiAppConfigurator;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("no.nav.fo.veilarbmalverk.rest")
public class ApplicationConfig implements ApiApplication.NaisApiApplication {

    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {

    }

    @Override
    public String getApplicationName() {
        return "veilarbmalverk";
    }

    @Override
    public Sone getSone() {
        return Sone.FSS;
    }
}
