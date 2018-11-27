import no.nav.testconfig.ApiAppTest;

import static java.lang.System.setProperty;
import static no.nav.fo.veilarbmalverk.config.ApplicationConfig.APPLICATION_NAME;
import static no.nav.testconfig.ApiAppTest.Config.builder;

public class MainTest {

    private static final String TEST_PORT = "8891";

    public static void main(String[] args) {
        setProperty("SERVICE_CALLS_HOME", "target/log");
        ApiAppTest.setupTestContext(builder().applicationName(APPLICATION_NAME).build());

        Main.main(TEST_PORT);
    }
}
