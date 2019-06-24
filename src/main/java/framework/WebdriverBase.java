package framework;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.HashMap;
import java.util.Map;

public class WebdriverBase extends TestBase {
    protected static WebDriver driver = null;
    protected static String currentSuiteName;
    private static boolean someTestRanInSuite = false;
    private static boolean firstTestInSuite = true;
    protected static boolean runThisTest = false;

    protected WebdriverBase() {
        super();
    }

    protected static void beforeSuite(String suiteName) {
        currentSuiteName = suiteName;
        firstTestInSuite = true;
        someTestRanInSuite = false;
    }

    protected static void afterSuite() {
        log.endSuite();
        quitDriver();
    }

    protected void beforeEachTest(TestCaseInfo testCaseInfo) {
        log.debug("^^^^^  In AppiumDriverBase::beforeAppiumTests() !!!");
        currentTestCaseInfo = testCaseInfo;

        if (firstTestInSuite) {
            log.startSuite(currentSuiteName);
            Functions.sleep(2);
            firstTestInSuite = false;
        }

        if (driver == null || driver.toString().contains("(null)")) {
            driver = createDriver(currentSuiteName);

            TestBase.setDriver(driver);
            driver.get(testParms.testingDomain);
        }

        log.info("Using Driver:  " + driver.toString());

        log.startTest(testCaseInfo);
        acceptCookiePopup();
        closeNewSignupModal();
    }

    protected void afterEachTest() {
        log.endTest();

        Functions.sleep(1); // let the intelliJ log catch up
    }

    private WebDriver createDriver(String sessionName) {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();

        if (testParms.desiredBrowser.contains("firefox")) {
            desiredCapabilities = DesiredCapabilities.firefox();
            driver = new FirefoxDriver(desiredCapabilities);
        }

        if (testParms.desiredBrowser.contains("chrome")) {
            // This is only the osx driver
            System.setProperty("webdriver.chrome.driver", "./lib/chromedriver");
            desiredCapabilities = DesiredCapabilities.chrome();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("allow-running-insecure-content");
            options.addArguments("test-type");
            desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, options);
            driver = new ChromeDriver(desiredCapabilities);
        }

        // simulates a mobile browser via chrome devicename
        if (testParms.desiredBrowser.contains("mobile")) {
            // This is only the osx driver
            System.setProperty("webdriver.chrome.driver", "./lib/chromedriver");
            driver = new ChromeDriver(desiredCapabilities);

            Map<String, String> mobileEmulation = new HashMap<String, String>();
            mobileEmulation.put("deviceName", "iPhone X");

            Map<String, Object> chromeOptions = new HashMap<String, Object>();
            chromeOptions.put("mobileEmulation", mobileEmulation);
            desiredCapabilities = DesiredCapabilities.chrome();
            desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
            driver = new ChromeDriver(desiredCapabilities);
        }

        return driver;
    }

    private static void quitDriver() {
        log.debug("*****  In AppiumDriverBase :: quitAppiumDriver");

        if (driver == null || driver.toString().contains("(null)")) {
            log.debug("Driver already dead:  " + (driver == null ? "null" : driver.toString()));
        } else {
            log.comment("Shutting down the WebDriver (" + driver + ")...");
            driver.quit();
        }

        driver = null;
    }


    // Ideally these methods should be in their own common class
    //      V     V     V     V     V     V     V     V     V
    public void acceptCookiePopup() {
        // Clicks the OK button to accept cookies
        FrameworkControl acceptCookie = new FrameworkControl(By.xpath("//button[contains(@class,'cookie-banner-button')]"), "Accept Cookies");
        if (acceptCookie.exists() && acceptCookie.isDisplayed()) {
            acceptCookie.click();
        }
    }

    public void closeNewSignupModal() {
        // Closes the new signup modal that occurs for first time users
        FrameworkControl btnCloseModal = new FrameworkControl(By.xpath("//div[@id='new-sign-up-modal']//button[@class='close']"), "New Sign In Close");
        if (btnCloseModal.exists(2) && btnCloseModal.isDisplayed()) {
            btnCloseModal.click();
        }
    }

    public String getTooltipContent(){
        // Gets to text of any displayed tooltip or null if no tooltip is displayed
        Functions.sleep(1);
        FrameworkControl stcTooltipText = new FrameworkControl(By.xpath("//div[contains(@class,'popover-content')]"));
        if (stcTooltipText.exists() && stcTooltipText.isDisplayed()) {
            return  stcTooltipText.getText();
        }
        return null;
    }

}
