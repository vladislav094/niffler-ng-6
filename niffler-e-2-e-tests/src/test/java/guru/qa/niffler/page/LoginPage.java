package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {

    public static final String URL = CFG.authUrl() + "login";

    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement createNewAccountLink = $(byText("Create new account"));
    private final SelenideElement errorText = $(".form__error");

    @Override
    public LoginPage checkThatPageLoaded() {
        return null;
    }

    @Step("Set credentials : {0}, {1}")
    @Nonnull
    public MainPage login(String username, String password) {
        setUsername(username);
        setPassword(password);
        clickLogInButton();
        return new MainPage();
    }

    @Step("Set username: {0}")
    @Nonnull
    public LoginPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    @Step("Set password: {0}")
    @Nonnull
    public LoginPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Click login button")
    @Nonnull
    public MainPage clickLogInButton() {
        submitButton.click();
        return new MainPage();
    }

    @Step("Click creat new account button")
    @Nonnull
    public RegisterPage clickCreateNewAccount() {
        createNewAccountLink.click();
        return new RegisterPage();
    }

    @Step("Check message about wrong credential: {0}")
    @Nonnull
    public LoginPage checkMessageThatWasInputBadCredentials(String badCredentialText) {
        errorText.shouldHave(text(badCredentialText)).shouldBe(visible);
        return this;
    }
}
