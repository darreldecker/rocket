package pages;

import framework.FrameworkControl;
import framework.Functions;
import framework.WebdriverBase;
import models.SearchResultModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class SearchResultsPage extends WebdriverBase {

    private FrameworkControl searchResults = new FrameworkControl(By.xpath("(//div[contains(@class,'search-result-container')])"));

    private List<WebElement> resultElements = null;

    public boolean exists() {
        return searchResults.exists();
    }

    public Integer waitForSearchResults() {
        for (int x = 0; x <= 60; x++) {
            List<WebElement> results = searchResults.getMyElements();
            if (results.size() > 0) {
                resultElements = results;
                return results.size();
            } else {
                log.info("Still searching ...");
                Functions.sleep(1);
            }
        }
        // We didn't find any search results after 60 seconds
        log.error("No results found after 60 seconds");
        return 0;
    }

    public SearchResultModel findHotelInSearchResults(String hotelToFind) {
        SearchResultModel data = new SearchResultModel();
        for (int x = 0; x < resultElements.size(); x++) {
            String resultName = resultElements.get(x).findElement(By.xpath(".//div[contains(@class,'name-container')]")).getText();
            String resultNeighborhood = resultElements.get(x).findElement(By.xpath(".//div[contains(@class,'neighborhood-container')]")).getText();
            String resultAvgRating = resultElements.get(x).findElement(By.xpath(".//div[contains(@class,'average-rating-container')]")).getText();
            String resultReviewLabel = resultElements.get(x).findElement(By.xpath(".//div[contains(@class,'review-label')]")).getText();
            String resultReviewDetail = resultElements.get(x).findElement(By.xpath(".//div[contains(@class,'review-detail')]")).getText();
            String resultPrice = resultElements.get(x).findElement(By.xpath(".//div[contains(@class,'price-details')]")).getText()
                    .replace("\nper night", "").replace("/room", "");
            String resultReward = resultElements.get(x).findElement(By.xpath(".//div[contains(@class,'rewards')]")).getText().replace("Earn\n", "");

            if (resultName.contains(hotelToFind)) {
                data.setHotelName(resultName);
                data.setNeighborhood(resultNeighborhood);
                data.setAvgRating(resultAvgRating);
                data.setReviewLabel(resultReviewLabel);
                data.setReviewDetail(resultReviewDetail);
                data.setPrice(resultPrice);
                data.setRewards(resultReward);
                return data;
            }
        }
        return null;
    }
}
