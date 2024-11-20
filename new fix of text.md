This error typically occurs due to one of several common issues. Let's fix them step by step:

1. First, let's update the maven-surefire-plugin configuration in pom.xml:

```xml
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
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <rest-assured.version>5.3.0</rest-assured.version>
        <testng.version>7.7.1</testng.version>
        <poi.version>5.2.3</poi.version>
        <lombok.version>1.18.26</lombok.version>
        <log4j.version>2.20.0</log4j.version>
        <extentreports.version>5.0.9</extentreports.version>
        <json.version>20230227</json.version>
    </properties>

    <!-- Dependencies remain the same as before -->
    <dependencies>
        <!-- ... all your existing dependencies ... -->
    </dependencies>

    <build>
        <plugins>
            <!-- Compiler Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>${maven.compiler.source}</source>
                    <target>${maven.compiler.target}</target>
                </configuration>
            </plugin>

            <!-- Surefire Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.0.0-M5</version>
                <configuration>
                    <suiteXmlFiles>
                        <suiteXmlFile>${project.basedir}/src/test/resources/testng.xml</suiteXmlFile>
                    </suiteXmlFiles>
                    <testFailureIgnore>false</testFailureIgnore>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

```

2. Make sure your testng.xml is in the correct location and has the correct content:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="API Test Suite">
    <test name="API Tests">
        <classes>
            <class name="com.demo.tests.ApiTests"/>
        </classes>
    </test>
</suite>

```

3. Update your ApiTests class to ensure the DataProvider is working correctly:

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

    @Test
    public void sampleApiTest() {
        // This is a simple test to verify the test execution
        System.out.println("Running sample API test");
        Assert.assertTrue(true, "Sample test passed");
    }
    
    @Test(dataProvider = "apiTestData", enabled = true)
    public void executeApiTests(TestCase testCase) {
        System.out.println("\nExecuting Test: " + testCase.getTestId());
        
        try {
            TestReporter.startTest(testCase.getTestId());
            Response response = RestUtils.executeRequest(testCase);
            Assert.assertEquals(
                response.getStatusCode(), 
                testCase.getExpectedStatus(), 
                "Status code mismatch for " + testCase.getTestId()
            );
            TestReporter.logPass(testCase, response);
        } catch (Exception e) {
            TestReporter.logError(testCase, e);
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @DataProvider(name = "apiTestData")
    public Object[][] getTestData() {
        if (testCases == null || testCases.isEmpty()) {
            // Return a dummy test case if no data is loaded
            TestCase dummy = new TestCase();
            dummy.setTestId("TC001");
            dummy.setEndpoint("/posts/1");
            dummy.setMethod("GET");
            dummy.setExpectedStatus(200);
            return new Object[][]{{dummy}};
        }
        return testCases.stream()
            .map(tc -> new Object[]{tc})
            .toArray(Object[][]::new);
    }
}

```

4. Verify your project structure:
```
your-project/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── demo/
│   │   │           ├── base/
│   │   │           ├── model/
│   │   │           └── utils/
│   │   └── resources/
│   │       └── config.properties
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── demo/
│       │           └── tests/
│       │               └── ApiTests.java
│       └── resources/
│           ├── TestCases.xlsx
│           └── testng.xml
```

5. Try running the tests with these commands:
```bash
# Clean and install dependencies
mvn clean install -DskipTests

# Run tests
mvn test
```

If you're still getting errors, can you share:
1. The exact error message you're getting?
2. The content of your project's src/test/resources directory?
3. The output of `mvn -X test` (debug mode)?

Also, try these troubleshooting steps:
1. Make sure testng.xml is in the correct location
2. Verify file permissions
3. Try running a single test without the DataProvider first
4. Check if your Excel file is being read correctly

<plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>${maven.compiler.source}</source>
                    <target>${maven.compiler.target}</target>
                    <release>${maven.compiler.release}</release>
                    <encoding>${project.build.sourceEncoding}</encoding>
                    <compilerArgs>
                        <arg>--enable-preview</arg>
                    </compilerArgs>
                </configuration>
            </plugin>

<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <maven.compiler.release>21</maven.compiler.release>


<properties>
        <!-- Java Configuration -->
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <maven.compiler.release>21</maven.compiler.release>

        <!-- Dependencies Versions -->
        <rest-assured.version>5.3.0</rest-assured.version>
        <testng.version>7.8.0</testng.version>
        <poi.version>5.2.3</poi.version>
        <lombok.version>1.18.30</lombok.version>
        <log4j.version>2.20.0</log4j.version>
        <extentreports.version>5.1.1</extentreports.version>
        <json.version>20231013</json.version>
    </properties



























I see the issue. There are two problems:
1. The TestNG configuration is trying to use a listener that doesn't exist
2. The package name in testng.xml doesn't match our project structure

Let's fix both issues:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="API Test Suite" parallel="methods" thread-count="2">
    
    <test name="REST API Tests">
        <classes>
            <class name="com.demo.tests.ApiTests"/>
        </classes>
    </test>
    
</suite>

```

And let's update the project structure to ensure all classes are in the correct packages:

```
src/
├── main/
│   └── java/
│       └── com/
│           └── demo/
│               ├── base/
│               │   └── TestBase.java
│               ├── constants/
│               │   └── ApiConstants.java
│               ├── model/
│               │   └── TestCase.java
│               └── utils/
│                   ├── ExcelReader.java
│                   └── RestUtils.java
└── test/
    └── java/
        └── com/
            └── demo/
                └── tests/
                    └── ApiTests.java
```

Please ensure your ApiTests class has the correct package declaration:

```java
package com.demo.tests;

import com.demo.base.TestBase;
import com.demo.model.TestCase;
import org.testng.annotations.Test;
import io.restassured.response.Response;
import org.testng.Assert;

public class ApiTests extends TestBase {
    
    @Test(dataProvider = "apiTestData")
    public void executeApiTests(TestCase testCase) {
        try {
            System.out.println("Executing Test: " + testCase.getTestId());
            
            // Your test implementation here
            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
}

```

Also, make sure your base class is in the correct package:

```java
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
    
    @BeforeSuite
    public void setup() {
        RestAssured.baseURI = ApiConstants.BASE_URL;
        excelReader = new ExcelReader();
        testCases = excelReader.readTestCases(ApiConstants.TEST_DATA_PATH);
    }
    
    @DataProvider(name = "apiTestData")
    public Object[][] getTestData() {
        return testCases.stream()
            .map(tc -> new Object[]{tc})
            .toArray(Object[][]::new);
    }
}

```

Steps to fix:
1. Update testng.xml with the correct package name
2. Ensure all your Java files are in the correct packages
3. Remove any references to TestNGTestListener
4. Clean and rebuild:

```bash
mvn clean test
```

If you're still seeing issues, can you confirm:
1. Are all your classes in the correct packages?
2. Did you update the testng.xml file with the correct package name?
3. Are there any other configuration files I should know about?
