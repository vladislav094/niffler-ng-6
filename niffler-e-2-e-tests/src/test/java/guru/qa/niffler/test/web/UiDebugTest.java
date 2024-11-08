package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.model.DataFilterValues;
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SpendingTable;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;

public class UiDebugTest extends BaseWebTest{

    @Test
    public void debugTest() {

        step("Открываем страницу авторизации и заполняем форму персистентными данными (username, password)", () -> {
            Selenide.open(CFG.frontUrl(), LoginPage.class)
                    .setUsername("vladislav")
                    .setPassword("root")
                    .clickLogInButton();
            new Header().signOut();
        });
    }
}
