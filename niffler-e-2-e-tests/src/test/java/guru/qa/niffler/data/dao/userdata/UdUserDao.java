package guru.qa.niffler.data.dao.userdata;

import guru.qa.niffler.data.entity.userdata.UdUserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UdUserDao {

    UdUserEntity create(UdUserEntity user);

    UdUserEntity update(UdUserEntity user);

    Optional<UdUserEntity> findById(UUID id);

    Optional<UdUserEntity> findByUsername(String username);

    List<UdUserEntity> findAll();

    void remove(UdUserEntity user);
}
