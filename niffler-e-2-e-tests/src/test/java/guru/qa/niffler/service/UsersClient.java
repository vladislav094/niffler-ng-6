package guru.qa.niffler.service;

import guru.qa.niffler.model.UdUserJson;

import javax.annotation.Nonnull;

public interface UsersClient {
    @Nonnull
    UdUserJson createUser(String username, String password);

    void createIncomingInvitation(UdUserJson targetUser, int count);

    void createOutcomingInvitation(UdUserJson targetUser, int count);

    void createFriend(UdUserJson targetUser, int count);
}
