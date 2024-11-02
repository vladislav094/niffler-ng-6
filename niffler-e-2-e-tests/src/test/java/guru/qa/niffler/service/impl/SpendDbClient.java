package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.implRepository.spend.SpendRepositoryHibernate;
import guru.qa.niffler.data.repository.implRepository.spend.SpendRepositoryJdbc;
import guru.qa.niffler.data.repository.implRepository.spend.SpendRepositorySpringJdbc;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();
    private final SpendRepository spendRepository = new SpendRepositoryJdbc();
//    private final JdbcTransactionsTemplate jdbcTxTemplate = new JdbcTransactionsTemplate(CFG.spendJdbcUrl());

    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    // SPEND TABLE
    @Override
    @Nonnull
    public SpendJson createSpend(SpendJson spend) {
        return requireNonNull(xaTxTemplate.execute(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spend);
            if (spendEntity.getCategory().getId() == null) {
                CategoryEntity categoryEntity = spendRepository.createCategory(spendEntity.getCategory());
                spendEntity.setCategory(categoryEntity);
            }
            return SpendJson.fromEntity(spendRepository.createSpend(spendEntity));
        }));
    }

    @Nonnull
    public SpendJson updateSpend(SpendJson spendJson) {
        return requireNonNull(xaTxTemplate.execute(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spendJson);
            return SpendJson.fromEntity(spendRepository.updateSpend(spendEntity));
        }));
    }

    public @Nullable SpendJson getSpendById(String id) {
        Optional<SpendEntity> entity = spendRepository.findSpendById(UUID.fromString(id));
        return entity.map(SpendJson::fromEntity).orElseThrow();
    }

    public @Nullable SpendJson getSpendByUsernameAndDescription(String username, String description) {
        Optional<SpendEntity> entity = spendRepository.findSpendByUsernameAndDescription(username, description);
        return entity.map(SpendJson::fromEntity).orElseThrow();
    }

    public void removeSpend(SpendJson spend) {
        xaTxTemplate.execute(() -> {
            spendRepository.removeSpend(SpendEntity.fromJson(spend));
            return null;
        });
    }

    // CATEGORY TABLE
    @Override
    @Nonnull
    public CategoryJson createCategory(CategoryJson category) {
        return requireNonNull(xaTxTemplate.execute(() ->
                CategoryJson.fromEntity(spendRepository.createCategory(
                        CategoryEntity.fromJson(category)))
        ));
    }

    @Override
    @Nonnull
    public CategoryJson updateCategory(CategoryJson category) {
        return requireNonNull(xaTxTemplate.execute(() -> {
            CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
            return CategoryJson.fromEntity(spendRepository.updateCategory(categoryEntity));
        }));
    }

    @Override
    public void removeCategory(CategoryJson category) {
        xaTxTemplate.execute(() -> {
            spendRepository.removeCategory(CategoryEntity.fromJson(category));
            return null;
        });
    }

    @Nullable
    public CategoryJson getCategoryById(String id) {
        Optional<CategoryEntity> entity = spendRepository.findCategoryById(UUID.fromString(id));
        return entity.map(CategoryJson::fromEntity).orElseThrow();
    }

    @Nullable
    public CategoryJson getCategoryByUsernameAndName(String username, String spendName) {
        Optional<CategoryEntity> entity = spendRepository.findCategoryByUsernameAndName(username, spendName);
        return entity.map(CategoryJson::fromEntity).orElseThrow();
    }
}
