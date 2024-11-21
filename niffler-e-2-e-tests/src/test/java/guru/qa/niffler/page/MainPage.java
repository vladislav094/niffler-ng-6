package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
    private final SelenideElement statisticComponent = $("canvas[role='img']");
    private final ElementsCollection statisticCells = $$("#legend-container li");
    private final SelenideElement deleteBtn = $("#delete");

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

    public MainPage deleteSpending(String spendingDescription) {
        SelenideElement spend = tableRows.find(text(spendingDescription));
        spend.click();
        deleteBtn.click();
        popup.find(byText("Delete")).click();
        return this;
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

    @SneakyThrows
    @Step("Check statistic component equals expected picture")
    public MainPage checkStatisticComponent(BufferedImage expected) {
        Thread.sleep(3000);
        BufferedImage actual = ImageIO.read(statisticComponent.screenshot());
        assertFalse(new ScreenDiffResult(actual, expected));
        return this;
    }

    @Step("Check that cells under statistic component have category by name: {0}")
    public MainPage checkCellsByCategoryName(List<String> categoriesName) {
        for (String category: categoriesName) {
            statisticCells.findBy(text(category))
                    .shouldBe(visible);
        }
        return this;
    }
}
