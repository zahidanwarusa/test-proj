This error is due to Apache POI version conflict. Let's fix the POI dependencies in pom.xml:

```xml
<!-- Update this section in your pom.xml -->
<properties>
    <!-- Other properties remain same -->
    <poi.version>5.2.3</poi.version>
    <commons-io.version>2.13.0</commons-io.version>
</properties>

<dependencies>
    <!-- Apache POI dependencies -->
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi</artifactId>
        <version>${poi.version}</version>
    </dependency>
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi-ooxml</artifactId>
        <version>${poi.version}</version>
    </dependency>
    <dependency>
        <groupId>commons-io</groupId>
        <artifactId>commons-io</artifactId>
        <version>${commons-io.version}</version>
    </dependency>
    
    <!-- Keep other dependencies as they are -->
</dependencies>

```

Then, update your ExcelReader class:

```java
package com.demo.utils;

import com.demo.model.TestCase;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {
    
    public List<TestCase> readTestCases(String filePath) {
        List<TestCase> testCases = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            System.out.println("Successfully opened Excel file: " + filePath);
            
            Sheet sheet = workbook.getSheetAt(0);
            System.out.println("Reading sheet: " + sheet.getSheetName());
            
            // Skip header row
            int rowNum = 1;
            while (rowNum <= sheet.getLastRowNum()) {
                Row row = sheet.getRow(rowNum);
                if (row != null) {
                    TestCase testCase = new TestCase();
                    
                    try {
                        testCase.setTestId(getCellValueAsString(row.getCell(0)));
                        testCase.setEndpoint(getCellValueAsString(row.getCell(1)));
                        testCase.setMethod(getCellValueAsString(row.getCell(2)));
                        testCase.setRequestBody(getCellValueAsString(row.getCell(3)));
                        testCase.setHeaders(getCellValueAsString(row.getCell(4)));
                        testCase.setExpectedStatus((int) row.getCell(5).getNumericCellValue());
                        testCase.setExpectedResponse(getCellValueAsString(row.getCell(6)));
                        testCase.setEnabled(true); // Default to true or read from cell 7
                        
                        testCases.add(testCase);
                        System.out.println("Read test case: " + testCase.getTestId());
                    } catch (Exception e) {
                        System.err.println("Error reading row " + rowNum + ": " + e.getMessage());
                    }
                }
                rowNum++;
            }
            
        } catch (IOException e) {
            System.err.println("Error reading Excel file: " + e.getMessage());
            e.printStackTrace();
        }
        
        return testCases;
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}

```

Steps to fix:
1. Update POI dependencies in pom.xml
2. Replace your ExcelReader.java with the updated version
3. Clean Maven repository and rebuild:
```bash
rm -rf ~/.m2/repository/org/apache/poi
mvn clean install -U
```

Key changes made:
1. Added commons-io dependency
2. Updated POI dependencies to ensure all required components are present
3. Enhanced ExcelReader with better error handling and logging
4. Added cell type handling for different data types

Let me know if you still see any issues!
