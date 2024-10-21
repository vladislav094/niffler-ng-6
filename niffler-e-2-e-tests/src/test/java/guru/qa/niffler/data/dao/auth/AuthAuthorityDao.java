package guru.qa.niffler.data.dao.auth;

import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;

import java.util.List;
import java.util.UUID;

public interface AuthAuthorityDao {

    void create(AuthAuthorityEntity... authority);

    List<AuthAuthorityEntity> findById(UUID id);

    List<AuthAuthorityEntity> findAllByUserId(UUID userId);

    List<AuthAuthorityEntity> findAll();

    void remove(AuthAuthorityEntity user);
}
