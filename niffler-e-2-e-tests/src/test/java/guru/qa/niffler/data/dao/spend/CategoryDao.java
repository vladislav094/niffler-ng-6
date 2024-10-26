package guru.qa.niffler.data.dao.spend;

import guru.qa.niffler.data.entity.spend.CategoryEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryDao {

    CategoryEntity create(CategoryEntity category);

    CategoryEntity update(CategoryEntity category);

    Optional<CategoryEntity> findById(UUID id);

    List<CategoryEntity> findAll();

    void remove(CategoryEntity category);
}
