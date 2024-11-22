package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public abstract class BasePage<T extends BasePage<?>> {

    protected final Header header = new Header();
    private final SelenideElement alert;
    protected final SelenideElement popup = $("[role='dialog']");
    private final ElementsCollection formErrors;

    protected BasePage(SelenideDriver driver) {
        this.alert = driver.$(".MuiSnackbar-root");
        this.formErrors = driver.$$("p.Mui-error, .input__helper-text");
    }

    public BasePage() {
        this.alert = Selenide.$(".MuiSnackbar-root");
        this.formErrors = Selenide.$$("p.Mui-error, .input__helper-text");
    }

    @Step("Check message: {0}")
    @Nonnull
    public T checkAlertMessage(String message) {
        alert.should(visible)
                .should(text(message));
        return (T) this;
    }

    @Nonnull
    public Header getHeader() {
        return header;
    }
}
