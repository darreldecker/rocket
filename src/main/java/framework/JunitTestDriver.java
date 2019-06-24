package framework;

import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class JunitTestDriver extends WebdriverBase {
    private TestInfo testInfo;
    private String currentTestName;

    protected JunitTestDriver() {
        super();

        log.debug("{}{}{}{}{}{}{}   In JunitTestDriver() constructor!   {}{}{}{}{}{}{}");
    }


    @ClassRule // the magic is done here
    public static TestRule suiteWatcher = new TestWatcher() {
        @Override
        protected void starting(Description desc) {
            String currentSuiteName = desc.getDisplayName().substring(desc.getDisplayName().lastIndexOf(".") + 1);
            int numTests = desc.testCount();
            log.debug("&&&  Suite starting with number of tests at: " + numTests);  // desc.getChildren() - Gets test cases

            beforeSuite(currentSuiteName);
        }

        @Override
        protected void finished(Description desc) {
            log.debug("&&&&  Suite completed!");
            afterSuite();
        }
    };

    @Rule
    public TestRule testWatcher = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            log.debug("!!!!!  In JunitTestDriver::TestWatcher::starting: " + description);

            testInfo = description.getAnnotation(TestInfo.class);
            currentTestName = description.getMethodName();
        }

        @Override
        protected void finished(Description description) {
            log.debug("!!!!!  In JunitTestDriver::TestWatcher::finished: " + description);
            afterEachTest();
        }

        @Override
        protected void failed(Throwable e, Description description) {
            log.debug("!!!!!  In JunitTestDriver::TestWatcher::'failed': " + description);

            if (!log.isTestException()) {
                //  It's not an exception raised by us!
                log.exception(e, true);
            }
        }

    };

    /**
     * Determines if we should run this test based on the input properties you
     * specify at runtime.  The properties are:
     * - qa.tests.to.run: Specify the name(s) of one or more tests you want to run
     * - qa.suites.to.run: Specify the name(s) of one or more suite files you want to run
     */
    @Before
    public void shouldWeRunThisTest() {
        log.debug("$$$$$  In JunitTestDriver::shouldWeRunThisTest @Before!");
        Assume.assumeTrue(runThisTest());

        // Once we get here, we've determined that we should run this test!
        beforeEachTest(new TestCaseInfo(currentTestName, testInfo));
    }


    /**
     * Contains the logic for choosing whether or not to run the current test based on
     * what has been specified in the properties file.
     *
     * @return boolean Should the test be run?
     */
    private boolean runThisTest() {
        if (testInfo != null) {
            //*********************************
            //          Test Level
            //*********************************
            if (testParms.testLevel.equals("smoke") && !testInfo.level().equals("smoke")) {
                log.skip("Skipping " + currentTestName + "... only running smoke tests!");
                runThisTest = false;
            } else {
                //*********************************
                //          Test Suites
                //*********************************
                if (testParms.specificSuitesToRun != null && !testParms.specificSuitesToRun.equals("") && !testParms.specificSuitesToRun.contains(currentSuiteName)) {
                    log.skip("Skipping " + currentTestName + " because it's not in the specific suites: " + testParms.specificSuitesToRun);
                    runThisTest = false;
                }
                //*********************************
                //          Test Name
                //*********************************
                if (!testParms.specificTestsToRun.equals("All") && !testParms.specificTestsToRun.contains(currentTestName)) {
                    log.skip("Skipping " + currentTestName + " because it's not in the specific runs list!");
                    runThisTest = false;
                }
            }
        }

        return true;
    }

}
