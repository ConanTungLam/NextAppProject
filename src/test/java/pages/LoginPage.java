package pages;

import bases.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class LoginPage extends BasePage {

    private final By emailField    = By.xpath("//input[@name='email']");
    private final By passwordField = By.xpath("//input[@name='password']");
    private final By loginButton   = By.cssSelector("button[type='submit']");


    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open(String baseUrl) {
        driver.get(baseUrl);
        return this;
    }

    public void login(String email, String password) {
        type(emailField, email);
        type(passwordField, password);
        click(loginButton);
    }

    public boolean isLoginSuccess() {
        return waitForText(By.tagName("body"), "Dashboard");
    }

    public boolean isWrongPasswordError() {
        return waitForText(
                By.xpath("//div[@variant='error'][2]"),
                "The email or password you entered is incorrect");
    }

    public boolean isEmailFormatInvalid() {
        return waitForText(
                By.cssSelector("p.text-red"),
                "Please enter a valid email");
    }

    public boolean isBothEmailAndPasswordRequired() {
        List<WebElement> errors = driver.findElements(By.cssSelector("p.text-red"));
        String allText = errors.stream()
                .map(WebElement::getText)
                .collect(Collectors.joining(" "));

        return allText.contains("Email is required")
                && allText.contains("Password is required");
    }

    public boolean isEmailRequired() {
        return waitForText(
                By.cssSelector("p.text-red"),
                "Email is required");
    }

    public boolean isPasswordRequired() {
        return waitForText(
                By.cssSelector("p.text-red"),
                "Password is required");
    }

    public boolean isBlockedAccountSession() {
        return waitForText(
                By.xpath("//div[@variant='error'][2]"),
                "Your account has been locked due to too many failed login attempts.");
    }

}
