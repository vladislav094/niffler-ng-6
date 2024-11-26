package guru.qa.niffler.data.dao.ImplDao.spend;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.spend.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.jdbc.Connections.holder;

public class CategoryDaoJdbc implements CategoryDao {

    private static final Config CFG = Config.getInstance();
    private final String url = CFG.spendJdbcUrl();

    @Override
    public CategoryEntity create(CategoryEntity category) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "INSERT INTO category (username, name, archived) " +
                        "VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, category.getUsername());
            ps.setString(2, category.getName());
            ps.setBoolean(3, category.isArchived());
            ps.executeUpdate();
            UUID generateKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generateKey = rs.getObject("id", UUID.class);
                } else throw new SQLException("Can't find ID in ResultSet");
            }
            category.setId(generateKey);

            return category;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CategoryEntity update(CategoryEntity category) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "UPDATE category SET name = ?, username = ?, archived = ? WHERE id = ?"
        )) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getUsername());
            ps.setBoolean(3, category.isArchived());
            ps.setObject(4, category.getId());
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Failed to update category. Category not found.");
            }
            return category;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CategoryEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "SELECT * FROM category WHERE  id = ?"
        )) {
            ps.setObject(1, id);
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
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CategoryEntity> findAll() {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "SELECT * FROM category")) {
            ps.execute();
            List<CategoryEntity> result = new ArrayList<>();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    result.add(CategoryEntityRowMapper.instance.mapRow(rs, rs.getRow()));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(CategoryEntity category) {
        UUID categoryId = category.getId();
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "DELETE FROM category WHERE id = ?"
        )) {
            ps.setObject(1, categoryId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
