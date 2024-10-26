package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;

import java.util.Optional;
import java.util.UUID;

public interface SpendRepository {

    SpendEntity createSpend(SpendEntity spend);

    SpendEntity updateSpend(SpendEntity spend);

    CategoryEntity createCategory(CategoryEntity category);

    CategoryEntity updateCategory(CategoryEntity spend);

    Optional<CategoryEntity> findCategoryById(UUID id);

    Optional<CategoryEntity> findCategoryByUsernameAndName(String username, String spendName);

    Optional<SpendEntity> findSpendById(UUID id);

    Optional<SpendEntity> findSpendByUsernameAndDescription(String username, String description);

    void removeSpend(SpendEntity spend);

    void removeCategory(CategoryEntity spend);
}
