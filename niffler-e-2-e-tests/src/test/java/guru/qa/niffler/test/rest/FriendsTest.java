package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotations.ApiLogin;
import guru.qa.niffler.jupiter.annotations.Token;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.jupiter.annotations.meta.RestTest;
import guru.qa.niffler.jupiter.extensions.ApiLoginExtension;
import guru.qa.niffler.model.FriendState;
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.service.impl.GatewayApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Comparator;
import java.util.List;

@RestTest
public class FriendsTest {
    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();
    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

    @User(friends = 2, incomingRequests = 1)
    @ApiLogin
    @Test
    void allFriendsAndIncomeInvitationsShouldBeReturnedFroUser(UdUserJson user, @Token String token) {
        final List<UdUserJson> expectedFriends = user.testData().friends()
                .stream()
                .sorted(Comparator.comparing(UdUserJson::username))
                .toList();

        final List<UdUserJson> expectedInvitations = user.testData().incomingRequest()
                .stream()
                .sorted(Comparator.comparing(UdUserJson::username))
                .toList();

        final List<UdUserJson> result = gatewayApiClient.allFriends(
                token,
                null
        );
        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());

        final List<UdUserJson> friendsFromResponse = result.stream()
                .filter(u -> u.friendState() == FriendState.FRIEND)
                .sorted(Comparator.comparing(UdUserJson::username))
                .toList();

        final List<UdUserJson> invitationsFromResponse = result.stream()
                .filter(u -> u.friendState() == FriendState.INVITE_RECEIVED)
                .sorted(Comparator.comparing(UdUserJson::username))
                .toList();

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

    @User(friends = 6, incomingRequests = 1)
    @ApiLogin
    @Test
    void allFriendsAndIncomeInvitationsShouldBeReturnedForUser(UdUserJson user, @Token String token) {
        final List<UdUserJson> expectedInvitations = user.testData().friends();
        final List<UdUserJson> result = gatewayApiClient.allFriends(token, null);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(7, result.size());

        // Список друзей
        final List<UdUserJson> friendsFromResponse = result.stream()
                .filter(u -> u.friendState() == FriendState.FRIEND)
                .toList();

        // Входящие заявки
        final List<UdUserJson> invitationsFromResponse = result.stream()
                .filter(u -> u.friendState() == FriendState.INVITE_RECEIVED)
                .toList();

        System.out.println(expectedInvitations);
        Assertions.assertEquals(6, friendsFromResponse.size());
        Assertions.assertEquals(1, invitationsFromResponse.size());
        Assertions.assertEquals(
                expectedInvitations,
                friendsFromResponse
        );
    }
}