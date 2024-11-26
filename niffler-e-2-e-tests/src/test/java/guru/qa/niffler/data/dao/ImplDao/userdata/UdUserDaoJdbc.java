package guru.qa.niffler.data.dao.ImplDao.userdata;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.userdata.UdUserDao;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;
import guru.qa.niffler.data.mapper.UserdataUserEntityRowMapper;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.jdbc.Connections.holder;

public class UdUserDaoJdbc implements UdUserDao {

    private static final Config CFG = Config.getInstance();
    private final String url = CFG.userdataJdbcUrl();

    @Override
    public UdUserEntity create(UdUserEntity user) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullname());
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
    public UdUserEntity update(UdUserEntity user) {
        try (PreparedStatement usersPs = holder(url).connection().prepareStatement(
                """
                      UPDATE "user"
                        SET currency    = ?,
                            firstname   = ?,
                            surname     = ?,
                            photo       = ?,
                            photo_small = ?
                        WHERE id = ?
                    """);

             PreparedStatement friendsPs = holder(url).connection().prepareStatement(
                     """
                         INSERT INTO friendship (requester_id, addressee_id, status)
                         VALUES (?, ?, ?)
                         ON CONFLICT (requester_id, addressee_id)
                             DO UPDATE SET status = ?
                         """)
        ) {
            usersPs.setString(1, user.getCurrency().name());
            usersPs.setString(2, user.getFirstname());
            usersPs.setString(3, user.getSurname());
            usersPs.setBytes(4, user.getPhoto());
            usersPs.setBytes(5, user.getPhotoSmall());
            usersPs.setObject(6, user.getId());
            usersPs.executeUpdate();

            for (FriendshipEntity fe : user.getFriendshipRequests()) {
                friendsPs.setObject(1, user.getId());
                friendsPs.setObject(2, fe.getAddressee().getId());
                friendsPs.setString(3, fe.getStatus().name());
                friendsPs.setString(4, fe.getStatus().name());
                friendsPs.addBatch();
                friendsPs.clearParameters();
            }
            friendsPs.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public Optional<UdUserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "SELECT * FROM \"user\" WHERE  id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    UdUserEntity ue = new UdUserEntity();
                    ue.setId(rs.getObject("id", UUID.class));
                    ue.setUsername(rs.getString("username"));
                    ue.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    ue.setFirstname(rs.getString("firstname"));
                    ue.setSurname(rs.getString("surname"));
                    ue.setPhoto(rs.getBytes("photo"));
                    ue.setPhotoSmall(rs.getBytes("photo_small"));
                    ue.setFullname(rs.getString("full_name"));

                    return Optional.of(ue);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UdUserEntity> findByUsername(String username) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "SELECT * FROM \"user\" WHERE username = ?"
        )) {
            ps.setString(1, username);
            ps.execute();

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(
                            UserdataUserEntityRowMapper.instance.mapRow(rs, rs.getRow()));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UdUserEntity> findAll() {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "SELECT * FROM \"user\"")) {
            ps.execute();
            List<UdUserEntity> result = new ArrayList<>();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    result.add(UserdataUserEntityRowMapper.instance.mapRow(rs, rs.getRow()));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(UdUserEntity user) {
        UUID categoryId = user.getId();
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "DELETE FROM \"user\" WHERE id = ?"
        )) {
            ps.setObject(1, categoryId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
