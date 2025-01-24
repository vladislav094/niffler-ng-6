package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static io.qameta.allure.Allure.step;

@DisplayName("Страница профиля")
@WebTest
public class ProfileWebTest extends BaseWebTest {

    @User(
            categories = @Category(
                    archived = false
            )
    )
    @ApiLogin
    @Test
    void archivedCategoryShouldPresentInCategoriesList(UserJson user) {
        final String testCategoryName = user.testData().categories().getFirst().name();

        Selenide.open(ProfilePage.URL, ProfilePage.class)
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
    @ApiLogin
    @Test
    void activeCategoryShouldPresentInCategoriesList(UserJson user) {
        final String testCategoryName = user.testData().categories().getFirst().name();

        Selenide.open(ProfilePage.URL, ProfilePage.class)
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
    @ApiLogin
    @Test
    void editProfile() {
        String randomName = RandomDataUtils.randomUsername().split("\\.")[0];

        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .setName(randomName)
                .clickSaveChange()
                .checkAlertMessage("Profile successfully updated");
    }

    @User
    @ApiLogin
    @ScreenShotTest("img/expected-avatar.png")
    public void checkAvatarAfterUploading(BufferedImage expected) throws IOException {
        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .uploadPicture("img/expected-avatar.png")
                .checkThatAvatarEqualsUploadingImage(expected);
    }
}
