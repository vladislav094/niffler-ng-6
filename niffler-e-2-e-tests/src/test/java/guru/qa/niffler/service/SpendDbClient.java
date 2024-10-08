package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.spend.CategoryDao;
import guru.qa.niffler.data.dao.spend.SpendDao;
import guru.qa.niffler.data.daoImplementation.spend.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.daoImplementation.spend.SpendingDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionsTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpendDbClient {

    private static final Config CFG = Config.getInstance();

    private final CategoryDao categoryDao = new CategoryDaoSpringJdbc();
    private final SpendDao spendDao = new SpendingDaoSpringJdbc();

    private final JdbcTransactionsTemplate jdbcTxTemplate = new JdbcTransactionsTemplate(
            CFG.spendJdbcUrl()
    );

    // Spend
    public SpendJson createSpend(SpendJson spend) {
        return jdbcTxTemplate.execute(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spend);
            if (spendEntity.getCategory().getId() == null) {
                CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
                spendEntity.setCategory(categoryEntity);
            }
            return SpendJson.fromEntity(spendDao.create(spendEntity));
        });
    }

    public SpendJson getSpendById(SpendJson spendJson) {
        return jdbcTxTemplate.execute(() -> {
            Optional<SpendEntity> entity = spendDao.findSpendById(spendJson.id());
            return entity.map(SpendJson::fromEntity).orElseThrow();
        });
    }

    public List<SpendJson> getAllSpendsByUsername(String username) {
        return jdbcTxTemplate.execute(() -> {
            List<SpendEntity> spendEntityList = spendDao.findAllByUsername(username);
            return spendEntityList.stream()
                    .map(SpendJson::fromEntity)
                    .collect(Collectors.toList());
        });
    }

    public List<SpendJson> getAllSpends() {
        return jdbcTxTemplate.execute(() -> {
            List<SpendEntity> spendEntityList = spendDao.findAll();
            List<CategoryEntity> categoryEntityList = categoryDao.findAll();

            spendEntityList.forEach(element -> {
                UUID categoryId = element.getCategory().getId();
                categoryEntityList.stream()
                        .filter(category -> categoryId.equals(category.getId()))
                        .findFirst().ifPresent(element::setCategory);
            });
            return spendEntityList.stream().map(SpendJson::fromEntity).toList();
        });
    }

    public void deleteSpend(SpendJson spend) {
        jdbcTxTemplate.execute(() ->
                spendDao.deleteSpend(SpendEntity.fromJson(spend))
        );
    }

    // Category
    public CategoryJson createCategory(CategoryJson category) {
        return jdbcTxTemplate.execute(() -> CategoryJson.fromEntity(categoryDao
                .create(CategoryEntity.fromJson(category))));
    }

    public CategoryJson updateCategory(CategoryJson category) {
        return jdbcTxTemplate.execute(() -> CategoryJson.fromEntity(
                categoryDao.updateCategory(CategoryEntity.fromJson(category))));
    }

    public CategoryJson getCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return jdbcTxTemplate.execute(() -> {
            Optional<CategoryEntity> categoryEntity = categoryDao
                    .findCategoryByUsernameAndCategoryName(username, categoryName);
            return categoryEntity.map(CategoryJson::fromEntity).orElseThrow();
        });
    }

    public List<CategoryJson> getAllCategoriesByUsername(String username) {
        return jdbcTxTemplate.execute(() -> {
            List<CategoryEntity> listCategoryEntity = categoryDao.findAllByUsername(username);
            return listCategoryEntity.stream()
                    .map(CategoryJson::fromEntity)
                    .collect(Collectors.toList());
        });
    }

    public List<CategoryJson> getAllCategory() {
        return jdbcTxTemplate.execute(() -> {
            List<CategoryEntity> entities = categoryDao.findAll();
            return entities.stream()
                    .map(CategoryJson::fromEntity)
                    .toList();
        });
    }

    public void deleteCategory(CategoryJson category) {
        jdbcTxTemplate.execute(() ->
                categoryDao.deleteCategory(CategoryEntity.fromJson(category)));
    }
}
