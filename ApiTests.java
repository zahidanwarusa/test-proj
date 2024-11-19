package com.demo.tests;

import com.demo.base.TestBase;
import com.demo.model.TestCase;
import com.demo.reporter.TestReporter;
import com.demo.utils.RestUtils;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ApiTests extends TestBase {
    
    @DataProvider(name = "apiTestData")
    public Object[][] getTestData() {
        return testCases.stream()
            .map(tc -> new Object[]{tc})
            .toArray(Object[][]::new);
    }
    
    @Test(dataProvider = "apiTestData")
    public void executeApiTests(TestCase testCase) {
        try {
            // Start reporting
            TestReporter.startTest(testCase.getTestId());
            
            // Execute request
            Response response = RestUtils.executeRequest(testCase);
            
            // Validate status code
            boolean isStatusValid = response.getStatusCode() == testCase.getExpectedStatus();
            
            // Validate response if expected response is provided
            boolean isResponseValid = true;
            if (testCase.getExpectedResponse() != null && !testCase.getExpectedResponse().isEmpty()) {
                isResponseValid = RestUtils.validateJsonResponse(
                    response, 
                    testCase.getExpectedResponse(), 
                    testCase.getExpectedResponse()
                );
            }
            
            // Log results
            if (isStatusValid && isResponseValid) {
                TestReporter.logPass(testCase, response);
            } else {
                TestReporter.logFail(testCase, response);
                if (!isStatusValid) {
                    Assert.fail("Status code mismatch. Expected: " + testCase.getExpectedStatus() 
                        + " but got: " + response.getStatusCode());
                }
            }
            
        } catch (Exception e) {
            TestReporter.logError(testCase, e);
            Assert.fail("Test failed with exception: " + e.getMessage());
        }
    }
    
    @AfterSuite
    public void tearDown() {
        TestReporter.generateReport();
    }
}
