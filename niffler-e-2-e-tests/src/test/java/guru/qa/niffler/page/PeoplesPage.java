package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Selenide.$;

public class PeoplesPage extends BasePage<PeoplesPage> {

    public static final String URL = CFG.frontUrl() + "people/all";

    private final SelenideElement peopleTab = $("a[href='/people/friends']");
    private final SelenideElement allTab = $("a[href='/people/all']");

    private final SearchField searchInput = new SearchField();

    private final SelenideElement peopleTable = $("#all");
    private final SelenideElement pagePrevBtn = $("#page-prev");
    private final SelenideElement pageNextBtn = $("#page-next");

    @Step("Check that the page is loaded")
    @Override
    @Nonnull
    public PeoplesPage checkThatPageLoaded() {
        peopleTab.shouldBe(Condition.visible);
        allTab.shouldBe(Condition.visible);
        return this;
    }
}
