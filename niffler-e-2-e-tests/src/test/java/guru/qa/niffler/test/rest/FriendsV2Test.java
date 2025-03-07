package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.rest.FriendshipStatus;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.model.rest.pageable.RestResponsePage;
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

  @User(friends = 2, incomeInvitations = 1)
  @ApiLogin
  @Test
  void allFriendsAndIncomeInvitationsShouldBeReturnedFroUser(UserJson user, @Token String token) {
    final List<UserJson> expectedFriends = user.testData().friends();
    final List<UserJson> expectedInvitations = user.testData().incomeInvitations();

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
