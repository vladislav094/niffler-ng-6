package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.SpendConditions;
import guru.qa.niffler.model.DataFilterValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.EditSpendingPage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class SpendingTable extends BaseComponent<SpendingTable> {

    private final SearchField searchField = new SearchField();
//    private final SelenideElement self = $("#spendings");
    private final SelenideElement periodDropdown = self.$("#period");
    private final SelenideElement currencyDropdown = self.$("#currency");
    private final ElementsCollection optionForChoose = $$("[role='option']");
    private final ElementsCollection spendingsList = $("tbody").$$("tr");

    public SpendingTable() {
        super($("#spendings"));
    }

    @Step("Select spending period: {0}")
    @Nonnull
    public SpendingTable selectPeriod(DataFilterValues period) {
        periodDropdown.click();
        optionForChoose.find(text(period.text)).click();
        return this;
    }

    @Step("Edit spending description: {0}")
    @Nonnull
    public EditSpendingPage editSpending(String description) {
        searchSpendingByDescription(description);
        SelenideElement spending = spendingsList.find(text(description));
        spending.$$("td").get(5).click();
        return new EditSpendingPage();
    }

    @Step("Search spending by description: {0}")
    @Nonnull
    public SpendingTable searchSpendingByDescription(String description) {
        searchField.search(description);
        return this;
    }

    @Step("Find spending by name: {0}")
    @Nonnull
    public SpendingTable checkTableContains(String expectedSpend) {
        searchSpendingByDescription(expectedSpend);
        spendingsList.find(text(expectedSpend)).shouldBe(visible);
        return this;
    }

    @Step("Check count spendings: {0}")
    @Nonnull
    public SpendingTable checkTableSize(int expectedSize) {
        spendingsList.should(size(expectedSize));
        return this;
    }

    @Step("Check spendings in table on main page: {0}")
    @Nonnull
    public SpendingTable checkSpends(SpendJson... spends) {
        spendingsList.should(SpendConditions.spends(spends));
        return this;
    }
}
