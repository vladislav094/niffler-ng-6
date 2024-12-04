package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage extends BasePage<FriendsPage> {

    public static final String URL = CFG.frontUrl() + "people/friends";

    private final SelenideElement peopleTab = $("a[href='/people/friends']");
    private final SelenideElement allTab = $("a[href='/people/all']");

    protected final SearchField searchField = new SearchField();
    private final SelenideElement friendsList = $(".MuiButtonBase-root[href*='friends']");
    private final SelenideElement allPeoplesList = $(".MuiButtonBase-root[href*='all']");
    private final ElementsCollection allPeoplesTable = $("#all").$$("tr");
    private final ElementsCollection friendsTable = $("#friends").$$("tr");
    private final ElementsCollection requestsTable = $("#requests").$$("tr");
    private final SelenideElement notFriendsText = $(By.xpath("//p[contains(text(), 'There are no users yet')]"));
    private final SelenideElement outcomingFriendRequestText = $(By.xpath("//span[contains(text(), 'Waiting...')]"));
    private final SelenideElement searchInput = $("input[placeholder='Search']");

    public SearchField getSearchField() {
        return searchField;
    }

    @Step("Check that the page is loaded")
    @Override
    @Nonnull
    public FriendsPage checkThatPageLoaded() {
        peopleTab.shouldBe(Condition.visible);
        allTab.shouldBe(Condition.visible);
        return this;
    }

    @Step("Set username: {0}")
    @Nonnull
    public FriendsPage searchInFriendsListByUsername(String username) {
        friendsList.click();
        setUsernameAndSearch(username);
        return this;
    }

    @Step("Set username: {0}")
    @Nonnull
    public FriendsPage searchInAllPeopleListByUsername(String username) {
        allPeoplesList.click();
        setUsernameAndSearch(username);
        return this;
    }

    @Step("Click friend list")
    @Nonnull
    public FriendsPage clickShowFriendsList() {
        friendsList.click();
        return this;
    }

    @Step("Click all people list")
    @Nonnull
    public FriendsPage clickShowAllPeopleList() {
        allPeoplesList.click();
        return this;
    }

    @Step("Accept invitation from user: {0}")
    @Nonnull
    public FriendsPage acceptInvitationFromUser(String username) {
        searchField.search(username);
        SelenideElement request = requestsTable.find(text(username));
        request.$(byText("Accept")).click();
        return this;
    }

    @Step("Decline invitation from user: {0}")
    @Nonnull
    public FriendsPage declineInvitationFromUser(String username) {
        searchField.search(username);
        SelenideElement request = requestsTable.find(text(username));
        request.$(byText("Decline")).click();
        popup.find(byText("Decline")).click();
        return this;
    }

    @Step("Check count friend: {0}")
    @Nonnull
    public FriendsPage checkFriendsCount(int count) {
        friendsTable.shouldHave(size(count));
        return this;
    }

    @Step("Check friend by username: {0}")
    @Nonnull
    public FriendsPage checkHaveFriendByUsername(String username) {
        friendsTable.shouldHave(textsInAnyOrder(username));
        return this;
    }

    @Step("Check count invitation: {0}")
    @Nonnull
    public FriendsPage checkInvitationCount(int count) {
        requestsTable.shouldHave(size(count));
        return this;
    }

    @Step("Check user not have a friend")
    @Nonnull
    public FriendsPage checkUserNotHaveFriend() {
        notFriendsText.should(visible);
        return this;
    }

    @Step("Check user have friend by name: {0}")
    @Nonnull
    public FriendsPage checkUserPresentInFriendTable(String friendName) {
        friendsTable.find(text(friendName)).should(visible);
        return this;
    }

    @Step("Check invitation from user: {0}")
    @Nonnull
    public FriendsPage checkInvitationInFriendFromUserByName(String username) {
        requestsTable.find(text(username)).should(visible);
        return this;
    }

    @Step("Check request to user: {0}")
    @Nonnull
    public FriendsPage checkOutgoingFriendInvitationRequestForUserByName(String username) {
        allPeoplesTable.find(text(username))
                .shouldHave(text("Waiting..."))
                .should(visible);
        return this;
    }

    @Step("Set username: {0}")
    @Nonnull
    private void setUsernameAndSearch(String username) {
        searchInput.setValue(username)
                .pressEnter();
    }
}
