package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage extends BasePage<ProfilePage> {

    private final ElementsCollection tableRows = $("#spendings tbody").$$("tr");
    private final SelenideElement spendingHistoryTable = $("#spendings");
    private final SelenideElement spendingStatisticsCanvas = $("#stat");
    private final SelenideElement statisticsHeader = $(".css-giaux5");
    private final SelenideElement historySpendingHeader = $(".css-uxhuts");
    private final SelenideElement menuButton = $(".css-1obba8g");
    private final SelenideElement profileButton = $(".nav-link[href*='profile']");
    private final SelenideElement friendsButton = $(".nav-link[href*='friends']");
    private final ElementsCollection categoryRows = $$(".css-gq8o4k");

    @Step("Set spending description: {0}")
    @Nonnull
    public EditSpendingPage editSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription)).$$("td").get(5).click();
        return new EditSpendingPage();
    }

    @Step("Click menu button")
    @Nonnull
    public MainPage clickMenuButton() {
        menuButton.click();
        return this;
    }

    @Step("Click profile button")
    @Nonnull
    public ProfilePage clickProfileButton() {
        profileButton.click();
        return new ProfilePage();
    }

    @Step("Click friends button")
    @Nonnull
    public FriendsPage clickFriendsButton() {
        friendsButton.click();
        return new FriendsPage();
    }

    @Step("Check spending in table by name: {0}")
    @Nonnull
    public void checkThatTableContainsSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription)).should(visible);
    }

    @Step("Check header text: {0}")
    @Nonnull
    public MainPage checkThatNameOfStatisticsHeaderIsDisplayed(String headerText) {
        statisticsHeader.shouldHave(text(headerText)).shouldBe(visible);
        return this;
    }

    @Step("Check spending in history by name: {0}")
    @Nonnull
    public MainPage checkThatNameOfHistorySpendingHeaderIsDisplayed(String headerText) {
        historySpendingHeader.shouldHave(text(headerText)).shouldBe(visible);
        return this;
    }
}
