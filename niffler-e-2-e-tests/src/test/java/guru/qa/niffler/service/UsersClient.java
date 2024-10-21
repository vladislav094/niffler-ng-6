package guru.qa.niffler.service;

import guru.qa.niffler.model.UdUserJson;

public interface UsersClient {

    UdUserJson createUser(String username, String password);

    void createIncomeInvitation(UdUserJson targetUser, int count);

    void crateOutcomeInvitation(UdUserJson targetUser, int count);

    void createFriend(UdUserJson targetUser, int count);
}
