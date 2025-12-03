package tests;

import apis.AuthAPI;
import bases.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;


public class LoginTest extends BaseTest {
    private final String incorrectPassword = "Wr0ngP@ss0rd";

    @Test(priority = 1)
    public void successfulWithValidAccount() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(baseUrl);

        loginPage.login(email, password);

        Assert.assertTrue(loginPage.isLoginSuccess());
    }

    @Test(priority = 2)
    public void successfulLess5TimesWrongPassword() throws Exception {
        AuthAPI authAPI = new AuthAPI();
        authAPI.resetLoginAttempts(email, password);

        for (int i = 1; i <= 5; i++) {
            String attemptPassword = (i < 5) ? incorrectPassword : password;

            JsonNode root = authAPI.login(email, attemptPassword);

            if (i < 5) {
                Assert.assertTrue(
                        authAPI.isIncorrectPassword(root));
            } else {
                Assert.assertTrue(
                        authAPI.hasAccessToken(root));
            }
        }
    }

    @Test(priority = 3)
    public void unsuccessfulWithInvalidAccount() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(baseUrl);

        loginPage.login(email, incorrectPassword);

        Assert.assertTrue(loginPage.isWrongPasswordError());
    }

    @Test(priority = 4)
    public void unsuccessfulWithLeaveEmpty() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(baseUrl);

        loginPage.login("", "");

        Assert.assertTrue(loginPage.isBothEmailAndPasswordRequired());
    }

    @Test(priority = 5)
    public void unsuccessfulWithEmptyEmail() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(baseUrl);

        loginPage.login("", incorrectPassword);

        Assert.assertTrue(loginPage.isEmailRequired());
    }

    @Test(priority = 6)
    public void unsuccessfulWithEmptyPassword() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(baseUrl);

        loginPage.login(email, "");

        Assert.assertTrue(loginPage.isPasswordRequired());
    }

    @Test(priority = 7)
    public void unsuccessfulWithSQLInjection() {
        String sqlInjection = "'; select * from iam_user limit 10; --";

        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(baseUrl);

        loginPage.login(sqlInjection, "");

        Assert.assertTrue(loginPage.isEmailFormatInvalid());
    }

    @Test(priority = 8)
    public void blockedAccountAfter5TimesWrongPassword() throws Exception {
        String testEmail = UUID.randomUUID().toString().replace("-", "") + "@gearment.com";
        AuthAPI authAPI = new AuthAPI();

        for (int i = 1; i <= 5; i++) {
            JsonNode root = authAPI.login(testEmail, incorrectPassword);

            if (i < 5) {
                Assert.assertTrue(authAPI.isIncorrectPassword(root));
            } else {
                Assert.assertTrue(authAPI.isAccountLocked(root));
            }
        }
    }

}
