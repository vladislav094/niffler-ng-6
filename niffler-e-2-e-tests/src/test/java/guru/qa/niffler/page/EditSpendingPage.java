package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.component.Calendar;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class EditSpendingPage extends BasePage<ProfilePage> {

    private final Calendar calendar = new Calendar();
    private final SelenideElement descriptionInput = $("#description");
    private final SelenideElement amountInput = $("#amount");
    private final SelenideElement categoryInput = $("#category");
    private final SelenideElement currencyDropdown = $("#currency");
    private final SelenideElement saveBtn = $("#save");

    public EditSpendingPage setNewSpendingDescription(String description) {
        descriptionInput.clear();
        descriptionInput.setValue(description);
        return this;
    }

    public void save() {
        saveBtn.click();
    }

    public Calendar getCalendar() {
        return calendar;
    }

    @Step("Fill spend data")
    @Nonnull
    public EditSpendingPage fillSpending(SpendJson spend) {
        setAmount(spend.amount());
        setCurrency(spend.currency());
        setCategory(spend.category());
        setDate(spend.spendDate());
        setDescription(spend.description());
        return this;
    }

    @Step("Set amount: {0}")
    @Nonnull
    public EditSpendingPage setAmount(double amount) {
        amountInput.clear();
        amountInput.setValue(String.valueOf(amount));
        return this;
    }

    @Step("Set currency: {0}")
    @Nonnull
    public EditSpendingPage setCurrency(CurrencyValues currency) {
        currencyDropdown.setValue(currency.name());
        return this;
    }

    @Step("Set category name: {0}")
    @Nonnull
    public EditSpendingPage setCategory(CategoryJson category) {
        categoryInput.setValue(category.name());
        return this;
    }

    @Step("Set date: {0}")
    @Nonnull
    public EditSpendingPage setDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String frmDate = sdf.format(date);
        calendar.setDate(frmDate);
        return this;
    }

    @Step("Set spend description: {0}")
    @Nonnull
    public EditSpendingPage setDescription(String description) {
        descriptionInput.setValue(description);
        return this;
    }

    @Step("Click save button")
    @Nonnull
    public EditSpendingPage saveSpending() {
        saveBtn.click();
        return this;
    }
}
