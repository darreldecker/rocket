# rocketmiles coding exercise


### Part 1: search functionality tests
Background:<br>
This repository contains a stripped down version of a selenium framework that
I've written in the past. The approach is a little different than standard assertions
and is designed to auto log the test step details so that when a test fails, it should
be easy to review the log and understand what the test was doing and where it failed without digging into
a stack trace. The framework files are located in /src/main/java/framework. At the moment, I've only verified
the tests running on Chrome on a mac and included the latest chromedriver in the /lib directory 
<br><br>
Execute with the following:
```
gradle clean test
```

This will execute the test cases
```
SearchTestSuite::testSearchLocation
SearchTestSuite::testSearchMessages

I didn't have time to do an exhaustive set of search tests, but hopefully the existing
tests give you an idea of how I structured the testcases. There's also a TODO section
listing other tests I would add to the test suite.
```

Gradle will build a simple test report that can be found in the build directory. I also
included a screenshot of one of the test logs so you can see the automated test logging 
```
‎~/rocket/build/reports/tests/test/index.html
```
---
### Part 2 - Test Plan
