package guru.qa.niffler.data.daoImplementation.auth;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.auth.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthUserDaoJdbc implements AuthUserDao {

    private static final Config CFG = Config.getInstance();

    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        try (PreparedStatement ps =  holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, " +
                        "credentials_non_expired) VALUES (?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, pe.encode(user.getPassword()));
            ps.setBoolean(3, user.isEnabled());
            ps.setBoolean(4, user.isAccountNonExpired());
            ps.setBoolean(5, user.isAccountNonLocked());
            ps.setBoolean(6, user.isCredentialsNonExpired());
            ps.executeUpdate();
            UUID generateKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generateKey = rs.getObject("id", UUID.class);
                } else throw new SQLException("Can't find ID in ResultSet");
            }
            user.setId(generateKey);

            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        try (PreparedStatement ps =  holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" WHERE  id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AuthUserEntity entity = new AuthUserEntity();
                    entity.setId(rs.getObject("id", UUID.class));
                    entity.setUsername(rs.getString("username"));
                    entity.setPassword(rs.getString("password"));
                    entity.setEnabled(rs.getBoolean("enabled"));
                    entity.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    entity.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    entity.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                    return Optional.of(entity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        try (PreparedStatement ps =  holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" WHERE username = ?"
        )) {
            ps.setString(1, username);
            ps.execute();
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AuthUserEntity entity = new AuthUserEntity();
                    entity.setId(rs.getObject("id", UUID.class));
                    entity.setUsername(rs.getString("username"));
                    entity.setPassword(rs.getString("password"));
                    entity.setEnabled(rs.getBoolean("enabled"));
                    entity.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    entity.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    entity.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                    return Optional.of(entity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(AuthUserEntity user) {
        UUID categoryId = user.getId();
        try (PreparedStatement ps =  holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "DELETE FROM \"user\" WHERE id = ?"
        )) {
            ps.setObject(1, categoryId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthUserEntity> findAll() {
        try (PreparedStatement ps =  holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\"")) {
            ps.execute();
            List<AuthUserEntity> result = new ArrayList<>();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    result.add(AuthUserEntityRowMapper.instance.mapRow(rs, rs.getRow()));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
