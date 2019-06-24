package pages;

import custom.CalendarControl;
import framework.FrameworkControl;
import framework.Functions;
import framework.TestParms;
import framework.WebdriverBase;
import org.openqa.selenium.By;

import java.util.Date;

public class RocketHomePage extends WebdriverBase {


    private FrameworkControl txtSearchLocation = new FrameworkControl(By.xpath("//input[@placeholder='Where do you need a hotel?']"), "Location");
    private FrameworkControl txtSearchRewardProgram = new FrameworkControl(By.xpath("//input[@placeholder='Select reward program']"), "Reward Program");
    private FrameworkControl txtSearchCheckIn = new FrameworkControl(By.xpath("//input[@placeholder='Check in']"), "Check In Date");
    private FrameworkControl txtSearchCheckOut = new FrameworkControl(By.xpath("//input[@placeholder='Check out']"), "Check Out Date");
    private FrameworkControl selSearchGuests = new FrameworkControl(By.xpath("//span[contains(.,' Guest')]"), "Guests");
    private FrameworkControl selSearchRooms = new FrameworkControl(By.xpath("//span[contains(.,' Room')]"), "Rooms");
    private FrameworkControl btnSearch = new FrameworkControl(By.xpath("//button[contains(@class,'search-submit-btn')]"), "Search");


    public boolean exists() {
        return driver.getCurrentUrl().contains("rocketmiles.com");
    }

    public void gotoHomePage(){
        driver.get(TestParms.getTestParms().testingDomain);
    }
    public void setSearchLocation(String location) {
        txtSearchLocation.click();
        txtSearchLocation.sendKeys(location.substring(0, 6));
        Functions.sleep(1.5);
        FrameworkControl txtSelect = new FrameworkControl(By.xpath("//p[contains(.,'" + location + "')]"), location);
        txtSelect.click();
    }

    public void selectSearchRewardProgram(String program) {
        txtSearchRewardProgram.click();
        txtSearchRewardProgram.clear();
        txtSearchRewardProgram.sendKeys(program.substring(0, 4));
        Functions.sleep(0.25);
        FrameworkControl txtSelect = new FrameworkControl(By.xpath("//a[contains(.,'" + program + "')]"), program);
        txtSelect.click();
    }

    public void setSearchRewardProgram(String program) {
        txtSearchRewardProgram.click();
        txtSearchRewardProgram.clear();
        txtSearchRewardProgram.sendKeys(program.substring(0, 4));
    }

    public void selectSearchGuests(Integer numGuests) {
        selSearchGuests.click();
        FrameworkControl selection = new FrameworkControl(By.xpath("//a[contains(.,'" + numGuests + " Guest')]"));
        selection.click();
    }

    public void selectSearchRooms(Integer numRooms) {
        selSearchRooms.click();
        FrameworkControl selection = new FrameworkControl(By.xpath("//a[contains(.,'" + numRooms + " Room')]"));
        selection.click();
    }

    public void selectCheckinDay(String checkinDate) {
        txtSearchCheckIn.click();
        CalendarControl calendar = new CalendarControl();
        if (calendar.exists()) {
            calendar.clickNextMonth();
            // I didn't want to mess with date parsing right now so its currently
            // just selecting the 10th day of the next month
            calendar.clickDay(10);
        }
    }

    public void selectCheckoutDay(String checkoutDate) {
        txtSearchCheckOut.click();
        CalendarControl calendar = new CalendarControl();
        if (calendar.exists()) {
            // I didn't want to mess with date parsing right now so its currently
            // just selecting the 15th day of the month of the checkin month
            calendar.clickDay(15);
        }
    }

    public void clickSearchButton() {
        btnSearch.waitForMe();
        btnSearch.click();
    }

    public void search(String location, String rewardProgram, String checkinDate, String checkoutDate, Integer numGuests, Integer numRooms) {
        if (location != null) {
            setSearchLocation(location);
        }

        if (rewardProgram != null) {
            selectSearchRewardProgram(rewardProgram);
        }

        if (checkinDate != null) {
            selectCheckinDay(checkinDate);
        }

        if (checkoutDate != null) {
            selectCheckoutDay(checkoutDate);
        }

        if (numGuests != null) {
            selectSearchGuests(numGuests);
        }

        if (numRooms != null) {
            selectSearchRooms(numRooms);
        }

        btnSearch.click();
    }
}
