package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProfilePage {

    private final SelenideElement nameInput = $("#name");
    private final SelenideElement saveChangeBtn = $("button[type='submit']");
    private final SelenideElement categoryInput = $("input[name=category]");
    private final SelenideElement showArchivedButton = $(".css-1m9pwf3[type='checkbox']");
    private final ElementsCollection activeCategoryList = $$("div[tabindex='0'].MuiChip-root");
    private final ElementsCollection archiveCategoryList = $$("div[tabindex='-1'].MuiChip-root");
    private final SelenideElement archiveButton = $(By.xpath("//button[contains(text(), 'Archive')]"));
    private final SelenideElement unarchivedButton = $(By.xpath("//button[contains(text(), 'Unarchive')]"));
    private final SelenideElement messageAfterArchivedUnarchivedCategory = $(".MuiAlert-message");

    @Step("Set name: {0}")
    @Nonnull
    public ProfilePage setName(String name) {
        nameInput.clear();
        nameInput.setValue(name);
        return this;
    }

    @Step("Click save change button")
    @Nonnull
    public ProfilePage clickSaveChange() {
        saveChangeBtn.click();
        return this;
    }

    @Step("Set category name for create: {0}")
    @Nonnull
    public ProfilePage addNewCategory(String categoryName) {
        categoryInput.setValue(categoryName)
                .pressEnter();
        return this;
    }

    @Step("Set category name in search: {0}")
    @Nonnull
    public ProfilePage searchCategoryByName(String categoryName) {
        categoryInput.setValue(categoryName);
        categoryInput.pressEnter();
        return this;
    }

    @Step("Click show archived categories button")
    @Nonnull
    public ProfilePage showArchivedCategories() {
        showArchivedButton.scrollIntoView(false);
        showArchivedButton.click();
        return this;
    }

    @Step("Click show unarchived categories button")
    @Nonnull
    public ProfilePage showUnarchivedCatogories() {
        showArchivedButton.scrollIntoView(false);
        showArchivedButton.click();
        return this;
    }

    @Step("Check category by name {0} is active")
    @Nonnull
    public void shouldHaveActiveCategoryByName(String categoryName) {
        activeCategoryList.findBy(text(categoryName))
                .shouldBe(visible);
    }

    @Step("Check category by name {0} is archived")
    @Nonnull
    public void shouldHaveArchiveCategoryByName(String categoryName) {
        archiveCategoryList.findBy(text(categoryName))
                .shouldBe(visible);
    }

    @Step("Check message after archived category: {0}")
    @Nonnull
    public ProfilePage shouldHaveMessageAfterArchivedCategory(String categoryName) {
        final String successMessage = String.format("Category %s is archived", categoryName);
        messageAfterArchivedUnarchivedCategory.shouldHave(text(successMessage))
                .shouldBe(visible);
        return this;
    }

    @Step("Check message after unarchived category: {0}")
    @Nonnull
    public ProfilePage shouldHaveMessageAfterUnarchivedCategory(String categoryName) {
        final String successMessage = String.format("Category %s is unarchived", categoryName);
        messageAfterArchivedUnarchivedCategory.shouldHave(text(successMessage))
                .shouldBe(visible);
        return this;
    }

    @Step("Archived category by name: {0}")
    @Nonnull
    public ProfilePage archivedCategoryByName(String categoryName) {
        String archivedCategoryLocator = ".css-dxoo7k[aria-label='Archive category']";
        activeCategoryList.findBy(text(categoryName))
                .parent()
                .$(archivedCategoryLocator)
                .click();
        return this;
    }

    @Step("Unarchived category by name: {0}")
    @Nonnull
    public ProfilePage unarchivedCategoryByName(String name) {
        String unarchivedCategoryLocator = ".css-dxoo7k[aria-label='Unarchive category']";
        archiveCategoryList.findBy(text(name))
                .parent()
                .$(unarchivedCategoryLocator)
                .click();
        return this;
    }

    @Step("Check message about successful edit profile description: {0}")
    public void checkSuccessfulMessage(String message) {
        $("div[role='alert']")
                .should(visible)
                .should(text(message));
    }

    @Step("Click archived button")
    @Nonnull
    public ProfilePage clickArchiveButton() {
        archiveButton.click();
        return this;
    }

    @Step("Click unarchived button")
    @Nonnull
    public ProfilePage clickUnarchivedButton() {
        unarchivedButton.click();
        return this;
    }
}
