package guru.qa.niffler.data.dao.ImplDao.auth;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.auth.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.mapper.AuthAuthorityEntityRowMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private static final Config CFG = Config.getInstance();
    private final String url = CFG.authJdbcUrl();


    @Override
    public void create(AuthAuthorityEntity... authority) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "INSERT INTO authority (user_id, authority) VALUES (?, ?)")) {
            for (AuthAuthorityEntity a : authority) {
                ps.setObject(1, a.getUser().getId());
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
        try (PreparedStatement ps =  holder(url).connection().prepareStatement(
                "SELECT * FROM authority WHERE  id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            List<AuthAuthorityEntity> result = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(AuthAuthorityEntityRowMapper.instance.mapRow(rs, rs.getRow()));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthAuthorityEntity> findAllByUserId(UUID userId) {
        try (PreparedStatement ps =  holder(url).connection().prepareStatement(
                "SELECT * FROM authority WHERE  user_id = ?"
        )) {
            ps.setObject(1, userId);
            ps.execute();
            List<AuthAuthorityEntity> result = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(AuthAuthorityEntityRowMapper.instance.mapRow(rs, rs.getRow()));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthAuthorityEntity> findAll() {
        try (PreparedStatement ps =  holder(url).connection().prepareStatement(
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
    public void remove(AuthAuthorityEntity user) {
        UUID categoryId = user.getId();
        try (PreparedStatement ps =  holder(url).connection().prepareStatement(
                "DELETE FROM authority WHERE id = ?"
        )) {
            ps.setObject(1, categoryId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
