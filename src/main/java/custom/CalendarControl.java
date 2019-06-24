package custom;

import framework.FrameworkControl;
import framework.WebdriverBase;
import org.openqa.selenium.By;

public class CalendarControl extends WebdriverBase {

    private FrameworkControl calendar = new FrameworkControl(By.xpath("//div[contains(@id,'ui-datepicker-div')]"));
    private FrameworkControl stcMonthYear = new FrameworkControl(By.xpath("//div[contains(@id,'ui-datepicker-title')]"));
    private FrameworkControl btnPrevMonth = new FrameworkControl(By.xpath("//span[contains(.,'Prev')]"));
    private FrameworkControl btnNextMonth = new FrameworkControl(By.xpath("//span[contains(.,'Next')]"));

    public boolean exists() {
        return (calendar.exists(1) && calendar.isDisplayed());
    }

    public void clickNextMonth() {
        btnNextMonth.click();
    }

    public void clickPrevMonth() {
        btnPrevMonth.click();
    }

    public void clickDay(Integer day) {
        FrameworkControl calDay = new FrameworkControl(By.xpath("//a[contains(.,'" + day + "')]"));
        calDay.click();
    }

}
