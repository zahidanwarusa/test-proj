I'll help you set up the complete test structure with sample test cases using the JSONPlaceholder API.

1. First, let's create the Excel sheet:



Create `src/test/resources/TestCases.xlsx` with these columns:

| TestId | Endpoint | Method | RequestBody | Headers | ExpectedStatus | ExpectedResponse | Enabled |
|--------|----------|--------|-------------|---------|----------------|------------------|---------|
| TC001 | /posts/1 | GET | | {"Content-Type": "application/json"} | 200 | $.title | true |
| TC002 | /posts | POST | {"title": "foo", "body": "bar", "userId": 1} | {"Content-Type": "application/json"} | 201 | $.id | true |
| TC003 | /users/1 | GET | | {"Content-Type": "application/json"} | 200 | $.name | true |
| TC004 | /posts/1 | PUT | {"id": 1, "title": "updated title", "body": "updated body", "userId": 1} | {"Content-Type": "application/json"} | 200 | $.title | true |
| TC005 | /posts/1 | DELETE | | {"Content-Type": "application/json"} | 200 | | true |

Note: Create this in Excel, not as a markdown table. Each column should be in a separate cell.


2. Next, let's create the TestNG XML file:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite name="API Test Suite" parallel="methods" thread-count="2">
    
    <!-- Test Configuration -->
    <parameter name="environment" value="test"/>
    
    <!-- API Tests -->
    <test name="REST API Tests">
        <classes>
            <class name="com.demo.tests.ApiTests"/>
        </classes>
    </test>
    
</suite>

```

3. Let's create a properties file for configuration:

```properties
# Base URL for API
base.url=https://jsonplaceholder.typicode.com

# Test Data
test.data.path=src/test/resources/TestCases.xlsx

# Reporting
report.path=test-output/ExtentReport.html

# Timeouts (in seconds)
request.timeout=30

```

4. Let's update the test class for better readability and logging:

```java
package com.demo.tests;

import com.demo.base.TestBase;
import com.demo.model.TestCase;
import com.demo.reporter.TestReporter;
import com.demo.utils.RestUtils;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.*;

public class ApiTests extends TestBase {
    
    @BeforeClass
    public void setUpClass() {
        System.out.println("Starting API Tests...");
    }
    
    @DataProvider(name = "apiTestData")
    public Object[][] getTestData() {
        return testCases.stream()
            .map(tc -> new Object[]{tc})
            .toArray(Object[][]::new);
    }
    
    @Test(dataProvider = "apiTestData")
    public void executeApiTests(TestCase testCase) {
        System.out.println("\nExecuting Test: " + testCase.getTestId());
        System.out.println("Endpoint: " + testCase.getEndpoint());
        System.out.println("Method: " + testCase.getMethod());
        
        try {
            TestReporter.startTest(testCase.getTestId());
            
            // Execute request
            Response response = RestUtils.executeRequest(testCase);
            
            // Log response details
            System.out.println("Response Status Code: " + response.getStatusCode());
            System.out.println("Response Body: " + response.asPrettyString());
            
            // Validate status code
            Assert.assertEquals(
                response.getStatusCode(), 
                testCase.getExpectedStatus(), 
                "Status code mismatch for " + testCase.getTestId()
            );
            
            // Validate response if expected
            if (testCase.getExpectedResponse() != null && !testCase.getExpectedResponse().isEmpty()) {
                String actualValue = response.jsonPath().getString(testCase.getExpectedResponse());
                System.out.println("Expected Response Path: " + testCase.getExpectedResponse());
                System.out.println("Actual Value: " + actualValue);
                Assert.assertNotNull(actualValue, "Response validation failed - null value");
            }
            
            TestReporter.logPass(testCase, response);
            
        } catch (Exception e) {
            TestReporter.logError(testCase, e);
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @AfterClass
    public void tearDownClass() {
        TestReporter.generateReport();
        System.out.println("\nAPI Tests Completed");
    }
}

```

To run the tests:

1. First, make sure all files are in place:
```
src/
├── main/
│   ├── resources/
│   │   └── config.properties
│   └── java/
│       └── com/demo/...
└── test/
    ├── resources/
    │   ├── TestCases.xlsx
    │   └── testng.xml
    └── java/
        └── com/demo/tests/ApiTests.java
```

2. Run tests using Maven:
```bash
mvn clean test
```

Or run specific test:
```bash
mvn test -Dtest=ApiTests
```

Expected results:
- The tests will run against JSONPlaceholder API
- You'll see detailed console output for each test
- An Extent Report will be generated in test-output folder
- Test results will include:
  - GET /posts/1 - Should return a post
  - POST /posts - Should create a new post
  - GET /users/1 - Should return user details
  - PUT /posts/1 - Should update a post
  - DELETE /posts/1 - Should delete a post

Sample test output will look like:
```
Starting API Tests...

Executing Test: TC001
Endpoint: /posts/1
Method: GET
Response Status Code: 200
Response Body: {
    "userId": 1,
    "id": 1,
    "title": "sunt aut facere...",
    "body": "quia et suscipit..."
}

Executing Test: TC002
Endpoint: /posts
Method: POST
...
```

Would you like me to help you run these tests or explain any part in more detail?
