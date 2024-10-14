package guru.qa.niffler.data.dao.userdata;

import guru.qa.niffler.data.entity.userdata.UdUserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UdUserDao {

    UdUserEntity createUser(UdUserEntity user);

    Optional<UdUserEntity> findById(UUID id);

    Optional<UdUserEntity> findByUsername(String username);

    List<UdUserEntity> findAll();

    void delete(UdUserEntity user);
}
