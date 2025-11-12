package STG;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;



public class LoginTestCase {

    @Test(priority = 1)
    void successfullyWithValidAccount(){
        WebDriver driver;
        driver = new ChromeDriver();
        driver.get("https://app.gearmentinc.com/login?app=pod");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[type='submit']"))
        );
        driver.findElement(By.xpath("//input[@name='email']")).sendKeys("lamvt@gearment.com");
        driver.findElement(By.xpath("//input[@name='password']")).sendKeys("Admin@12345");
        driver.findElement(By.cssSelector("button[type=submit]")).click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.tagName("body"),
                "Dashboard"
        ));
        Assert.assertEquals(driver.getCurrentUrl(), "https://app.gearmentinc.com/pod");

        driver.quit();
    }

    @Test(priority = 2)
    public void successfullyLess5TimesWrongPassword() throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        String url = "https://api.gearmentinc.com/iam/api.iam.v1.UserAccountAPI/UserLogin";
        ObjectMapper mapper = new ObjectMapper();

        HttpRequest resetLoginReq = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"email\":\"lamvt@gearment.com\",\"password\":\"Admin@12345\"}"))
                .build();
        client.send(resetLoginReq, HttpResponse.BodyHandlers.discarding());

        for (int i = 1; i <= 5; i++) {
            String password = (i < 5) ? "Admin@1234" : "Admin@12345";
            String body = String.format("{\"email\":\"lamvt@gearment.com\",\"password\":\"%s\"}", password);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            String json = resp.body();
            JsonNode root = mapper.readTree(json);

            if (i < 5) {
                String message = root.path("message").asText("");
                Assert.assertTrue(
                        message.contains("The email or password you entered is incorrect."),
                        "Attempt " + i + " expected incorrect password, but got: " + message
                );
            } else {
                JsonNode tokenNode = root.path("accessToken");
                Assert.assertTrue(
                        !tokenNode.isMissingNode() && !tokenNode.asText().isEmpty(),
                        "Attempt 5 expected accessToken, but got none. Response: " + json
                );
            }
        }
    }

    @Test(priority = 3)
    void unsuccessfullyWithInvalidAccount(){
        WebDriver driver;
        driver = new ChromeDriver();
        driver.get("https://app.gearmentinc.com/login?app=pod");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[type='submit']"))
        );
        driver.findElement(By.xpath("//input[@name='email']")).sendKeys("lamvt@gearment.com");
        driver.findElement(By.xpath("//input[@name='password']")).sendKeys("Tunglam@955");
        driver.findElement(By.cssSelector("button[type=submit]")).click();
        wait.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@variant='error'][2]"))
        );
        WebElement errorMessage = driver.findElement(By.xpath("//div[@variant='error'][2]"));
        Assert.assertTrue(errorMessage.getText().contains("The email or password you entered is incorrect."));

        driver.quit();
    }

    @Test(priority = 4)
    void unsuccessfullyWithEmptyInput(){
        WebDriver driver;
        driver = new ChromeDriver();
        driver.get("https://app.gearmentinc.com/login?app=pod");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[type='submit']"))
        );
        driver.findElement(By.xpath("//input[@name='email']")).sendKeys("");
        driver.findElement(By.xpath("//input[@name='password']")).sendKeys("");
        driver.findElement(By.cssSelector("button[type=submit]")).click();
        wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("p.text-red"))
        );
        WebElement errorMessage = driver.findElement(By.cssSelector("p.text-red"));
        Assert.assertTrue(errorMessage.getText().contains("Email is required"));

        driver.quit();
    }

    @Test(priority = 5)
    void unsuccessfullyWithSQLInjection(){
        WebDriver driver;
        driver = new ChromeDriver();
        driver.get("https://app.gearmentinc.com/login?app=pod");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[type='submit']"))
        );
        driver.findElement(By.xpath("//input[@name='email']")).sendKeys("'; select * from iam_user limit 10; --");
        driver.findElement(By.xpath("//input[@name='password']")).sendKeys("");
        driver.findElement(By.cssSelector("button[type=submit]")).click();
        wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("p.text-red"))
        );
        WebElement errorMessage = driver.findElement(By.cssSelector("p.text-red"));
        Assert.assertTrue(errorMessage.getText().contains("Please enter a valid email"));

        driver.quit();
    }

    @Test(priority = 6)
    public void unsuccessfullyAfter5TimesWrongPassword() throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        String url = "https://api.gearmentinc.com/iam/api.iam.v1.UserAccountAPI/UserLogin";
        String sampleEmail =  UUID.randomUUID().toString().replace("-", "") + "@gearment.com";
        ObjectMapper mapper = new ObjectMapper();

        for (int i = 1; i <= 5; i++) {
            String password = "Test@123";
            String body = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", sampleEmail, password);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            String json = resp.body();
            JsonNode root = mapper.readTree(json);

            String message = root.path("message").asText("");

            if (i < 5) {
                Assert.assertTrue(
                        message.contains("The email or password you entered is incorrect."),
                        "Attempt " + i + " expected incorrect password, but got: " + message
                );
            } else {
                Assert.assertTrue(
                        message.contains("Your account has been locked due to too many failed login attempts."),
                        "Attempt " + i + " expected count down time, but got: " + message
                );
            }
        }
    }
}

