package tests;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.MediaEntityBuilder;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;

public class BillCalculationTest {

    WebDriver driver;
    WebDriverWait wait;
    ExtentReports extent;
    ExtentTest test;

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Setup Extent Report
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter("test-output/BillCalculationReport.html");
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
    }

    @DataProvider(name = "billData")
    public Object[][] provideBillData() {
        return new Object[][] {
                {1250, 113.25},
                {1000, 92.71},
                {750, 64.38},
                {500, 49.50},  // Fails
                {0, 7.00}
        };
    }

    @Test(dataProvider = "billData")
    public void testBillCalculation(int inputUnits, double expectedBill) throws IOException {
        test = extent.createTest("Bill Test for units: " + inputUnits);

        driver.get("http://localhost:5173/CalculateBills");
        test.info("Navigated to CalculateBills page");

        WebElement meterInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[type='number']")));
        meterInput.clear();
        meterInput.sendKeys(String.valueOf(inputUnits));
        test.info("Entered meter reading: " + inputUnits);

        WebElement calculateBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Calculate Bill']")));
        calculateBtn.click();
        test.info("Clicked on Calculate Bill");

        WebElement billElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[span[contains(text(), 'Total Bill:')]]")));
        String fullText = billElement.getText(); // e.g., "Total Bill: $92.71"
        String numberOnly = fullText.replace("Total Bill:", "").replace("$", "").trim();
        double actualBill = Double.parseDouble(numberOnly);

        test.info("Expected: $" + expectedBill + " | Actual: $" + actualBill);

        try {
            Assert.assertEquals(actualBill, expectedBill, 0.1, "Bill calculation is incorrect");
            test.pass("✅ Bill matched for units: " + inputUnits);
        } catch (AssertionError e) {
            String screenshotPath = takeScreenshot("BillTest_" + inputUnits);
            test.fail("❌ Mismatch for units: " + inputUnits +
                            " - Expected: $" + expectedBill +
                            " but found: $" + actualBill,
                    MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            throw e;
        }
    }

    private double calculateExpectedBill(int units) {
        double fixedCharge = 7.00;
        double ratePerUnit = 0.085;
        return Math.round((fixedCharge + units * ratePerUnit) * 100.0) / 100.0;
    }

    private String takeScreenshot(String testName) {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File src = ts.getScreenshotAs(OutputType.FILE);
        String path = "test-output/screenshots/" + testName + "_" + System.currentTimeMillis() + ".png";
        File dest = new File(path);
        dest.getParentFile().mkdirs(); // Create directory if not exists
        try {
            Files.copy(src.toPath(), dest.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dest.getAbsolutePath();
    }

    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
        extent.flush(); // Write results to report
    }
}
