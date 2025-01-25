package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.FriendsPage;
import org.junit.jupiter.api.Test;

@WebTest
public class FriendsWebTest extends BaseWebTest {

    @User(friends = 1)
    @ApiLogin
    @Test
    void friendShouldBePresentInFriendsTable(UserJson user) {
        final String expectedName = user.testData().friends().getFirst().username();

        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .searchInFriendsListByUsername(expectedName)
                .checkUserPresentInFriendTable(expectedName);
    }

    @User
    @ApiLogin
    @Test
    void friendsTableShouldBeEmptyForNewUser() {
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .checkUserNotHaveFriend();
    }

    @User(incomingRequests = 1)
    @ApiLogin
    @Test
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        final String expectedName = user.testData().incomingRequest().getFirst().username();

        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .searchInFriendsListByUsername(expectedName)
                .checkInvitationInFriendFromUserByName(expectedName);
    }

    @User(outcomingRequests = 1)
    @ApiLogin
    @Test
    void outcomeInvitationBePresentInAppPeoplesTable(UserJson user) {
        final String expectedName = user.testData().outcomingRequest().getFirst().username();

        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .searchInAllPeopleListByUsername(expectedName)
                .checkOutgoingFriendInvitationRequestForUserByName(expectedName);
    }

    @User(incomingRequests = 1)
    @ApiLogin
    @Test
    void acceptFriendshipRequest(UserJson user) {
        final String requesterName = user.testData().incomingRequest().getFirst().username();

        FriendsPage friendsPage = Selenide.open(FriendsPage.URL, FriendsPage.class)
                .acceptInvitationFromUser(requesterName);

        Selenide.refresh();

        friendsPage.checkFriendsCount(1)
                .checkHaveFriendByUsername(requesterName);
    }

    @User(incomingRequests = 1)
    @ApiLogin
    @Test
    void declineFriendshipRequest(UserJson user) {
        final String requesterName = user.testData().incomingRequest().getFirst().username();

        FriendsPage friendsPage = Selenide.open(FriendsPage.URL, FriendsPage.class)
                .declineInvitationFromUser(requesterName);

        Selenide.refresh();

        friendsPage.checkInvitationCount(0)
                .checkUserNotHaveFriend();
    }
}
