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
import guru.qa.niffler.service.impl.AuthApiClient;
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

    @User(friends = 3)
    @ApiLogin
    @Test
    void testFriendShouldBeReturnedForUserWithFilerByUsername(UdUserJson user, @Token String token) {
        final UdUserJson expectedFriend = user.testData().friends().getFirst();
        final List<UdUserJson> result = gatewayApiClient.allFriends(token, expectedFriend.username());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());

        Assertions.assertEquals(expectedFriend.username(), result.getFirst().username());
    }

    @User(incomingRequests = 3)
    @ApiLogin
    @Test
    void testIncomingInvitationShouldBeReturnedForUserWithFilerByUsername(UdUserJson user, @Token String token) {
        final UdUserJson expectedFriend = user.testData().incomingRequest().getFirst();
        final List<UdUserJson> result = gatewayApiClient.allFriends(token, expectedFriend.username());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());

        Assertions.assertEquals(expectedFriend.username(), result.getFirst().username());
    }

    @User(friends = 1)
    @ApiLogin
    @Test
    void testFriendShouldBeRemoved(UdUserJson user, @Token String token) {
        final UdUserJson expectedFriend = user.testData().friends().getFirst();
        Assertions.assertNotNull(expectedFriend);
        gatewayApiClient.removeFriend(token, expectedFriend.username());
        final List<UdUserJson> result = gatewayApiClient.allFriends(token, expectedFriend.username());

        Assertions.assertEquals(0, result.size());
    }

    @User(incomingRequests = 1)
    @ApiLogin
    @Test
    void testIncomingInvitationShouldBeAccepted(UdUserJson user, @Token String token) {
        // пользователь из аннотации от которого исходит запрос в друзья
        final UdUserJson expectedFriend = user.testData().incomingRequest().getFirst();

        // пользователь от которого был принят запрос в друзья
        final UdUserJson userFromInvitation = gatewayApiClient.acceptInvitation(
                token, new FriendJson(expectedFriend.username())
        );

        // друг пользователя, который появился в списке друзей после принятия запроса в друзья
        final UdUserJson userFriendAfterAcceptInvitation = gatewayApiClient.allFriends(
                token, expectedFriend.username()
        ).getFirst();

        Assertions.assertNotNull(userFromInvitation);
        Assertions.assertEquals(userFromInvitation, userFriendAfterAcceptInvitation);
    }


    @User(incomingRequests = 1)
    @ApiLogin
    @Test
    void testIncomingInvitationShouldBeDeclined(UdUserJson user, @Token String token) {
        // пользователь из аннотации от которого исходит запрос в друзья
        final UdUserJson expectedFriend = user.testData().incomingRequest().getFirst();

        // пользователь от которого был отклонен запрос в друзья
        final UdUserJson userFromInvitation = gatewayApiClient.declineInvitation(
                token, new FriendJson(expectedFriend.username())
        );

        // список друзей после отклонения запроса в друзья
        final List<UdUserJson> userFriendAfterDeclinedInvitation = gatewayApiClient.allFriends(token, null);

        Assertions.assertNotNull(userFromInvitation);
        Assertions.assertEquals(0, userFriendAfterDeclinedInvitation.size());
    }

    @User(outcomingRequests = 1)
    @ApiLogin
    @Test
    void testAfterSendOutcomingInvitationShouldBeCreateIncomingAndOutcomingRequestAccordingly(UdUserJson user, @Token String token) {
        // пользователь из аннотации которому был отправлен запрос в друзья
        final UdUserJson expectedFriendFromAnno = user.testData().outcomingRequest().getFirst();

        final UdUserJson userWithFriendStateInviteSent = gatewayApiClient.allUsers(token, null)
                .stream()
                .filter(u -> u.friendState() == FriendState.INVITE_SENT)
                .toList()
                .getFirst();

        Assertions.assertEquals(expectedFriendFromAnno.username(), userWithFriendStateInviteSent.username());

        ThreadSafeCookieStore.INSTANCE.removeAll();
        String tokenUserWhomWasSentInvitation = new AuthApiClient().login(expectedFriendFromAnno.username(),
                expectedFriendFromAnno.testData().password());
        final UdUserJson userFromWhomWasReceivedInvitation = gatewayApiClient.allFriends(
                        "Bearer " + tokenUserWhomWasSentInvitation, null)
                .stream()
                .filter(u -> u.friendState() == FriendState.INVITE_RECEIVED)
                .toList()
                .getFirst();

        Assertions.assertEquals(user.username(), userFromWhomWasReceivedInvitation.username());
    }
}