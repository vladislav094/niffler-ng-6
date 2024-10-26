package guru.qa.niffler.service;

import guru.qa.niffler.model.UdUserJson;

public interface UsersClient {

    UdUserJson createUser(String username, String password);

    void createIncomingInvitation(UdUserJson targetUser, int count);

    void createOutcomingInvitation(UdUserJson targetUser, int count);

    void createFriend(UdUserJson targetUser, int count);
}
