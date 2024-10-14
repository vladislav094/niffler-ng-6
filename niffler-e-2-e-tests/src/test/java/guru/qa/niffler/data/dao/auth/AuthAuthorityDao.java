package guru.qa.niffler.data.dao.auth;

import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;

import java.util.List;
import java.util.UUID;

public interface AuthAuthorityDao {

    void create(AuthAuthorityEntity... authority);

    List<AuthAuthorityEntity> findById(UUID id);

    List<AuthAuthorityEntity> findByUserId(UUID userId);

    List<AuthAuthorityEntity> findAll();

    void delete(AuthAuthorityEntity user);
}
