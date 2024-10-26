package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotations.Category;
import guru.qa.niffler.jupiter.annotations.Spending;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.jupiter.annotations.meta.WebTest;
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.page.LoginPage;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.faker.RandomDataUtils.randomPassword;
import static io.qameta.allure.Allure.step;

@DisplayName("Авторизация")
@WebTest
public class LoginWebTest extends BaseWebTest {

    private final String persistentName = "vladislav";
    private final String statisticsHeader = "Statistics";
    private final String historyOfSpendingHeader = "History of Spendings";
    private final String badCredentialMessage = "Неверные учетные данные пользователя";


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

        step("Открываем страницу авторизации и заполняем форму персистентными данными (username, password)", () -> {
            Selenide.open(CFG.frontUrl(), LoginPage.class)
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
}
