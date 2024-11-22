package guru.qa.niffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<ProfilePage> {

    private final SelenideElement createNewAccountLink = $(byText("Create new account"));
    private final SelenideElement errorText = $(".form__error");

    private final SelenideElement usernameInput;
    private final SelenideElement passwordInput;
    private final SelenideElement submitButton;
    private final SelenideElement registerButton;
    private final SelenideElement errorContainer;

    public LoginPage(SelenideDriver driver) {
        super(driver);
        this.usernameInput = driver.$("input[name='username']");
        this.passwordInput = driver.$("input[name='password']");
        this.submitButton = driver.$("button[type='submit']");
        this.registerButton = driver.$("a[href='/register']");
        this.errorContainer = driver.$(".form__error");
    }

    public LoginPage() {
        this.usernameInput = Selenide.$("input[name='username']");
        this.passwordInput = Selenide.$("input[name='password']");
        this.submitButton = Selenide.$("button[type='submit']");
        this.registerButton = Selenide.$("a[href='/register']");
        this.errorContainer = Selenide.$(".form__error");
    }


    @Step("Fill login page with credentials: username: {0}, password: {1}")
    @Nonnull
    public LoginPage fillLoginPage(String login, String password) {
        setUsername(login);
        setPassword(password);
        return this;
    }

    @Step("Submit login")
    @Nonnull
    public <T extends BasePage<?>> T submit(T expectedPage) {
        submitButton.click();
        return expectedPage;
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

    @Step("Check error on page: {error}")
    @Nonnull
    public LoginPage checkError(String error) {
        errorContainer.shouldHave(text(error));
        return this;
    }
}
