package guru.qa.niffler.data.repository.implRepository.spend;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.ImplDao.spend.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.ImplDao.spend.SpendDaoJdbc;
import guru.qa.niffler.data.dao.spend.CategoryDao;
import guru.qa.niffler.data.dao.spend.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import guru.qa.niffler.data.repository.SpendRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

public class SpendRepositoryJdbc implements SpendRepository {

    private static final Config CFG = Config.getInstance();
    private final String url = CFG.spendJdbcUrl();
    private final SpendDao spendDao = new SpendDaoJdbc();
    private final CategoryDao categoryDao = new CategoryDaoJdbc();

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
    public Optional<CategoryEntity> findCategoryByUsernameAndName(String username, String spendName) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "SELECT * FROM category WHERE username = ? AND name = ?"
        )) {
            ps.setString(1, username);
            ps.setString(2, spendName);
            ps.execute();
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(
                            CategoryEntityRowMapper.instance.mapRow(rs, rs.getRow()));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        return spendDao.findById(id);
    }

    @Override
    public Optional<SpendEntity> findSpendByUsernameAndDescription(String username, String description) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "SELECT * FROM spend WHERE username = ? AND description = ?"
        )) {
            ps.setString(1, username);
            ps.setString(2, description);
            ps.execute();
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(
                            SpendEntityRowMapper.instance.mapRow(rs, rs.getRow()));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
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
