package guru.qa.niffler.data.daoImplementation.spend;

import guru.qa.niffler.data.dao.spend.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoSpringJdbc implements CategoryDao {

    private DataSource dataSource;

    public CategoryDaoSpringJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public CategoryEntity create(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO category (name, username, archived) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, category.getName());
            ps.setString(2, category.getUsername());
            ps.setBoolean(3, category.isArchived());
            return ps;
        }, kh);
        UUID generatedKey = (UUID) kh.getKeys().get("id");
        category.setId(generatedKey);

        return category;
    }

    @Override
    public CategoryEntity updateCategory(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("UPDATE category SET name = ?, username = ?, archived = ? WHERE id",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, category.getName());
            ps.setString(2, category.getUsername());
            ps.setBoolean(3, category.isArchived());
            return ps;
        }, kh);
        return category;
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return Optional.ofNullable(
                new JdbcTemplate(dataSource).queryForObject(
                        "SELECT * FROM category WHERE id = ?",
                        CategoryEntityRowMapper.instance,
                        id
                )
        );
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return Optional.ofNullable(
                new JdbcTemplate(dataSource).queryForObject(
                        "SELECT * FROM category WHERE username = ? AND name = ?",
                        CategoryEntityRowMapper.instance,
                        username, categoryName
                )
        );
    }

    @Override
    public List<CategoryEntity> findAllByUsername(String username) {
        return new JdbcTemplate(dataSource).query(
                "SELECT * FROM category WHERE username = ?",
                CategoryEntityRowMapper.instance,
                username
        );
    }

    @Override
    public List<CategoryEntity> findAll() {
        return new JdbcTemplate(dataSource).query(
                "SELECT * FROM category",
                CategoryEntityRowMapper.instance
        );
    }

    @Override
    public void deleteCategory(CategoryEntity category) {
        new JdbcTemplate(dataSource).update(
                "DELETE FROM category WHERE id = ?",
                category.getId()
        );
    }
}
