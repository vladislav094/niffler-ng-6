package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.userdata.UdUserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UdUserRepository {

    UdUserEntity create(UdUserEntity user);

    Optional<UdUserEntity> findById(UUID id);

    Optional<UdUserEntity> findByUsername(String username);

    List<UdUserEntity> findAll();

    void delete(UdUserEntity user);

    void addIncomeInvitation(UdUserEntity requester, UdUserEntity addressee);

    void addOutcomeInvitation(UdUserEntity requester, UdUserEntity addressee);

    void addFriend(UdUserEntity requester, UdUserEntity addressee);

    void deleteFriendshipInvitation(UdUserEntity requester, UdUserEntity addressee);
}
