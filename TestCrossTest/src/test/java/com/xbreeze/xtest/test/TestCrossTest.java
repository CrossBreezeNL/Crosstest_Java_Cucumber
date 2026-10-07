package com.xbreeze.xtest.test;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

/**
 * JUnit Platform Suite class to run Cucumber tests in the features package.
 * @author Harmen
 */
@Suite
@SelectPackages("features")
public class TestCrossTest {
}
