package guru.qa.niffler.data.dao.ImplDao.auth;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.auth.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;
import static guru.qa.niffler.data.tpl.DataSources.getDataSource;

public class AuthUserDaoSpringJdbc implements AuthUserDao {

    private static final Config CFG = Config.getInstance();
    private final String url = CFG.authJdbcUrl();

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource(url));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, " +
                            "credentials_non_expired) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.getEnabled());
            ps.setBoolean(4, user.getAccountNonExpired());
            ps.setBoolean(5, user.getAccountNonLocked());
            ps.setBoolean(6, user.getCredentialsNonExpired());

            return ps;
        }, kh);

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        user.setId(generatedKey);
        return user;
    }

    @Override
    public AuthUserEntity update(AuthUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource(url));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE \"user\" SET password = ?," +
                            " enabled = ?," +
                            "account_non_expired = ?," +
                            "account_non_locked = ?," +
                            "credentials_non_expired = ?" +
                            "WHERE id = ? AND username = ?",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getPassword());
            ps.setBoolean(2, user.getEnabled());
            ps.setBoolean(3, user.getAccountNonExpired());
            ps.setBoolean(4, user.getAccountNonLocked());
            ps.setBoolean(5, user.getCredentialsNonExpired());
            return ps;
        }, kh);
        return user;
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource(url));
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"user\" WHERE id = ?",
                        AuthUserEntityRowMapper.instance,
                        id
                )
        );
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource(url));
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"user\" WHERE username = ?",
                        AuthUserEntityRowMapper.instance,
                        username
                )
        );
    }

    @Override
    public List<AuthUserEntity> findAll() {
        return new JdbcTemplate(getDataSource(url)).query(
                "SELECT * FROM \"user\"",
                AuthUserEntityRowMapper.instance
        );
    }

    @Override
    public void remove(AuthUserEntity user) {
        new JdbcTemplate(getDataSource(url)).update(
                "DELETE FROM \"user\" WHERE id = ?",
                user.getId()
        );
    }
}