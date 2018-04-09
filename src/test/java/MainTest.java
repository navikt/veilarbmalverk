import no.nav.testconfig.ApiAppTest;

import static java.lang.System.setProperty;

public class MainTest {

    private static final String TEST_PORT = "8895";

    public static void main(String[] args) {
        setProperty("SERVICE_CALLS_HOME", "target/log");

        ApiAppTest.setupTestContext();
        Main.main(TEST_PORT);
    }
}
