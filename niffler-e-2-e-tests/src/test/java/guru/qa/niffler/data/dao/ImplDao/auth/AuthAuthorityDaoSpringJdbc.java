package guru.qa.niffler.data.dao.ImplDao.auth;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.auth.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.mapper.AuthAuthorityEntityRowMapper;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.DataSources.getDataSource;

public class AuthAuthorityDaoSpringJdbc implements AuthAuthorityDao {

    private static final Config CFG = Config.getInstance();
    private final String url = CFG.authJdbcUrl();

    @Override
    public void create(AuthAuthorityEntity... authority) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource(url));
        jdbcTemplate.batchUpdate(
                "INSERT INTO authority (user_id, authority) values (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, authority[i].getUser().getId());
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
        return new JdbcTemplate(getDataSource(url)).query(
                "SELECT * FROM authority WHERE id = ?",
                AuthAuthorityEntityRowMapper.instance,
                id
        );
    }

    @Override
    public List<AuthAuthorityEntity> findAllByUserId(UUID userId) {
        return new JdbcTemplate(getDataSource(url)).query(
                "SELECT * FROM authority WHERE user_id = ?",
                AuthAuthorityEntityRowMapper.instance,
                userId
        );
    }

    @Override
    public List<AuthAuthorityEntity> findAll() {
        return new JdbcTemplate(getDataSource(url)).query(
                "SELECT * FROM authority",
                AuthAuthorityEntityRowMapper.instance
        );
    }


    @Override
    public void remove(AuthAuthorityEntity user) {
        new JdbcTemplate(getDataSource(url)).update(
                "DELETE FROM authority WHERE id = ?",
                user.getId()
        );
    }
}
