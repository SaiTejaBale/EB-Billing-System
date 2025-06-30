package com.tangedco.ebbilling.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LoginTest {
    public static WebDriver driver;

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://localhost:5173/login"); // replace with your login URL
    }

    @Test
    public void testLogin() {
// Find the input by id and type "00000001"

        WebElement userIdInput = driver.findElement(By.id("userId"));
        userIdInput.sendKeys("00000001");

        // Enter password
        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.sendKeys("12345@Sa");

        // Optional: Validate input
        assert userIdInput.getAttribute("value").equals("00000001") : "User ID not entered correctly!";
        assert passwordInput.getAttribute("value").equals("12345@Sa") : "Password not entered correctly!";

        WebElement signInButton = driver.findElement(By.xpath("//button[text()='Sign In']"));
        signInButton.click();

    }

//    @AfterClass
//    public void teardown() {
//        driver.quit();
//    }
}
