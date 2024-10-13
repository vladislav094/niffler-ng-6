package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.daoImplementation.spend.CategoryDaoJdbc;
import guru.qa.niffler.data.daoImplementation.spend.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.daoImplementation.spend.SpendDaoJdbc;
import guru.qa.niffler.data.daoImplementation.spend.SpendingDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static guru.qa.niffler.data.Databases.getDataSource;

public class SpendDbClient {

    private static final Config CFG = Config.getInstance();

    // Spend
    public SpendJson createSpend(SpendJson spend) {
        return Databases.transaction(connection -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spend);
            if (spendEntity.getCategory().getId() == null) {
                CategoryEntity categoryEntity = new CategoryDaoJdbc(connection)
                        .create(spendEntity.getCategory());
                spendEntity.setCategory(categoryEntity);
            }
            return SpendJson.fromEntity(
                    new SpendDaoJdbc(connection).create(spendEntity)
            );
        }, CFG.spendJdbcUrl(), Connection.TRANSACTION_READ_COMMITTED);
    }

    public SpendJson getSpendById(SpendJson spendJson) {
        return Databases.transaction(connection -> {
            Optional<SpendEntity> entity = new SpendDaoJdbc(connection).findSpendById(spendJson.id());
            return entity.map(SpendJson::fromEntity).orElseThrow();
        }, CFG.spendJdbcUrl(), Connection.TRANSACTION_READ_COMMITTED);
    }

    public List<SpendJson> getAllSpendsByUsername(String username) {
        return Databases.transaction(connection -> {
            List<SpendEntity> spendEntityList = new SpendDaoJdbc(connection).findAllByUsername(username);
            return spendEntityList.stream()
                    .map(SpendJson::fromEntity)
                    .collect(Collectors.toList());
        }, CFG.spendJdbcUrl(), Connection.TRANSACTION_READ_COMMITTED);
    }

    public List<SpendJson> getAllSpends() {
        return Databases.transaction(connection -> {
            List<SpendEntity> spendEntityList = new SpendDaoJdbc(connection).findAll();
            List<CategoryEntity> categoryEntityList = new CategoryDaoJdbc(connection).findAll();
            spendEntityList.forEach(element -> {
                UUID categoryId = element.getCategory().getId();
                categoryEntityList.stream().filter(
                        category -> categoryId.equals(category.getId())
                ).findFirst().ifPresent(element::setCategory);
            });
            return spendEntityList.stream()
                    .map(SpendJson::fromEntity)
                    .collect(Collectors.toList());
        }, CFG.spendJdbcUrl(), Connection.TRANSACTION_READ_COMMITTED);
    }

    public void deleteSpend(SpendJson spend) {
        Databases.transaction(connection -> {
            new SpendDaoJdbc(connection).deleteSpend(SpendEntity.fromJson(spend));
        }, CFG.spendJdbcUrl(), Connection.TRANSACTION_READ_COMMITTED);
    }

    public List<SpendJson> getAllSpendSpringJdbc() {
        List<SpendEntity> spendEntityList = new SpendingDaoSpringJdbc(getDataSource(CFG.spendJdbcUrl()))
                .findAll();
        List<CategoryEntity> categoryEntityList = new CategoryDaoSpringJdbc(getDataSource(CFG.spendJdbcUrl()))
                .findAll();

        spendEntityList.forEach(element -> {
            UUID categoryId = element.getCategory().getId();
            categoryEntityList.stream()
                    .filter(category ->
                            categoryId.equals(category.getId()))
                    .findFirst()
                    .ifPresent(element::setCategory);
        });
        return spendEntityList.stream()
                .map(SpendJson::fromEntity)
                .toList();
    }

    // Category
    public CategoryJson createCategory(CategoryJson category) {
        return Databases.transaction(connection -> {
            return CategoryJson.fromEntity(new CategoryDaoJdbc(connection)
                    .create(CategoryEntity.fromJson(category)));
        }, CFG.spendJdbcUrl(), Connection.TRANSACTION_READ_COMMITTED);
    }

    public CategoryJson updateCategory(CategoryJson category) {
        return Databases.transaction(connection -> {
            return CategoryJson.fromEntity(new CategoryDaoJdbc(connection)
                    .updateCategory(CategoryEntity.fromJson(category)));
        }, CFG.spendJdbcUrl(), Connection.TRANSACTION_READ_COMMITTED);
    }

    public CategoryJson getCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return Databases.transaction(connection -> {
            Optional<CategoryEntity> categoryEntity = new CategoryDaoJdbc(connection)
                    .findCategoryByUsernameAndCategoryName(username, categoryName);
            return categoryEntity.map(CategoryJson::fromEntity).orElseThrow();
        }, CFG.spendJdbcUrl(), Connection.TRANSACTION_REPEATABLE_READ);
    }

    public List<CategoryJson> getAllCategoriesByUsername(String username) {
        return Databases.transaction(connection -> {
            List<CategoryEntity> listCategoryEntity = new CategoryDaoJdbc(connection).findAllByUsername(username);
            return listCategoryEntity.stream()
                    .map(CategoryJson::fromEntity)
                    .collect(Collectors.toList());
        }, CFG.spendJdbcUrl(), Connection.TRANSACTION_REPEATABLE_READ);
    }

    public List<CategoryJson> getCategoriesByUsernameSpringJdbc(String username) {
        List<CategoryEntity> categoryEntity = new CategoryDaoSpringJdbc(
                getDataSource(CFG.spendJdbcUrl()))
                .findAllByUsername(username);
        return categoryEntity.stream()
                .map(CategoryJson::fromEntity)
                .collect(Collectors.toList());
    }

    public List<CategoryJson> getAllCategory() {
        return Databases.transaction(connection -> {
            List<CategoryEntity> entities = new CategoryDaoJdbc(connection).findAll();
            return entities.stream()
                    .map(CategoryJson::fromEntity)
                    .toList();
        }, CFG.spendJdbcUrl(), Connection.TRANSACTION_READ_COMMITTED);
    }


    public void deleteCategory(CategoryJson category) {
        Databases.transaction(connection -> {
            new CategoryDaoJdbc(connection).deleteCategory(CategoryEntity.fromJson(category));
        }, CFG.spendJdbcUrl(), Connection.TRANSACTION_REPEATABLE_READ);
    }

    public List<CategoryJson> getAllCategorySpringJdbc() {
        return new CategoryDaoSpringJdbc(getDataSource(CFG.spendJdbcUrl()))
                .findAll()
                .stream()
                .map(CategoryJson::fromEntity)
                .toList();
    }
}
