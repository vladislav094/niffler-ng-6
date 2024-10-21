package guru.qa.niffler.data.repository.implRepository.spend;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.jpa.EntityManagers;
import guru.qa.niffler.data.repository.SpendRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Optional;
import java.util.UUID;

public class SpendRepositoryHibernate implements SpendRepository {

    private static final Config CFG = Config.getInstance();
    private final EntityManager entityManager = EntityManagers.em(CFG.spendJdbcUrl());

    @Override
    public SpendEntity createSpend(SpendEntity spend) {
        entityManager.joinTransaction();
        entityManager.persist(spend);
        return spend;
    }

    @Override
    public SpendEntity updateSpend(SpendEntity spend) {
        entityManager.joinTransaction();
        return entityManager.merge(spend);
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        entityManager.joinTransaction();
        entityManager.persist(category);
        return category;
    }

    public CategoryEntity updateCategory(CategoryEntity category) {
        entityManager.joinTransaction();
        return entityManager.merge(category);
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return Optional.ofNullable(entityManager.find(CategoryEntity.class, id));
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndName(String username, String name) {
        try {
            return Optional.of(
                    entityManager.createQuery(
                                    "select c from CategoryEntity c where c.username =: username and c.name =: name",
                                    CategoryEntity.class)
                            .setParameter("username", username)
                            .setParameter("name", name)
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        return Optional.ofNullable(entityManager.find(SpendEntity.class, id));
    }

    @Override
    public Optional<SpendEntity> findSpendByUsernameAndDescription(String username, String description) {
        try {
            return Optional.of(
                    entityManager.createQuery(
                                    "select c from SpendEntity c where c.username =: username and c.description =: description",
                                    SpendEntity.class)
                            .setParameter("username", username)
                            .setParameter("description", description)
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void removeSpend(SpendEntity spend) {
        entityManager.joinTransaction();
        entityManager.remove(entityManager.contains(spend) ? spend : entityManager.merge(spend));
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        entityManager.joinTransaction();
        entityManager.remove(entityManager.contains(category) ? category : entityManager.merge(category));
    }
}
