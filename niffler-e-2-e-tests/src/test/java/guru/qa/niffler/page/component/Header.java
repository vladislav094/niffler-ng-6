package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.*;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class Header extends BaseComponent<Header> {

    private final Calendar calendar = new Calendar();
//    private final SelenideElement self = $("#root header");
    private final SelenideElement menuButton = self.$("[aria-label='Menu'");
    private final SelenideElement mainPageLink = self.$("a[href*='/main']");
    private final SelenideElement addSpendingsButton = self.$("a[href*='/spending']");
    private final SelenideElement menu = $("ul[role='menu']");
    private final ElementsCollection menuList = menu.$$("li");

    public Header() {
        super($("#root header"));
    }

    @Step("Open friends page")
    @Nonnull
    public FriendsPage toFriendsPage() {
        menuButton.click();
        menuList.find(text("Friends")).click();
        return new FriendsPage();
    }

    @Step("Open all people page")
    @Nonnull
    public PeoplesPage toAllPeoplesPage() {
        menuButton.click();
        menuList.find(text("All People")).click();
        return new PeoplesPage();
    }

    @Step("Open profile page")
    @Nonnull
    public ProfilePage toProfilePage() {
        menuButton.click();
        menuList.find(text("Profile")).click();
        return new ProfilePage();
    }

    @Step("Log out from account")
    @Nonnull
    public LoginPage signOut() {
        menuButton.click();
        menuList.find(text("Sign out")).click();
        $$("button").find(text("Log out")).click();
        return new LoginPage();
    }

    @Step("Open add new spending page")
    @Nonnull
    public EditSpendingPage toAddSpendingPage() {
        addSpendingsButton.click();
        return new EditSpendingPage();
    }

    @Step("Open main page")
    @Nonnull
    public MainPage toMainPage() {
        mainPageLink.click();
        return new MainPage();
    }
}
