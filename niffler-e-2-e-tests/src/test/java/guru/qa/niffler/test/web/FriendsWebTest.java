package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.jupiter.annotations.meta.WebTest;
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.Test;

@WebTest
public class FriendsWebTest extends BaseWebTest {

    private final SelenideDriver driver = new SelenideDriver(SelenideUtils.chromeConfig);

    @User(friends = 1)
    @Test
    void friendShouldBePresentInFriendsTable(UdUserJson user) {
        final String expectedName = user.testData().friends().getFirst().username();

        driver.open(CFG.frontUrl(), LoginPage.class)
                .setUsername(user.username())
                .setPassword(user.testData().password())
                .clickLogInButton()
                .clickMenuButton()
                .clickFriendsButton()
                .searchInFriendsListByUsername(expectedName)
                .checkUserPresentInFriendTable(expectedName);
    }

    @User
    @Test
    void friendsTableShouldBeEmptyForNewUser(UdUserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .setUsername(user.username())
                .setPassword(user.testData().password())
                .clickLogInButton()
                .clickMenuButton()
                .clickFriendsButton()
                .checkUserNotHaveFriend();
    }

    @User(incomingRequests = 1)
    @Test
    void incomeInvitationBePresentInFriendsTable(UdUserJson user) {
        final String expectedName = user.testData().incomingRequest().getFirst().username();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .setUsername(user.username())
                .setPassword(user.testData().password())
                .clickLogInButton()
                .clickMenuButton()
                .clickFriendsButton()
                .searchInFriendsListByUsername(expectedName)
                .checkInvitationInFriendFromUserByName(expectedName);
    }

    @User(outcomingRequests = 1)
    @Test
    void outcomeInvitationBePresentInAppPeoplesTable(UdUserJson user) {
        final String expectedName = user.testData().outcomingRequest().getFirst().username();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .setUsername(user.username())
                .setPassword(user.testData().password())
                .clickLogInButton()
                .clickMenuButton()
                .clickFriendsButton()
                .searchInAllPeopleListByUsername(expectedName)
                .checkOutgoingFriendInvitationRequestForUserByName(expectedName);
    }

    @User(incomingRequests = 1)
    @Test
    void acceptFriendshipRequest(UdUserJson user) {
        final String requesterName = user.testData().incomingRequest().getFirst().username();

        FriendsPage friendsPage = Selenide.open(frontUrl, LoginPage.class)
                .login(user.username(), user.testData().password())
                .getHeader()
                .toFriendsPage()
                .acceptInvitationFromUser(requesterName);

        Selenide.refresh();

        friendsPage.checkFriendsCount(1)
                .checkHaveFriendByUsername(requesterName);
    }

    @User(incomingRequests = 1)
    @Test
    void declineFriendshipRequest(UdUserJson user) {
        final String requesterName = user.testData().incomingRequest().getFirst().username();

        FriendsPage friendsPage = Selenide.open(frontUrl, LoginPage.class)
                .login(user.username(), user.testData().password())
                .getHeader()
                .toFriendsPage()
                .declineInvitationFromUser(requesterName);

        Selenide.refresh();

        friendsPage.checkInvitationCount(0)
                .checkUserNotHaveFriend();
    }
}
