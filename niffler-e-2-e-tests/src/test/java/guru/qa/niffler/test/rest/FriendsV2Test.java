package guru.qa.niffler.test.rest;

import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.jupiter.annotations.ApiLogin;
import guru.qa.niffler.jupiter.annotations.Token;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.jupiter.annotations.meta.RestTest;
import guru.qa.niffler.jupiter.extensions.ApiLoginExtension;
import guru.qa.niffler.model.FriendState;
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.model.rest.FriendJson;
import guru.qa.niffler.model.rest.pageable.RestResponsePage;
import guru.qa.niffler.service.impl.AuthApiClient;
import guru.qa.niffler.service.impl.GatewayApiClient;
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
    void allFriendsAndIncomeInvitationsShouldBeReturnedFromUser(UdUserJson user, @Token String token) {
        final List<UdUserJson> expectedFriends = user.testData().friends();
        final List<UdUserJson> expectedInvitations = user.testData().incomingRequest();
        final RestResponsePage<UdUserJson> result = gatewayApiV2Client.allFriends(
                token,
                null,
                0,
                usernameAscSort
        );
        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.getContent().size());
        final List<UdUserJson> friendsFromResponse = result.stream().filter(
                u -> u.friendState() == FriendState.FRIEND
        ).toList();
        final List<UdUserJson> invitationsFromResponse = result.stream().filter(
                u -> u.friendState() == FriendState.INVITE_RECEIVED
        ).toList();
        Assertions.assertEquals(2, friendsFromResponse.size());
        Assertions.assertEquals(1, invitationsFromResponse.size());
        Assertions.assertEquals(
                expectedInvitations.getFirst().username(),
                invitationsFromResponse.getFirst().username()
        );
        final UdUserJson firstUserFromRequest = friendsFromResponse.getFirst();
        final UdUserJson secondUserFromRequest = friendsFromResponse.getLast();
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
    void testFriendShouldBeReturnedForUserWithFilerByUsername(UdUserJson user, @Token String token) {
        final UdUserJson expectedFriend = user.testData().friends().getFirst();
        final String filter = expectedFriend.username();
        final RestResponsePage<UdUserJson> result = gatewayApiV2Client.allFriends(
                token,
                filter,
                0,
                usernameAscSort);

        final List<UdUserJson> friendWasFoundByFilter = result.getContent()
                .stream()
                .filter(u -> u.username().equals(filter) && u.friendState() == FriendState.FRIEND)
                .toList();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, friendWasFoundByFilter.size());
        Assertions.assertEquals(filter, friendWasFoundByFilter.getFirst().username());
    }

    @User(outcomingRequests = 1)
    @ApiLogin
    @Test
    void testAfterSendOutcomingInvitationShouldBeCreateIncomingAndOutcomingRequestAccordingly(UdUserJson user, @Token String token) {
        // пользователь из аннотации которому был отправлен запрос в друзья
        final UdUserJson expectedFriendFromAnno = user.testData().outcomingRequest().getFirst();
        final RestResponsePage<UdUserJson> result = gatewayApiV2Client.allUsers(
                token,
                null,
                0,
                usernameAscSort);

        System.out.println(result.getContent().stream().toList());
        final UdUserJson userWithFriendStateInviteSent = result.getContent().stream().toList().getFirst();

        Assertions.assertEquals(expectedFriendFromAnno.username(), userWithFriendStateInviteSent.username());

        ThreadSafeCookieStore.INSTANCE.removeAll();
        String tokenUserWhomWasSentInvitation = "Bearer " + new AuthApiClient().login(expectedFriendFromAnno.username(),
                expectedFriendFromAnno.testData().password());

        final UdUserJson userFromWhomWasReceivedInvitation = gatewayApiV2Client.allFriends(
                        tokenUserWhomWasSentInvitation,
                        null,
                        0,
                        usernameAscSort)
                .getContent()
                .stream()
                .filter(u -> u.friendState() == FriendState.INVITE_RECEIVED)
                .toList()
                .getFirst();

        Assertions.assertEquals(user.username(), userFromWhomWasReceivedInvitation.username());
    }
}
