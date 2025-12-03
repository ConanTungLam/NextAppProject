package testcase;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;

public class CreateManualOrderTestCase {
    @Test(priority = 1)
    void successfullyWithValidData(){
        WebDriver driver;
        driver = new ChromeDriver();
        driver.get("https://app.gearmentinc.com/login?app=pod");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[type='submit']"))
        );
        driver.findElement(By.xpath("//input[@name='email']")).sendKeys("lamvt+selenium@gearment.com");
        driver.findElement(By.xpath("//input[@name='password']")).sendKeys("Admin@123");
        driver.findElement(By.cssSelector("button[type=submit]")).click();

        WebElement draftOrderLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[href='/pod/orders/draft']")
        ));

        draftOrderLink.click();
        driver.findElement(By.xpath("//button[normalize-space(text())='Create order']")).click();
        driver.findElement(By.xpath("//div[text()='Manual order']")).click();
        driver.findElement(By.xpath("//button[text()='Catalog']")).click();
        driver.findElement(By.name("searchText")).sendKeys("G5000");

        WebElement selectedVariant = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//button[text()=\"Select\"])[1]"))
        );
        selectedVariant.click();
        driver.findElement(By.xpath("(//div[@class='space-y-1'])[1]")).click();

        WebElement artwork = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//img[@src='https://cdn.geadev.com/0TxKZ0Q9M75/artwork/4MxJQ7HCW7L9PFS.jpg']"))
        );
        artwork.click();
        driver.findElement(By.xpath("//button[normalize-space(text())='Save']")).click();
        driver.findElement(By.xpath("//button[normalize-space(text())='Continue']")).click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space(text())='Save']"))
        );

        driver.findElement(By.name("firstName")).sendKeys("Automation");
        driver.findElement(By.name("lastName")).sendKeys("Tester");
        driver.findElement(By.name("email")).sendKeys("lamvt+99@gearment.com");
        driver.findElement(By.name("phoneNo")).sendKeys("0123456789");

        driver.findElement(By.xpath("(//button[@role=\"combobox\"])[4]")).click();
        driver.findElement(By.xpath("//div[@cmdk-list-sizer]/div[1]")).click();


    }


}
