package guru.qa.niffler.data.daoImplementation.auth;

import guru.qa.niffler.data.dao.auth.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.mapper.AuthAuthorityEntityRowMapper;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class AuthAuthorityDaoSpringJdbc implements AuthAuthorityDao {

    private DataSource dataSource;

    public AuthAuthorityDaoSpringJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void create(AuthAuthorityEntity... authority) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.batchUpdate(
                "INSERT INTO authority (user_id, authority) values (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, authority[i].getUserId());
                        ps.setString(2, authority[i].getAuthority().name());
                    }

                    @Override
                    public int getBatchSize() {
                        return authority.length;
                    }
                }
        );
    }

    @Override
    public List<AuthAuthorityEntity> findById(UUID id) {
        return new JdbcTemplate(dataSource).query(
                "SELECT * FROM authority WHERE id = ?",
                AuthAuthorityEntityRowMapper.instance,
                id
        );
    }

    @Override
    public List<AuthAuthorityEntity> findByUserId(UUID userId) {
        return new JdbcTemplate(dataSource).query(
                "SELECT * FROM authority WHERE user_id = ?",
                AuthAuthorityEntityRowMapper.instance,
                userId
        );
    }

    @Override
    public List<AuthAuthorityEntity> findAll() {
        return new JdbcTemplate(dataSource).query(
                "SELECT * FROM authority",
                AuthAuthorityEntityRowMapper.instance
        );
    }


    @Override
    public void delete(AuthAuthorityEntity user) {
        new JdbcTemplate(dataSource).update(
                "DELETE FROM authority WHERE id = ?",
                user.getId()
        );
    }
}
