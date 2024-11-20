package com.demo.base;

import com.demo.constants.ApiConstants;
import com.demo.model.TestCase;
import com.demo.utils.ExcelReader;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import java.util.List;

public class TestBase {
    protected List<TestCase> testCases;
    protected ExcelReader excelReader;
    
    @BeforeSuite(alwaysRun = true)
    public void setup() {
        try {
            System.out.println("Setting up test base...");
            RestAssured.baseURI = ApiConstants.BASE_URL;
            
            // Initialize ExcelReader
            excelReader = new ExcelReader();
            
            // Print the file path being used
            String excelPath = "src/test/resources/TestCases.xlsx";
            System.out.println("Reading Excel file from: " + excelPath);
            
            // Read test cases
            testCases = excelReader.readTestCases(excelPath);
            
            // Verify test cases were read
            if (testCases == null || testCases.isEmpty()) {
                throw new RuntimeException("No test cases were read from Excel file");
            }
            
            System.out.println("Successfully read " + testCases.size() + " test cases");
            
        } catch (Exception e) {
            System.err.println("Error in setup: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize test setup", e);
        }
    }
    
    @DataProvider(name = "apiTestData")
    public Object[][] getTestData() {
        if (testCases == null) {
            throw new RuntimeException("Test cases not initialized. Check if @BeforeSuite method was executed.");
        }
        return testCases.stream()
                .map(tc -> new Object[]{tc})
                .toArray(Object[][]::new);
    }
}





@Test(priority = -1) // Run this first
public void verifyExcelReading() {
    Assert.assertNotNull(testCases, "Test cases should not be null");
    Assert.assertFalse(testCases.isEmpty(), "Test cases should not be empty");
    testCases.forEach(tc -> {
        System.out.println("Test Case ID: " + tc.getTestId());
        System.out.println("Endpoint: " + tc.getEndpoint());
        System.out.println("Method: " + tc.getMethod());
        System.out.println("-------------------");
    });
}
