package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;

public class UiDebugTest extends BaseWebTest {

    @Test
    public void debugTest() {

        step("Открываем страницу авторизации и заполняем форму персистентными данными (username, password)", () -> {
            Selenide.open(CFG.frontUrl(), LoginPage.class)
                    .setUsername("vladislav")
                    .setPassword("root")
                    .clickLogInButton()
                    .getHeader()
                    .signOut();
        });
    }
}
