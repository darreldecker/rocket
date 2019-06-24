package framework;

public class TestCaseInfo {
    public String name;
    public String description = "";
    public String level = "regression";
    public String catgories = "";

    public TestCaseInfo(String name, String description, String catgories) {
        this.name = name;
        this.description = description;
        this.catgories = catgories;
    }

    public TestCaseInfo(String name, TestInfo testInfo) {
        this.name = name;

        if (testInfo != null) {
            this.description = testInfo.description();
            this.level = testInfo.level();
            this.catgories = testInfo.categories();
        }
    }

}
