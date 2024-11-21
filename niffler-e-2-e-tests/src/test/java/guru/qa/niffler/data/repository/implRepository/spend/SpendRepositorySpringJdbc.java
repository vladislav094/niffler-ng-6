package guru.qa.niffler.data.repository.implRepository.spend;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.ImplDao.spend.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.ImplDao.spend.SpendDaoSpringJdbc;
import guru.qa.niffler.data.dao.spend.CategoryDao;
import guru.qa.niffler.data.dao.spend.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import guru.qa.niffler.data.repository.SpendRepository;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.jdbc.DataSources.getDataSource;

public class SpendRepositorySpringJdbc implements SpendRepository {

    private static final Config CFG = Config.getInstance();
    private final String url = CFG.spendJdbcUrl();
    private final SpendDao spendDao = new SpendDaoSpringJdbc();
    private final CategoryDao categoryDao = new CategoryDaoSpringJdbc();

    @Override
    public SpendEntity createSpend(SpendEntity spend) {
        return spendDao.create(spend);
    }

    @Override
    public SpendEntity updateSpend(SpendEntity spend) {
        return spendDao.update(spend);
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        return categoryDao.create(category);
    }

    public CategoryEntity updateCategory(CategoryEntity category) {
        return categoryDao.update(category);
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return categoryDao.findById(id);
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndName(String username, String categoryName) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource(url));
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM category WHERE username = ? AND name = ?",
                        CategoryEntityRowMapper.instance, username, categoryName
                )
        );
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        return spendDao.findById(id);
    }

    @Override
    public Optional<SpendEntity> findSpendByUsernameAndDescription(String username, String description) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource(url));
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM spend WHERE username = ? AND description = ?",
                        SpendEntityRowMapper.instance, username, description
                )
        );
    }

    @Override
    public void removeSpend(SpendEntity spend) {
        spendDao.remove(spend);
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        categoryDao.remove(category);
    }
}
