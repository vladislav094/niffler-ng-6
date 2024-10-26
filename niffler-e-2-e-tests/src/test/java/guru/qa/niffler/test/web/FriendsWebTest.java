package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.jupiter.annotations.meta.WebTest;
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class FriendsWebTest extends BaseWebTest {

    @User(friends = 1)
    @Test
    void friendShouldBePresentInFriendsTable(UdUserJson user) {
        String expectedName = user.testData().friends().getFirst().username();
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .setUsername(user.username())
                .setPassword(user.testData().password())
                .clickLogInButton()
                .clickAvatarButton()
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
                .clickAvatarButton()
                .clickFriendsButton()
                .checkUserNotHaveFriend();
    }

    @User(incomingRequests = 1)
    @Test
    void incomeInvitationBePresentInFriendsTable(UdUserJson user) {
        String expectedName = user.testData().incomingRequest().getFirst().username();
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .setUsername(user.username())
                .setPassword(user.testData().password())
                .clickLogInButton()
                .clickAvatarButton()
                .clickFriendsButton()
                .searchInFriendsListByUsername(expectedName)
                .checkInvitationInFriendFromUserByName(expectedName);
    }

    @User(outcomingRequests = 1)
    @Test
    void outcomeInvitationBePresentInAppPeoplesTable(UdUserJson user) {
        String expectedName = user.testData().outcomingRequest().getFirst().username();
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .setUsername(user.username())
                .setPassword(user.testData().password())
                .clickLogInButton()
                .clickAvatarButton()
                .clickFriendsButton()
                .searchInAllPeopleListByUsername(expectedName)
                .checkOutgoingFriendInvitationRequestForUserByName(expectedName);
    }
}
