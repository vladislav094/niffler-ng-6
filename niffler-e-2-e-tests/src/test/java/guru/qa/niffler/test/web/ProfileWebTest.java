package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.faker.RandomDataUtils;
import guru.qa.niffler.jupiter.annotations.Category;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.jupiter.annotations.meta.WebTest;
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;

@DisplayName("Страница профиля")
@WebTest
public class ProfileWebTest extends BaseWebTest {

    @User(
            categories = @Category(
                    archived = false
            )
    )
    @Test
    void archivedCategoryShouldPresentInCategoriesList(UdUserJson user) {
        final String testCategoryName = user.testData().categories().getFirst().name();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .setUsername(user.username())
                .setPassword(user.testData().password())
                .clickLogInButton()
                .clickMenuButton()
                .clickProfileButton()
                .searchCategoryByName(testCategoryName)
                .archivedCategoryByName(testCategoryName)
                .clickArchiveButton()
                .shouldHaveMessageAfterArchivedCategory(testCategoryName)
                .showArchivedCategories();

        step("Категория, которая была заархивирована, отображается в списке архивных категорий", () -> {
            page.profilePage.shouldHaveArchiveCategoryByName(testCategoryName);
        });
    }

    @User(
            categories = @Category(
                    archived = true
            )
    )
    @Test
    void activeCategoryShouldPresentInCategoriesList(UdUserJson user) {
        final String testCategoryName = user.testData().categories().getFirst().name();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .setUsername(user.username())
                .setPassword(user.testData().password())
                .clickLogInButton()
                .clickMenuButton()
                .clickProfileButton()
                .showArchivedCategories()
                .searchCategoryByName(testCategoryName)
                .unarchivedCategoryByName(testCategoryName)
                .clickUnarchivedButton()
                .shouldHaveMessageAfterUnarchivedCategory(testCategoryName)
                .showUnarchivedCatogories();

        step("Категория, которая была разархивирована, отображается в списке активных категорий", () -> {
            page.profilePage.shouldHaveActiveCategoryByName(testCategoryName);
        });
    }

    @User
    @Test
    void editProfile(UdUserJson user) {
        String randomName = RandomDataUtils.randomUsername().split("\\.")[0];

        Selenide.open(frontUrl, LoginPage.class)
                .login(user.username(), user.testData().password())
                .getHeader()
                .toProfilePage()
                .setName(randomName)
                .clickSaveChange()
                .checkSuccessfulMessage("Profile successfully updated");
    }
}
