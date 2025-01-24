package guru.qa.niffler.service;

import guru.qa.niffler.model.rest.UserJson;

import javax.annotation.Nonnull;

public interface UsersClient {
    @Nonnull
    UserJson createUser(String username, String password);

    void createIncomingInvitation(UserJson targetUser, int count);

    void createOutcomingInvitation(UserJson targetUser, int count);

    void createFriend(UserJson targetUser, int count);
}
