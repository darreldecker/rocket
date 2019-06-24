package framework;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestBase {
    protected static final TestParms testParms = TestParms.getTestParms();
    protected static final FrameworkLogger log = FrameworkLogger.getLogger();

    protected static ThreadLocal<WebDriverWait> wait = new ThreadLocal<WebDriverWait>();
    protected static TestCaseInfo currentTestCaseInfo = null;

    protected static WebDriver driver = null;

    static public void setDriver(WebDriver driver) {
        TestBase.driver = driver;
        TestBase.wait.set(new WebDriverWait(driver, 30));
    }

}
