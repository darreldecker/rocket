package models;

public class SearchResultModel {
    private String hotelName;
    private String neighborhood;
    private String avgRating;
    private String reviewLabel;
    private String reviewDetail;
    private String price;
    private String rewards;

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(String avgRating) {
        this.avgRating = avgRating;
    }

    public String getReviewLabel() {
        return reviewLabel;
    }

    public void setReviewLabel(String reviewLabel) {
        this.reviewLabel = reviewLabel;
    }

    public String getReviewDetail() {
        return reviewDetail;
    }

    public void setReviewDetail(String reviewDetail) {
        this.reviewDetail = reviewDetail;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRewards() {
        return rewards;
    }

    public void setRewards(String rewards) {
        this.rewards = rewards;
    }

    public static SearchResultModel getExampleModel() {
        // Using this hard coded data as a sample search expected result.
        // Ideally we would populate the model using an api call or something more dynamic
        SearchResultModel data = new SearchResultModel();
        data.setHotelName("Holiday Inn Rockford");
        data.setNeighborhood("Rockford");
        data.setAvgRating("8");
        data.setReviewLabel("Very Good");
        data.setReviewDetail("97 verified guest reviews");
        data.setPrice("$115");
        data.setRewards("32,000 Points");
        return data;
    }

    public String toString() {
        return getHotelName() + "|" +
                getNeighborhood() + "|" +
                getAvgRating() + "|" +
                getReviewLabel() + "|" +
                getReviewDetail() + "|" +
                getPrice() + "|" +
                getRewards();
    }
}
