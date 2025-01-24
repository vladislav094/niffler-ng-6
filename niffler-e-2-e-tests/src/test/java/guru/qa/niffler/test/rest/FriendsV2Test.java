package guru.qa.niffler.test.rest;

import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.rest.FriendshipStatus;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.model.rest.pageable.RestResponsePage;
import guru.qa.niffler.service.impl.AuthApiClient;
import guru.qa.niffler.service.impl.GatewayApiV2Client;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

@RestTest
public class FriendsV2Test {
    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();
    private final GatewayApiV2Client gatewayApiV2Client = new GatewayApiV2Client();

    private final String usernameAscSort = "username,ASC";

    @User(friends = 2, incomingRequests = 1)
    @ApiLogin
    @Test
    void allFriendsAndIncomeInvitationsShouldBeReturnedFromUser(UserJson user, @Token String token) {
        final List<UserJson> expectedFriends = user.testData().friends();
        final List<UserJson> expectedInvitations = user.testData().incomingRequest();
        final RestResponsePage<UserJson> result = gatewayApiV2Client.allFriends(
                token,
                null,
                0,
                usernameAscSort
        );
        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.getContent().size());
        final List<UserJson> friendsFromResponse = result.stream().filter(
                u -> u.friendshipStatus() == FriendshipStatus.FRIEND
        ).toList();
        final List<UserJson> invitationsFromResponse = result.stream().filter(
                u -> u.friendshipStatus() == FriendshipStatus.INVITE_RECEIVED
        ).toList();
        Assertions.assertEquals(2, friendsFromResponse.size());
        Assertions.assertEquals(1, invitationsFromResponse.size());
        Assertions.assertEquals(
                expectedInvitations.getFirst().username(),
                invitationsFromResponse.getFirst().username()
        );
        final UserJson firstUserFromRequest = friendsFromResponse.getFirst();
        final UserJson secondUserFromRequest = friendsFromResponse.getLast();
        Assertions.assertEquals(
                expectedFriends.getFirst().username(),
                firstUserFromRequest.username()
        );
        Assertions.assertEquals(
                expectedFriends.getLast().username(),
                secondUserFromRequest.username()
        );
    }

    @User(friends = 3)
    @ApiLogin
    @Test
    void testFriendShouldBeReturnedForUserWithFilerByUsername(UserJson user, @Token String token) {
        final UserJson expectedFriend = user.testData().friends().getFirst();
        final String filter = expectedFriend.username();
        final RestResponsePage<UserJson> result = gatewayApiV2Client.allFriends(
                token,
                filter,
                0,
                usernameAscSort);

        final List<UserJson> friendWasFoundByFilter = result.getContent()
                .stream()
                .filter(u -> u.username().equals(filter) && u.friendshipStatus() == FriendshipStatus.FRIEND)
                .toList();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, friendWasFoundByFilter.size());
        Assertions.assertEquals(filter, friendWasFoundByFilter.getFirst().username());
    }

    @User(outcomingRequests = 1)
    @ApiLogin
    @Test
    void testAfterSendOutcomingInvitationShouldBeCreateIncomingAndOutcomingRequestAccordingly(UserJson user, @Token String token) {
        // пользователь из аннотации которому был отправлен запрос в друзья
        final UserJson expectedFriendFromAnno = user.testData().outcomingRequest().getFirst();
        final RestResponsePage<UserJson> result = gatewayApiV2Client.allUsers(
                token,
                null,
                0,
                usernameAscSort);

        System.out.println(result.getContent().stream().toList());
        final UserJson userWithFriendStateInviteSent = result.getContent().stream().toList().getFirst();

        Assertions.assertEquals(expectedFriendFromAnno.username(), userWithFriendStateInviteSent.username());

        ThreadSafeCookieStore.INSTANCE.removeAll();
        String tokenUserWhomWasSentInvitation = "Bearer " + new AuthApiClient().login(expectedFriendFromAnno.username(),
                expectedFriendFromAnno.testData().password());

        final UserJson userFromWhomWasReceivedInvitation = gatewayApiV2Client.allFriends(
                        tokenUserWhomWasSentInvitation,
                        null,
                        0,
                        usernameAscSort)
                .getContent()
                .stream()
                .filter(u -> u.friendshipStatus() == FriendshipStatus.INVITE_RECEIVED)
                .toList()
                .getFirst();

        Assertions.assertEquals(user.username(), userFromWhomWasReceivedInvitation.username());
    }

    @User(friends = 2, incomingRequests = 1)
    @ApiLogin
    @Test
    void allFriendsAndIncomeInvitationsShouldBeReturnedFroUser(UserJson user, @Token String token) {
        final List<UserJson> expectedFriends = user.testData().friends();
        final List<UserJson> expectedInvitations = user.testData().incomingRequest();

        final RestResponsePage<UserJson> result = gatewayApiV2Client.allFriends(
                token,
                null,
                0,
                "username,ASC"
        );

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.getContent().size());

        final List<UserJson> friendsFromResponse = result.stream().filter(
                u -> u.friendshipStatus() == FriendshipStatus.FRIEND
        ).toList();

        final List<UserJson> invitationsFromResponse = result.stream().filter(
                u -> u.friendshipStatus() == FriendshipStatus.INVITE_RECEIVED
        ).toList();

        Assertions.assertEquals(2, friendsFromResponse.size());
        Assertions.assertEquals(1, invitationsFromResponse.size());

        Assertions.assertEquals(
                expectedInvitations.getFirst().username(),
                invitationsFromResponse.getFirst().username()
        );

        final UserJson firstUserFromRequest = friendsFromResponse.getFirst();
        final UserJson secondUserFromRequest = friendsFromResponse.getLast();

        Assertions.assertEquals(
                expectedFriends.getFirst().username(),
                firstUserFromRequest.username()
        );

        Assertions.assertEquals(
                expectedFriends.getLast().username(),
                secondUserFromRequest.username()
        );
    }
}
