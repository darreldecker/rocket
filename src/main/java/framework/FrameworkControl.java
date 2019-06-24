package framework;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FrameworkControl extends TestBase {
    By locator = null;
    private String controlName = null;

    private WebElement elementCache = null;
    protected Boolean useCache = true;

    public FrameworkControl() {
    }

    public FrameworkControl(By locator) {
        this();
        this.locator = locator;
    }

    public FrameworkControl(By locator, String controlName) {
        this();
        this.locator = locator;
        this.controlName = controlName;
    }

    public By getLocator() {
        return this.locator;
    }

    protected WebElement findElement() {
        return findElement(wait.get(), true);
    }

    protected WebElement findElement(int waitSecs) {

        return findElement((new WebDriverWait(driver, waitSecs)), false);
    }

    private WebElement findElement(WebDriverWait wait, boolean failWhenNotFound) {
        WebElement foundElement = null;

        if (useCache && elementCache != null) {
            log.debug("%%%%%  Using element cache: " + elementCache);
            return elementCache;
        }

        try {
            foundElement = (WebElement) wait.until(ExpectedConditions.presenceOfElementLocated(this.getLocator()));
            Actions actions = new Actions(driver);
            actions.moveToElement(foundElement);
            actions.perform();
        } catch (Exception e) {
            if (failWhenNotFound) {
                log.exception(e, true);
                log.fatal("AutoControl::findElement()  --->  Stopping the test");
            }
        }

        if (useCache && foundElement != null) {
            elementCache = foundElement;
        }

        return foundElement;
    }

    private List<WebElement> findElements() {
        return this.findElements(this.getLocator());
    }

    protected List<WebElement> findElements(By locator) {
        return driver.findElements(locator);
    }

    private String formatControlName() {
        if (this.controlName == null) {
            String byType = this.getLocator().toString().split(":")[0];
            String byValue = (this.getLocator().toString().split(":")[1]).trim();

            if ((byType.toLowerCase().contains("xpath")) && (byValue.contains("'"))) {
                String[] split = byValue.split("'");
                return split[split.length - 2];
            } else
                return byValue;
        } else {
            return this.controlName;
        }
    }

    public void hoverOverMe() {
        log.info("   [MoveTo] " + this.formatControlName());
        Actions action = new Actions(driver);
        WebElement element = findElement();
        action.moveToElement(element).perform();
    }

    public void clickDisplayed() {
        List<WebElement> elements = findElements();
        for (int x = 0; x < elements.size(); x++) {
            if (elements.get(x).isDisplayed()) {
                log.info("    [Click] " + this.formatControlName());
                elements.get(x).click();
                break;
            }
        }
    }

    public void click() {
        log.info("    [Click] " + this.formatControlName());

        for (int i = 1; i <= 2; i++) {
            WebElement element = findElement();

            try {
                element.click();
                break;
            } catch (StaleElementReferenceException stale) {
                log.info("    !! StaleElementReferenceException for click()... Clearing cache and trying again !!");
                resetElementCache();
                Functions.sleep(1);
            } catch (ElementClickInterceptedException e) {
                for (int x=0; x<10; x++){
                    Functions.sleep(1);
                    if(element.isEnabled()){
                        break;
                    }
                }
            } catch (WebDriverException e) {
                log.exception(e, true);
            }
        }
    }

    public String clickAndSwitchTabs() {
        String handleToCurrentWindow = driver.getWindowHandle();
        this.click();

        ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
        if (tabs.size() != 1) {
            driver.switchTo().window(tabs.get(1));
        } else {
            log.warning("New tab not found after clicking");
        }
        return handleToCurrentWindow;
    }


    public void setText(String text) {
        setText(text, false);
    }

    public void setText(String text, Boolean doClear) {
        int i;
        log.info("    [SetText] " + this.formatControlName() + " ---> '" + text.replace("\n", "<Enter>") + "'");

        WebElement element = findElement();
        try {
            element.click();
        } catch (Exception e) {
            Functions.sleep(1);
            element.click();
        }
        element.sendKeys(text);
    }

    public void clear() {
        log.info("    [Clear] " + this.formatControlName());

        for (int i = 1; i <= 3; i++) {
            WebElement element = findElement();
            try {
                element.clear();
                break;
            } catch (Exception e) {
                String message = e.getMessage();
                Functions.sleep(2);
            }
        }
    }

    public void sendKeys(CharSequence... keysToSend) {
        String message = "[SendKeys]";
        try {
            log.info("    " + message + " " + this.formatControlName() + " ---> '" + ((Keys) keysToSend[0]).name() + "'");
        } catch (Exception e) {
            log.info("    " + message + " " + this.formatControlName() + " ---> '" + (keysToSend[0]).toString().replace("\n", "<Enter>") + "'");
        }

        for (int i = 1; i <= 3; i++) {
            WebElement element = findElement();
            try {
                element.sendKeys(keysToSend);
                break;
            } catch (Exception e) {
                log.debug("  !! Element not found or stale for sendKeys()... trying again !!");
                resetElementCache();
            }
        }
    }

    public String waitForText(String textToWaitFor) {
        return waitForText(textToWaitFor, 10);
    }

    public String waitForText(String textToWaitFor, int maxWaitTime) {
        int count = 0;
        String returnText = "";
        while (count <= maxWaitTime) {
            returnText = this.getText();
            if (returnText.contains(textToWaitFor)) {
                break;
            } else {
                log.debug("Did not find text '" + textToWaitFor + "' .. trying again");
                Functions.sleep(1);
                count++;
            }
        }
        return returnText;
    }

    public String getValue() {
        return this.getAttribute("value");
    }

    public String getText() {
        return this.getText(false);
    }

    public String getText(boolean getLastControl) {
        String returnText = null;
        WebElement element;
        int i;

        for (i = 1; i <= 3; i++) {
            try {
                if (getLastControl) {
                    List<WebElement> elements = findElements();

                    element = elements.get(elements.size() - 1);
                } else
                    element = findElement();

                returnText = element.getText();
                break;
            } catch (Exception e) {
                if (i == 3)
                    throw e;
                else
                    log.info("[" + e.getClass().getSimpleName() + "] Exception trapped in getText().  Trying again...");

                Functions.sleep(1);   // Give the page a second to re-try
            }
        }

        return returnText;
    }

    public boolean isEnabled() {
        boolean isEnabled = false;

        try {
            isEnabled = findElement().isEnabled();
        } catch (Exception e) {
            // Do nothing!
        }

        return isEnabled;
    }

    public boolean isSelected() {
        return findElement().isSelected();
    }

    public boolean isDisplayed() {
        try {
            resetElementCache();
            return findElement(1).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean exists() {
        return this.exists(0);
    }

    public boolean exists(int seconds) {
        this.resetElementCache();
        return (findElement((seconds == 0) ? 1 : seconds) != null);
    }

    public boolean waitForMe() {
        return this.exists(120);
    }

    public boolean waitForMeToBeClickable() {
        if(this.exists() && this.isEnabled()){
            return true;
        }
        return this.exists(120);
    }

    public String getAllAttributes(WebElement webElement) {
        List<String> attribs = Arrays.asList("name", "label", "value", "enabled", "text", "tag", "class", "visible", "width", "height", "valid");
        String debug = "";

        for (String attrib : attribs) {
            try {
                debug += attrib + ": " + webElement.getAttribute(attrib) + " | ";
            } catch (Exception e) {
                debug += attrib + ": <invalid> | ";
            }
        }

        return debug;
    }

    public String getAllAttributes() {
        return this.getAllAttributes(this.findElement());
    }


    public WebElement getMyElement() {
        return this.findElement();
    }

    public List<WebElement> getMyElements() {
        return this.findElements();
    }

    public List<WebElement> getChildElements(By childLocator) {
        return this.findElement().findElements(childLocator);
    }

    public String getAttribute(String attributeName) {
        return this.findElement().getAttribute(attributeName);
    }

    public void setAttribute(String attribute, String value) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].setAttribute('" + attribute + "', '" + value + "')", this.findElement());
    }

    public String getControlName() {
        return this.formatControlName();
    }

    public void setControlName(String controlName) {
        this.controlName = controlName;
    }

    public String toString() {
        return this.formatControlName();
    }

    public void resetElementCache() {
        this.elementCache = null;
    }

    public void disableCache() {
        resetElementCache();
        this.useCache = false;
    }


}
