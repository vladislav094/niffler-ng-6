package guru.qa.niffler.data.dao.implementation.springJdbc;

import guru.qa.niffler.data.dao.auth.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
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
    public Optional<List<AuthAuthorityEntity>> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public Optional<List<AuthAuthorityEntity>> findByUserId(UUID userId) {
        return Optional.empty();
    }

    @Override
    public void update(UUID userId, String authority) {

    }

    @Override
    public void delete(AuthAuthorityEntity user) {

    }
}
