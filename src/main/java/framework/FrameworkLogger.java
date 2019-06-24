package framework;

import com.google.common.collect.Maps;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;

import java.io.*;
import java.math.BigDecimal;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;


public class FrameworkLogger {
    private static final Logger log = Logger.getLogger("");
    private static boolean passed = true;
    private static boolean skipped = false;
    private static boolean exception = false;
    private static boolean suitePassed = true;
    private static String currentSuiteName = null;
    private static Date timeStarted = null;
    private static Date suiteStarted = null;
    private static int testsPassed = 0;
    private static int testsFailed = 0;
    private static int testsSkipped = 0;
    private static PrintStream systemOut = System.out;
    private static int stepNumber = 1;
    private static ExtentReports extent = null;
    private static ExtentTest extentTest = null;
    private static String extentVerifyText = "";
    private static String extentStepDetails = null;
    private boolean logToExtent = true;

    private static FrameworkLogger me = new FrameworkLogger();

    public static FrameworkLogger getLogger() {
        me.setLevel(Level.toLevel(TestBase.testParms.logLevel, Level.INFO));

        return me;
    }

    public boolean isTestPassed() {
        return passed;
    }

    public boolean isTestException() {
        return exception;
    }

    public static boolean isSuitePassed() {
        return testsFailed == 0;
    }

    public static boolean isSuiteSkipped() {
        return testsFailed == 0 && testsPassed == 0 && testsSkipped > 0;
    }

    public void setExtentReportDeviceInfo(String info) {
        extent.addSystemInfo("Device Info", info);
    }

    //**********************************
    //    Methods that mirror Logger
    //**********************************
    public void info(Object message) {
        log.info(message);
    }

    public void debug(Object message) {
        log.debug(message);
    }

    public void trace(Object message) {
        log.trace(message);
    }

    public void error(String message) {
        log.error(message);
        passed = false;

        if (logToExtent)
            extentTestLog(LogStatus.FAIL, message, "");
    }

    private void errorNoExtent(String message) {
        this.logToExtent = false;
        this.error(message);
        this.logToExtent = true;
    }

    public void fatal(String message) {
        passed = false;
        log.fatal(message);
        extentTestLog(LogStatus.FATAL, message, "");
        Assert.fail(message);
    }

    public void comment(String message) {
        log.info("");
        log.info("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        log.info(message);
        log.info("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
    }

    public void warning(String message) {
        log.warn(message);
        extentTestLog(LogStatus.WARNING, message, "");
    }

    public void exception(Throwable except) {
        this.exception(except, false);
    }

    public void exception(Throwable except, boolean screenshot) {
        String[] exceptLines = except.toString().split("\\n");
        String header = exceptLines[0].split("Original error:")[0];
        String stackTrace = getStackTrace(except);

        this.errorNoExtent("! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !");
        this.errorNoExtent("!              EXCEPTION RAISED               !");
        this.errorNoExtent(header);
        this.errorNoExtent("! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !");

        exception = true;
    }

    private String getStackTrace(Throwable e) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        printWriter.flush();

        return writer.toString();
    }

    public void exceptionAsWarning(Throwable except) {
        String[] exceptLines = except.toString().split("\\n");

        this.warning(exceptLines[0]);
    }

    public void exceptionAsInfo(Throwable except) {
        String[] exceptLines = except.toString().split("\\n");

        this.info(exceptLines[0]);
    }

    public void setLevel(Level level) {
        log.setLevel(level);
    }

    // *********************************
    //    New methods to extend logger
    // *********************************
    public void startTest(TestCaseInfo testInfo) {
        log.info("======================================================================================");
        log.info("    Starting test:  " + testInfo.name);
        log.info("    Description:    " + testInfo.description);
        log.info("    Environment:    " + TestBase.testParms.testingDomain);
        log.info("======================================================================================");
        log.info("");

        passed = true;
        skipped = false;
        exception = false;
        timeStarted = new Date();
        stepNumber = 1;
    }

    public void endTest() {
        if (timeStarted != null) {
            Date duration = new Date((new Date()).getTime() - timeStarted.getTime());
            Format formatted = new SimpleDateFormat("mm:ss");
            String passFail = skipped ? "SKIP" : passed ? "PASS" : "FAIL";
            timeStarted = null;
            suitePassed = suitePassed && passed;

            log.info("");
            log.info("======================================================================================");
            log.info("       Ending test status:    (" + passFail + ")        Duration: " + formatted.format(duration));
            log.info("======================================================================================");

            if (!skipped) {
                if (passed)
                    testsPassed++;
                else
                    testsFailed++;
            }

            if (extent != null) {
                extent.endTest(extentTest);
                extent.flush();
            }

            if (!exception) {
                Assert.assertTrue("Test failed with 1 or more ERRORs", passed);
            }

        }
    }

    private void resetSuiteVariables(String suiteName) {
        //  Reset to defaults in case we're running multiple suites back-to-back
        testsPassed = 0;
        testsFailed = 0;
        testsSkipped = 0;
        suitePassed = true;
        suiteStarted = new Date();
        currentSuiteName = suiteName;

    }

    public void startSuite(String suiteName) {
        resetSuiteVariables(suiteName);

        log.info("//////////////////////////////////////////////////////////////////////////////////////");
        log.info("//            Starting suite:  " + currentSuiteName);
        log.info("//////////////////////////////////////////////////////////////////////////////////////");
    }

    public void endSuite() {
        if (!isSuiteSkipped()) {
            Date duration = new Date((new Date()).getTime() - suiteStarted.getTime());
            Format formatted = new SimpleDateFormat("mm:ss");

            log.info("//////////////////////////////////////////////////////////////////////////////////////");
            log.info("//    Ending suite:  " + currentSuiteName);
            log.info("//    ------------");
            log.info("//    Tests Passed:  " + testsPassed);
            log.info("//    Tests Failed:  " + testsFailed);
            log.info("//    Tests Skipped:  " + testsSkipped);
            log.info("//////////////////////////////////////////////////////////////////////////////////////");

        }

        resetSuiteVariables(null);
    }

    public void step(String message) {
        if (!message.toLowerCase().startsWith("step") && !message.toLowerCase().startsWith("setup - ") && !message.toLowerCase().startsWith("cleanup - ")) {
            message = "Step " + stepNumber + " - " + message;
            stepNumber++;
        }

        log.info("");
        log.info("**********************************************************************");
        log.info(message);
        log.info("**********************************************************************");

        extentTestLog(LogStatus.INFO, message, "");
    }

    public void extentMessage(String message) {
        extentTestLog(LogStatus.INFO, message, "");
    }

    public void pass(String message) {
        log.info("    (PASS)  " + message);

        if (message.length() > 50) {
            message = message.substring(0, 47) + "...";
        }
    }

    public void fail(String message) {
        this.errorNoExtent("    (FAIL)  " + message);
    }

    public void skip() {
        skip(null);
    }

    public void skip(String message) {
        if (message != null) {
            log.info("");
            log.info(message);
            log.info("");
        }

        testsSkipped++;
        skipped = true;
    }

    public boolean result(String message, Object expected, Object actual) {
        extentVerifyText = message;
        log.info("Verifying: " + extentVerifyText);

        boolean pass = compareExpectedAndActualValues(expected, actual);

        if (pass) {
            this.pass("Got: " + actual);
        } else {
            if (expected.getClass().toString().contains("Map")) {
                this.fail(formatExtentHashMapCompareFailureMessage(expected, actual));
            } else {
                formatExtentCompareFailureMessage(expected, actual);
                this.fail("Exp: " + expected);
                this.errorNoExtent("            Got: " + actual);
            }

            extentStepDetails = null;
        }

        extentVerifyText = "";
        return pass;
    }

    public boolean result(String message, String[] expected, String[] actual) {
        Boolean pass = true;
        List<String> same = new ArrayList<>();
        List<String> notInExp = new ArrayList<>();
        List<String> notInActual = new ArrayList<>();

        Set<String> expectedSet = new HashSet<>(Arrays.asList(expected));
        for (String actualValue : actual) {
            if (expectedSet.contains(actualValue)) {
            } else {
                notInExp.add(actualValue);
                pass = false;
            }
        }
        Set<String> actualSet = new HashSet<>(Arrays.asList(actual));
        for (String expValue : expected) {
            if (actualSet.contains(expValue)) {
            } else {
                notInActual.add(expValue);
                pass = false;
            }
        }

        extentVerifyText = message;
        log.info("Verifying: " + message);

        if (pass) {
            this.pass("Same: " + expectedSet);
        } else {
            this.fail("Actual not in Exp: " + notInExp);
            this.fail("Exp not in Actual: " + notInActual);
        }

        return pass;
    }

    public boolean resultWithNoLogs(String message, Object expected, Object actual) {
        boolean pass = compareExpectedAndActualValues(expected, actual);

        if (!pass) {
            if (!extentVerifyText.equals(message)) {
                extentVerifyText += " " + message;
            }
            extentStepDetails = "Exp: " + expected + "<BR>Got: " + actual;
            log.info("Verifying: " + message);
            this.fail("Exp: " + expected);
            this.errorNoExtent("            Got: " + actual);
            pass = false;
            if (!extentVerifyText.equals(message)) {
                extentVerifyText = extentVerifyText.replace(message, "").trim();
            }
            extentStepDetails = null;
        }

        return pass;
    }

    public boolean resultNotEquals(String message, Object expected, Object actual) {
        boolean pass = true;

        log.info("Verifying Not Equals: " + message);

        if (expected.equals(actual)) {
            this.fail("Got and Exp: " + actual.toString());
            pass = false;
        } else {
            this.pass("Got: " + actual.toString());
            this.info("            Not: " + expected);
        }

        return pass;
    }

    public boolean resultContains(String message, String expectedToLookFor, String actualToSearch) {
        boolean pass = true;
        extentVerifyText = message;

        log.info("Verifying: " + message);

        if (actualToSearch.contains(expectedToLookFor)) {
            this.info("    Actual : " + actualToSearch);
            this.pass("Contains: " + expectedToLookFor);
        } else {
            this.fail("Actual : " + actualToSearch);
            this.errorNoExtent("            Contain: " + expectedToLookFor);
            pass = false;
        }

        extentVerifyText = "";
        return pass;
    }

    public boolean resultForListContainsValues(String message, List expectedList, List actualList) {
        int i;
        int actualCount = actualList.size();
        List expectedCopy = new ArrayList(expectedList);
        List actualNotInExpected = new ArrayList(actualList);
        actualNotInExpected.clear();
        boolean pass = false;

        log.info("Verifying: " + message);

        for (i = 0; i < actualCount; i++) {
            if (expectedCopy.contains(actualList.get(i))) {
                // if in both expected and actual then remove them from the expected list
                expectedCopy.remove(expectedCopy.indexOf(actualList.get(i)));
            } else {
                actualNotInExpected.add(actualList.get(i));
            }
        }

        if (expectedCopy.size() == 0 && actualNotInExpected.size() == 0) {
            pass = true;
        }
        if (pass) {
            this.pass("Got: " + actualList.toString());
        } else {
            this.info("Actual: " + actualList.toString());
            this.info("Expect: " + expectedList.toString());

            if (actualNotInExpected.size() > 0)
                this.fail("Actual not in Expected: " + actualNotInExpected.toString());
            if (expectedCopy.size() > 0)
                this.fail("Expected not in Actual: " + expectedCopy.toString());
        }

        return pass;

    }

    public boolean resultForListContains(String message, List expectedList, List actualList) {
        return this.doListCompare(message, expectedList, actualList, "contains");
    }

    public boolean resultForListContains(String message, String expectedItem, List actualList) {
        boolean pass = true;

        log.info("Verifying: " + message);

        if (actualList.contains(expectedItem)) {
            this.pass("Contains: " + expectedItem);
        } else {
            this.fail("Actual : " + actualList);
            this.error("            Contain: " + expectedItem);
            pass = false;
        }

        return pass;
    }

    public boolean resultForListEquals(String message, List expectedList, List actualList) {
        return this.doListCompare(message, expectedList, actualList, "equals");
    }

    public boolean resultContainsAnyValue(String message, Object expected) {
        boolean pass = true;

        log.info("Verifying: " + message);

        if ((expected == null) || expected.toString().equals("") || expected.toString().equals("null")) {
            this.fail("Actual : " + expected);
            pass = false;
        } else {
            this.pass("Got: " + expected.toString());
        }

        return pass;
    }

    private boolean doListCompare(String message, List expectedList, List actualList, String compareType) {
        int i;
        int expectedCount = expectedList.size();
        int actualCount = actualList.size();
        boolean pass = (expectedCount == actualCount);
        boolean result = false;

        log.info("Verifying: " + message);

        for (i = 0; i < Math.max(expectedCount, actualCount); i++) {
            Object expected = (i >= expectedCount) ? "<null>" : expectedList.get(i);
            Object actual = (i >= actualCount) ? "<null>" : actualList.get(i);

            switch (compareType) {
                case "equals":
                    result = expected.equals(actual);
                    break;
                case "contains":
                    result = actual.toString().contains(expected.toString());
                    break;
            }

            if (result) {
                this.pass("Got: " + actual.toString());
            } else {
                this.fail("Exp: " + expected.toString());
                this.error("            Got: " + actual.toString());
                pass = false;
            }
        }

        return pass;

    }


    private boolean compareExpectedAndActualValues(Object expected, Object actual) {
        boolean pass = false;

        if (expected == null && actual == null) {
            pass = true;
        } else if (expected instanceof BigDecimal) {
            BigDecimal biggExp = (BigDecimal) expected;

            if ((expected != null) && (actual != null) && (biggExp.compareTo((BigDecimal) actual) == 0)) {
                pass = true;
            }
        } else {
            if ((expected != null) && (actual != null) && (expected.equals(actual))) {
                pass = true;
            }
        }
        return pass;
    }

    private void formatExtentCompareFailureMessage(Object expected, Object actual) {
        String[] expectedParts = expected.toString().split("(?<=\\G.{50})");
        String[] actualParts = actual.toString().split("(?<=\\G.{50})");

        extentStepDetails = "Exp: ";

        for (int i = 0; i < expectedParts.length; i++) {
            if (i > 0) {
                extentStepDetails += "<BR>&emsp;";
            }
            extentStepDetails += expectedParts[i];
        }
        extentStepDetails += "<BR>Got: ";
        for (int i = 0; i < actualParts.length; i++) {
            if (i > 0) {
                extentStepDetails += "<BR>&emsp;";
            }
            extentStepDetails += actualParts[i];
        }

    }

    private String formatExtentHashMapCompareFailureMessage(Object expected, Object actual) {
        // not equal: only on left={3=three}: only on right={4=four}: value differences={2=(whatever2, whateverTwo)}
        String mapCompareOutput = Maps.difference((HashMap) expected, (HashMap) actual).toString();

        mapCompareOutput = mapCompareOutput.replace("not equal: ", "").
                replace("on left", "in expected").
                replace("on right", "in actual").
                replace("value differences", "values differ").
                replace("only", "Only");

        String[] msgParts = mapCompareOutput.split("(?<=\\G.{50})");

        extentStepDetails = "";

        for (int i = 0; i < msgParts.length; i++) {
            if (i > 0) {
                extentStepDetails += "<BR>&emsp;";
            }
            extentStepDetails += msgParts[i];
        }

        return mapCompareOutput.replace("}:", "}\n                                         ");
    }

    private void extentTestLog(LogStatus logStatus, String stepName, String details) {
        if (extentTest != null)
            extentTest.log(logStatus, stepName, details);
    }


    Process p;
    ProcessBuilder builder;

    public String runCommand(String command) throws InterruptedException, IOException {
        p = Runtime.getRuntime().exec(command);
        // get std output
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = "";
        String allLine = "";
        int i = 1;
        while ((line = r.readLine()) != null) {
            allLine = allLine + "" + line + "\n";
            if (line.contains("Console LogLevel: debug") && line.contains("Complete")) {
                break;
            }
            i++;
        }
        return allLine;

    }

}
