// pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.demo</groupId>
    <artifactId>mini-api-framework</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <rest-assured.version>5.3.0</rest-assured.version>
        <testng.version>7.7.1</testng.version>
        <poi.version>5.2.3</poi.version>
        <lombok.version>1.18.26</lombok.version>
        <log4j.version>2.20.0</log4j.version>
        <extentreports.version>5.0.9</extentreports.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>io.rest-assured</groupId>
            <artifactId>rest-assured</artifactId>
            <version>${rest-assured.version}</version>
        </dependency>
        <dependency>
            <groupId>org.testng</groupId>
            <artifactId>testng</artifactId>
            <version>${testng.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi-ooxml</artifactId>
            <version>${poi.version}</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-core</artifactId>
            <version>${log4j.version}</version>
        </dependency>
        <dependency>
            <groupId>com.aventstack</groupId>
            <artifactId>extentreports</artifactId>
            <version>${extentreports.version}</version>
        </dependency>
    </dependencies>
</project>

// ApiConstants.java
package com.demo.constants;

public class ApiConstants {
    public static final String BASE_URL = "https://jsonplaceholder.typicode.com";
    public static final String TEST_DATA_PATH = "src/test/resources/TestCases.xlsx";
    public static final String REPORT_PATH = "test-output/ExtentReport.html";
}

// Config.java
package com.demo.config;

import java.io.FileInputStream;
import java.util.Properties;

public class Config {
    private static Properties properties;
    
    static {
        try {
            properties = new Properties();
            FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
            properties.load(fis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}

// TestCase.java
package com.demo.model;

import lombok.Data;

@Data
public class TestCase {
    private String testId;
    private String endpoint;
    private String method;
    private String requestBody;
    private String headers;
    private int expectedStatus;
    private String expectedResponse;
    private boolean enabled;
}

// ExcelReader.java
package com.demo.utils;

import com.demo.model.TestCase;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {
    public List<TestCase> readTestCases(String filePath) {
        List<TestCase> testCases = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                TestCase testCase = new TestCase();
                testCase.setTestId(getCellValue(row.getCell(0)));
                testCase.setEndpoint(getCellValue(row.getCell(1)));
                testCase.setMethod(getCellValue(row.getCell(2)));
                testCase.setRequestBody(getCellValue(row.getCell(3)));
                testCase.setHeaders(getCellValue(row.getCell(4)));
                testCase.setExpectedStatus(Integer.parseInt(getCellValue(row.getCell(5))));
                testCase.setExpectedResponse(getCellValue(row.getCell(6)));
                testCase.setEnabled(Boolean.parseBoolean(getCellValue(row.getCell(7))));
                
                if (testCase.isEnabled()) {
                    testCases.add(testCase);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return testCases;
    }
    
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return "";
        }
    }
}

// RestUtils.java
package com.demo.utils;

import com.demo.model.TestCase;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;

public class RestUtils {
    public static Response executeRequest(TestCase testCase) {
        RequestSpecification request = RestAssured.given()
            .log().all();
        
        // Add headers if present
        if (testCase.getHeaders() != null && !testCase.getHeaders().isEmpty()) {
            JSONObject headers = new JSONObject(testCase.getHeaders());
            headers.keys().forEachRemaining(key -> 
                request.header(key, headers.getString(key))
            );
        }
        
        // Add request body if present
        if (testCase.getRequestBody() != null && !testCase.getRequestBody().isEmpty()) {
            request.body(testCase.getRequestBody());
        }
        
        // Execute request based on method
        switch (testCase.getMethod().toUpperCase()) {
            case "GET": return request.get(testCase.getEndpoint());
            case "POST": return request.post(testCase.getEndpoint());
            case "PUT": return request.put(testCase.getEndpoint());
            case "DELETE": return request.delete(testCase.getEndpoint());
            default: throw new IllegalArgumentException("Unsupported method: " + testCase.getMethod());
        }
    }
}

// TestReporter.java
package com.demo.reporter;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.demo.constants.ApiConstants;
import com.demo.model.TestCase;
import io.restassured.response.Response;

public class TestReporter {
    private static ExtentReports extent;
    private static ExtentTest test;
    
    static {
        extent = new ExtentReports();
        ExtentSparkReporter spark = new ExtentSparkReporter(ApiConstants.REPORT_PATH);
        extent.attachReporter(spark);
    }
    
    public static void startTest(String testId) {
        test = extent.createTest(testId);
    }
    
    public static void logPass(TestCase testCase, Response response) {
        test.log(Status.PASS, "Test Passed: " + testCase.getTestId())
            .info("Response: " + response.asPrettyString());
    }
    
    public static void logFail(TestCase testCase, Response response) {
        test.log(Status.FAIL, "Test Failed: " + testCase.getTestId())
            .info("Response: " + response.asPrettyString());
    }
    
    public static void logError(TestCase testCase, Exception e) {
        test.log(Status.FAIL, "Test Error: " + testCase.getTestId() + "\n" +
                "Error Message: " + e.getMessage() + "\n" +
                "Stack Trace: " + e.getStackTrace());
    }
    
    public static void generateReport() {
        extent.flush();
    }
}

// TestBase.java
package com.demo.base;

import com.demo.constants.ApiConstants;
import com.demo.model.TestCase;
import com.demo.utils.ExcelReader;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeSuite;
import java.util.List;

public class TestBase {
    protected List<TestCase> testCases;
    protected ExcelReader excelReader;
    
    @BeforeSuite
    public void setup() {
        RestAssured.baseURI = ApiConstants.BASE_URL;
        excelReader = new ExcelReader();
        testCases = excelReader.readTestCases(ApiConstants.TEST_DATA_PATH);
    }
}

// ApiTests.java
package com.demo.tests;

import com.demo.base.TestBase;
import com.demo.model.TestCase;
import com.demo.reporter.TestReporter;
import com.demo.utils.RestUtils;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ApiTests extends TestBase {
    
    @Test(dataProvider = "testCases")
    public void executeApiTests(TestCase testCase) {
        try {
            TestReporter.startTest(testCase.getTestId());
            
            Response response = RestUtils.executeRequest(testCase);
            
            // Validate status code
            boolean isStatusValid = response.getStatusCode() == testCase.getExpectedStatus();
            
            if (isStatusValid) {
                TestReporter.logPass(testCase, response);
            } else {
                TestReporter.logFail(testCase, response);
            }
            
            Assert.assertEquals(response.getStatusCode(), 
                              testCase.getExpectedStatus(), 
                              "Status code mismatch for " + testCase.getTestId());
            
        } catch (Exception e) {
            TestReporter.logError(testCase, e);
            throw e;
        }
    }
}


base.url=https://jsonplaceholder.typicode.com
