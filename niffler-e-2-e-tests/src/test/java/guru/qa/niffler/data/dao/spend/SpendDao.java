package guru.qa.niffler.data.dao.spend;

import guru.qa.niffler.data.entity.spend.SpendEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendDao {

    SpendEntity create(SpendEntity spend);

    SpendEntity update(SpendEntity spend);

    Optional<SpendEntity> findById(UUID id);

    List<SpendEntity> findAll();

    void remove(SpendEntity spend);
}