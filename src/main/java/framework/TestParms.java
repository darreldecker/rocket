package framework;

import java.io.InputStream;
import java.util.Properties;

public class TestParms {
    private static final Properties ENVIRONMENT_PROPERTIES = getPropertiesFile();
    private static TestParms me = new TestParms();

    public String testingDomain = loadProperty("qa.test.domain");
    public String testLevel = loadProperty("qa.test.level");
    public String logLevel = loadProperty("qa.test.log.level");
    public String specificTestsToRun = loadProperty("qa.tests.to.run");
    public String specificSuitesToRun = loadProperty("qa.suites.to.run");
    public String desiredBrowser = loadProperty("qa.test.browser");


    public static TestParms getTestParms() {
        return me;
    }

    private static Properties getPropertiesFile() {
        String env = System.getProperty("qa.env.target", "default");
        Properties properties = new Properties();
        if (env != null) {
            String envFile = "/" + env + ".properties";

            try {
                InputStream envStream = TestParms.class.getResourceAsStream(envFile);
                properties.load(envStream);
                envStream.close();
            } catch (Exception e) {
                return properties;
            }
        }

        return properties;
    }

    private static String loadProperty(final String inProperty) {
        String outValue = System.getProperty(inProperty, ENVIRONMENT_PROPERTIES.getProperty(inProperty, null));
        return outValue;
    }

    private static Boolean loadPropertyAsBoolean(final String inProperty) {
        String outValue = System.getProperty(inProperty, ENVIRONMENT_PROPERTIES.getProperty(inProperty, "false"));
        return outValue.toLowerCase().equals("true");
    }

}
