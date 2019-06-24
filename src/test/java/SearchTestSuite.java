import framework.JunitTestDriver;
import framework.TestInfo;
import models.SearchResultModel;
import org.junit.Test;
import pages.RocketHomePage;
import pages.SearchResultsPage;

import java.sql.Date;

public class SearchTestSuite extends JunitTestDriver {

    public RocketHomePage homePage = new RocketHomePage();
    public SearchResultsPage resultsPage = new SearchResultsPage();

    @Test
    @TestInfo(description = "Search for a hotel using a date range",
            categories = "test",
            level = "smoke")
    public void testSearchLocation() {
        // hardcoding test data to keep things simple, but ideally we would loop this test with dynamic data from
        // a file, database, api, etc
        log.step("Enter the search criteria");
        log.result("Are we on the home page?", true, homePage.exists());

        homePage.search("Rockford, IL, USA", "Buzz Points", "07-10-2019", "07-15-2019", 1, 2);
        resultsPage.waitForSearchResults();

        log.step("Verify the search results");
        log.result("Did the search return any results?", true, resultsPage.exists());
        SearchResultModel expectedResults = SearchResultModel.getExampleModel();
        SearchResultModel actualResults = resultsPage.findHotelInSearchResults(expectedResults.getHotelName());
        if (log.result("'" + expectedResults.getHotelName() + "' found in search results?", true, (actualResults != null))) {
            log.result("Expected search details found?", expectedResults.toString(), actualResults.toString());
        }
    }

    @Test
    @TestInfo(description = "Verify the tooltip messages for invalid data",
            categories = "test",
            level = "smoke")
    public void testSearchMessages() {
        homePage.gotoHomePage();
        log.step("Click search button with no location entered");
        homePage.clickSearchButton();
        String message = getTooltipContent();
        log.resultContains("Message for empty location","Unknown location", message);

        log.step("Enter location with no offers");
        homePage.setSearchLocation("Rachel, NV, USA");
        message = getTooltipContent();
        log.resultContains("Message for invalid location","No offers available", message);

        log.step("Enter valid location with no selected rewards program");
        homePage.setSearchLocation("Chicago, IL, USA");
        homePage.clickSearchButton();
        message = getTooltipContent();
        log.resultContains("Message for empty reward program","Reward program is required", message);

        log.step("Enter invalid rewards program");
        homePage.setSearchRewardProgram("xxxx");
        message = getTooltipContent();
        log.resultContains("Message for invalid reward program","Please choose a valid reward program", message);
    }

    //TODO: Additional Tests for the homepage
    //      - Verify various calendar entry combinations
    //          - Can't select dates in the past
    //          - Can't select checkout date earlier than checkin date
    //      - Verify a search using current location returns the expected results
    //      - Verify any dependencies with selected location or reward program. I didn't see any, but I didn't
    //        try every combination. Is it important to test every combination? If so then I would loop the test
    //        driving it with various combinations of search criteria stored in a file
    //      - Verify selections resulting in "We were unable to find offers for your search" message. I saw this when
    //        using a really large range of dates
    //      - Verify search result changes to price and points based on number of rooms and guests
    //      - Verify search results with currency differences
    //      - Verify page in different languages. I assume there would be some kind of strings file that can be used
    //        for the expected text in other languages
    //      - Verify the search works when displayed in a mobile browser. I noticed that the rewards program control
    //        changes when on mobile so the locator logic would need to be modified if the test was on a mobile device.
    //        I wouldn't make a separate test just for mobile, but would adjust the framework to adapt to the control
    //        changes so all tests could be run on desktop or mobile browsers with a simple switch
}
