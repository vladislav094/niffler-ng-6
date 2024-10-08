package guru.qa.niffler.data.daoImplementation.auth;

import guru.qa.niffler.data.dao.auth.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.mapper.AuthAuthorityEntityRowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private final Connection connection;

    public AuthAuthorityDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(AuthAuthorityEntity... authority) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO authority (user_id, authority) VALUES (?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            for (AuthAuthorityEntity a : authority) {
                ps.setObject(1, a.getUserId());
                ps.setString(2, a.getAuthority().name());
                ps.addBatch();
                ps.clearParameters();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthAuthorityEntity> findById(UUID id) {
        List<AuthAuthorityEntity> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM authority WHERE  id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AuthAuthorityEntity entity = new AuthAuthorityEntity();
                    entity.setId(rs.getObject("id", UUID.class));
                    entity.setUserId(rs.getObject("user_id", UUID.class));
                    entity.setAuthority(rs.getObject("authority", Authority.class));
                    result.add(entity);
                }
                return result;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthAuthorityEntity> findByUserId(UUID userId) {
        List<AuthAuthorityEntity> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM authority WHERE  user_id = ?"
        )) {
            ps.setObject(1, userId);
            ps.execute();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AuthAuthorityEntity entity = new AuthAuthorityEntity();
                    entity.setId(rs.getObject("id", UUID.class));
                    entity.setUserId(rs.getObject("user_id", UUID.class));
                    entity.setAuthority(rs.getObject("authority", Authority.class));
                    result.add(entity);
                }
                return result;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthAuthorityEntity> findAll() {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM authority")) {
            ps.execute();
            List<AuthAuthorityEntity> result = new ArrayList<>();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    result.add(AuthAuthorityEntityRowMapper.instance.mapRow(rs, rs.getRow()));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(AuthAuthorityEntity user) {
        UUID categoryId = user.getId();
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM authority WHERE id = ?"
        )) {
            ps.setObject(1, categoryId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}