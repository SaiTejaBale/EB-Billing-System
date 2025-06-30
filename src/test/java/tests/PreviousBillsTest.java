package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class PreviousBillsTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void login() {
        driver.get("http://localhost:5173/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userId")));
        usernameInput.sendKeys("00000001");

        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.sendKeys("12345@Sa");

        WebElement signInButton = driver.findElement(By.xpath("//button[text()='Sign In']"));
        signInButton.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Logout")));

        WebElement prevBillButton = driver.findElement(By.linkText("Past Readings"));
        prevBillButton.click();

        // Wait for month element to be visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'text-2xl') and contains(text(),'2025')]")));
    }

    @DataProvider(name = "billData")
    public Object[][] billDataProvider() {
        return new Object[][]{
                // ✅ Positive Case
                {
                        List.of(
                                Map.of("month", "May 2025", "status", "PAID", "units", "350")
                        )
                },
                // ❌ Negative Case (Incorrect units)
                {
                        List.of(
                                Map.of("month", "May 2025", "status", "PAID", "units", "999")
                        )
                }
        };
    }


    @Test(dataProvider = "billData", dependsOnMethods = {"login"})
    public void validatePreviousBillsDynamically(List<Map<String, String>> expectedBills) {
        List<WebElement> monthElements = driver.findElements(By.xpath(
                "//div[contains(@class,'text-2xl') and contains(@class,'italic')]"
        ));

        System.out.println("Number of month divs found: " + monthElements.size());

        for (Map<String, String> expected : expectedBills) {
            boolean found = false;

            for (WebElement monthElement : monthElements) {
                String monthText = monthElement.getText().trim();

                if (monthText.equalsIgnoreCase(expected.get("month"))) {
                    // Locate the parent, then find the sibling div with status and units
                    WebElement monthContainer = monthElement.findElement(By.xpath("./parent::div"));
                    WebElement statusUnitsContainer = monthContainer.findElement(By.xpath("following-sibling::div[contains(@class,'mt-8')]"));

                    String status = statusUnitsContainer.findElement(By.xpath("./div[1]")).getText().trim();
                    String units = statusUnitsContainer.findElement(By.xpath("./div[2]")).getText().trim();

                    System.out.printf("Expected: %s | Found: Month='%s', Status='%s', Units='%s'\n",
                            expected, monthText, status, units);

                    if (status.equalsIgnoreCase(expected.get("status")) &&
                            units.equalsIgnoreCase(expected.get("units") + " units")) {
                        found = true;
                        break;
                    }
                }
            }

            Assert.assertTrue(found, "Bill not found: " + expected);
        }
    }

    @Test(dataProvider = "billData", dependsOnMethods = {"login"})
    public void validatePreviousBillsWithExpectedFailure(List<Map<String, String>> expectedBills) {
        boolean atLeastOneFail = false;

        List<WebElement> monthElements = driver.findElements(By.xpath(
                "//div[contains(@class,'text-2xl') and contains(@class,'italic')]"
        ));

        for (Map<String, String> expected : expectedBills) {
            boolean found = false;

            for (WebElement monthElement : monthElements) {
                String monthText = monthElement.getText().trim();

                if (monthText.equalsIgnoreCase(expected.get("month"))) {
                    WebElement monthContainer = monthElement.findElement(By.xpath("./parent::div"));
                    WebElement statusUnitsContainer = monthContainer.findElement(By.xpath("following-sibling::div[contains(@class,'mt-8')]"));

                    String status = statusUnitsContainer.findElement(By.xpath("./div[1]")).getText().trim();
                    String units = statusUnitsContainer.findElement(By.xpath("./div[2]")).getText().trim();

                    if (status.equalsIgnoreCase(expected.get("status")) &&
                            units.equalsIgnoreCase(expected.get("units") + " units")) {
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                System.out.println("✅ Negative case passed. Expected bill not found: " + expected);
                atLeastOneFail = true;
            } else {
                System.out.println("❌ Negative case failed. Unexpected match found: " + expected);
            }
        }

        Assert.assertTrue(atLeastOneFail, "Negative test case failed — invalid data unexpectedly matched.");
    }


    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
