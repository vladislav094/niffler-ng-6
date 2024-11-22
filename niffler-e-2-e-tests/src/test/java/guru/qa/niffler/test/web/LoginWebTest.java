package guru.qa.niffler.test.web;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotations.Category;
import guru.qa.niffler.jupiter.annotations.Spending;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.jupiter.annotations.meta.WebTest;
import guru.qa.niffler.jupiter.extensions.BrowserExtension;
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.SelenideUtils;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static guru.qa.niffler.utils.RandomDataUtils.randomPassword;
import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static io.qameta.allure.Allure.step;

@DisplayName("Авторизация")
@WebTest
public class LoginWebTest extends BaseWebTest {

    private final String persistentName = "vladislav";
    private final String statisticsHeader = "Statistics";
    private final String historyOfSpendingHeader = "History of Spendings";
    private final String badCredentialMessage = "Неверные учетные данные пользователя";

    @RegisterExtension
    private final BrowserExtension browserExtension = new BrowserExtension();
    private final SelenideDriver driver = new SelenideDriver(SelenideUtils.chromeConfig);

    @User(
            categories = {
                    @Category(name = "cat_1", archived = false),
                    @Category(name = "cat_2", archived = true)
            },
            spendings = {
                    @Spending(
                            category = "cat_3",
                            description = "test_spend",
                            amount = 100
                    )
            }
    )
    @Test
    @Story("Успешная авторизация")
    @DisplayName("Выполняем авторизацию пользователя")
    public void testMainPageShouldBeDisplayedAfterSuccessfulLogin(UdUserJson user) {

        step("Открываем страницу авторизации и заполняем форму (username, password)", () -> {
            browserExtension.drivers().add(driver);
            driver.open(CFG.frontUrl());
            new LoginPage(driver)
                    .setUsername(user.username())
                    .setPassword(user.testData().password())
                    .clickLogInButton();
        });
        step("Проверяем наличие заголовков на главное странице после авторизации", () -> {
            page.mainPage.checkThatNameOfStatisticsHeaderIsDisplayed(statisticsHeader)
                    .checkThatNameOfHistorySpendingHeaderIsDisplayed(historyOfSpendingHeader);
        });
    }

    @Test
    @Story("Неуспешная авторизация")
    @DisplayName("Выполняем авторизацию пользователя с невалидным паролем")
    public void testUserShouldStayOnLoginPageAfterLoginWithBadCredential() {

        String invalidPassword = randomPassword(1, 3);

        step("Открываем страницу авторизации и заполняем форму, используя персистентный username и случайный password", () -> {
            Selenide.open(CFG.frontUrl(), LoginPage.class)
                    .setUsername(persistentName)
                    .setPassword(invalidPassword)
                    .clickLogInButton();
        });
        step("Проверяем наличие сообщения о неверных учетных данных при авторизации", () -> {
            page.loginPage.checkMessageThatWasInputBadCredentials(badCredentialMessage);
        });
    }

    @Test
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        SelenideDriver firefox = new SelenideDriver(SelenideUtils.firefoxConfig);
        browserExtension.drivers().addAll(List.of(driver, firefox));
        driver.open(CFG.frontUrl());
        firefox.open(CFG.frontUrl());
        new LoginPage(driver)
                .fillLoginPage(randomUsername(), "BAD")
                .checkError("Bad credentials!");
        firefox.$(".logo-section__text").should(Condition.text("Niffler!"));
    }
}
